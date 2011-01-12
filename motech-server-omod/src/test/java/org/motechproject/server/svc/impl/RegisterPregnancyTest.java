package org.motechproject.server.svc.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.omod.AuthenticationService;
import org.motechproject.server.omod.ConceptEnum;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.*;
import org.openmrs.*;
import org.openmrs.Patient;
import org.openmrs.api.*;
import org.openmrs.test.BaseContextSensitiveTest;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RegisterPregnancyTest extends BaseContextSensitiveTest {

    private RegistrarBeanImpl registrarService;

    @Mock
    private ContextService contextService;
    @Mock
    private MotechService motechService;
    @Mock
    private ConceptService conceptService;
    @Mock
    private PersonService personService;
    @Mock
    private ObsService obsService;
    @Mock
    private LocationService locationService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private PatientService patientService;
    @Mock
    private EncounterService encounterService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        registrarService = new RegistrarBeanImpl();

        registrarService.setContextService(contextService);
        registrarService.setConceptService(conceptService);
        registrarService.setPersonService(personService);
        registrarService.setObsService(obsService);
        registrarService.setLocationService(locationService);
        registrarService.setAuthenticationService(authenticationService);
        registrarService.setPatientService(patientService);
        registrarService.setEncounterService(encounterService);
    }

    @Test
    public void shouldRegisterNewPregnancy() {
        Patient patient = new Patient(1);
        HashSet personAttributes = new HashSet(1);
        PersonAttribute phoneNumberAttribute = new PersonAttribute(new PersonAttributeType(8), "Phone Number");
        personAttributes.add(phoneNumberAttribute);
        patient.setAttributes(personAttributes);
        Date expectedDeliveryDate = getDateAfterMonths(4);
        Date observationDate = new Date();

        Concept pregnancyConcept = new Concept(1);
        Concept pregnancyStatusConcept = new Concept(2);
        Concept estimatedDateOfConfinement = new Concept(3);
        Obs pregnancyObservation = new Obs(1);

        when(contextService.getMotechService()).thenReturn(motechService);
        when(contextService.getConceptService()).thenReturn(conceptService);

        when(conceptService.getConcept(any(String.class))).thenReturn(pregnancyConcept);
        when(motechService.getActivePregnancies(patient.getPatientId(),pregnancyConcept , pregnancyStatusConcept ))
                .thenReturn(Arrays.asList(pregnancyObservation));
        when(personService.getPerson(patient.getPersonId())).thenReturn(patient);
        when(personService.getPersonAttributeTypeByName(any(String.class))).thenReturn(phoneNumberAttribute.getAttributeType());
        when(conceptService.getConcept(ConceptEnum.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT.name())).thenReturn(estimatedDateOfConfinement);
        Location ghana = new Location(2);
        ghana.setCountyDistrict(MotechConstants.LOCATION_KASSENA_NANKANA_WEST);
        when(locationService.getLocation("Ghana")).thenReturn(ghana);
        User staff = new User(1);
        when(authenticationService.getAuthenticatedUser()).thenReturn(staff);
        Encounter pregnancyVisitEncounter = new Encounter(1);
        when(encounterService.saveEncounter(pregnancyVisitEncounter)).thenReturn(pregnancyVisitEncounter) ;
        when(patientService.savePatient(patient)).thenReturn(patient);
        Community community = mock(Community.class);
        Facility facility = new Facility();
        facility.setLocation(ghana);
        when(community.getFacility()).thenReturn(facility);
        when(motechService.getCommunityByPatient(patient)).thenReturn(community);
        when(motechService.getActiveMessageProgramEnrollments(any(Integer.class),any(String.class),any(Integer.class),
                any(Long.class),any(Long.class),any(Integer.class))).thenReturn(new ArrayList<MessageProgramEnrollment>(0));

        registrarService.registerPregnancy(patient, expectedDeliveryDate,
                true, true, true,
                "012345678", ContactNumberType.PERSONAL,
                MediaType.TEXT, "English", DayOfWeek.MONDAY, observationDate,
                InterestReason.CURRENTLY_PREGNANT, HowLearned.MOTECH_FIELD_AGENT);

        verify(contextService,atLeastOnce()).getMotechService();
        verify(conceptService,atLeastOnce()).getConcept(any(String.class));
        verify(personService,atLeastOnce()).getPersonAttributeTypeByName(any(String.class));
        verify(encounterService,atLeastOnce()).getEncounterType(any(String.class));
        verify(encounterService).saveEncounter(any(Encounter.class));
        verify(locationService).getLocation("Ghana");
        verify(authenticationService).getAuthenticatedUser();
        verify(patientService).savePatient(patient);
        verify(motechService).getActivePregnancies(patient.getPatientId(),pregnancyConcept , pregnancyConcept );
        verify(obsService).saveObs(any(Obs.class),any(String.class));
        verify(motechService).getCommunityByPatient(patient);
        verify(motechService).saveMessageProgramEnrollment(any(MessageProgramEnrollment.class));
    }

    private Date getDateAfterMonths(int afterSoManyMonths) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, afterSoManyMonths);
        return calendar.getTime();
    }

}
