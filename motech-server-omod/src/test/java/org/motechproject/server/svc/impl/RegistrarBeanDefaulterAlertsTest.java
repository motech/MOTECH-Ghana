package org.motechproject.server.svc.impl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.*;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.filters.FilterChain;
import org.motechproject.server.service.StaffMessageSender;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.server.ws.WebServiceCareModelConverterImpl;
import org.motechproject.server.ws.WebServicePatientModelConverterImpl;
import org.motechproject.ws.Care;
import org.motechproject.ws.CareMessageGroupingStrategy;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.mobile.MessageService;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

public class RegistrarBeanDefaulterAlertsTest {

    private RegistrarBeanImpl registrarBean;
    private ContextService contextService;
    private MotechService motechService;
    private AdministrationService administrationService;
    private FilterChain encounterFilter;
    private FilterChain obsFilter;
    private MessageService messageService;
    private StaffMessageSender staffMessageSender;


    @Before
    public void setUp() {
        registrarBean = new RegistrarBeanImpl();

        contextService = createMock(ContextService.class);
        motechService = createMock(MotechService.class);
        administrationService = createMock(AdministrationService.class);
        encounterFilter = createMock(FilterChain.class);
        obsFilter = createMock(FilterChain.class);
        messageService = createMock(MessageService.class);

        WebServiceCareModelConverterImpl careModelConverter = new WebServiceCareModelConverterImpl();
        careModelConverter.setContextService(contextService);

        staffMessageSender = new StaffMessageSender(contextService, messageService, null);
        staffMessageSender.setExpectedEncountersFilter(encounterFilter);
        staffMessageSender.setExpectedObsFilter(obsFilter);
        staffMessageSender.setCareModelConverter(careModelConverter);


        registrarBean.setContextService(contextService);
        registrarBean.setAdministrationService(administrationService);
        registrarBean.setExpectedEncountersFilter(encounterFilter);
        registrarBean.setExpectedObsFilter(obsFilter);
        registrarBean.setMobileService(messageService);
        registrarBean.setStaffMessageSender(staffMessageSender);
    }


    @Test
    public void newDefaultersAlertsShouldBeCreatedForPatientsWhenNoneExists() {



        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(administrationService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(administrationService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);

        expect(encounterFilter.doFilter(expectedEncounters)).andReturn(expectedEncounters);
        expect(obsFilter.doFilter(expectedObservations)).andReturn(expectedObservations);


        expect(messageService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(MessageStatus.DELIVERED);
        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(null);
        expect(motechService.getCareConfigurationFor("ANC")).andReturn(careConfigurationForANC);
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn(null);
        expect(motechService.getCareConfigurationFor("TT1")).andReturn(careConfigurationForTT1);

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 1)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 1)));
        expectLastCall();

        WebServiceCareModelConverterImpl careModelConverter = new WebServiceCareModelConverterImpl();
        careModelConverter.setContextService(contextService);

        registrarBean.setStaffMessageSender(staffMessageSender);

        replay(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

        registrarBean.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

    }


    @Test
    public void defaultersAlertCountShouldBeIncreasedForExistingAlerts() {
        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(administrationService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(administrationService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);

        expect(encounterFilter.doFilter(expectedEncounters)).andReturn(expectedEncounters);
        expect(obsFilter.doFilter(expectedObservations)).andReturn(expectedObservations);


        expect(messageService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(MessageStatus.DELIVERED);

        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 1));
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn((new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 2, 1)));

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 2, 2)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 3, 2)));
        expectLastCall();

        replay(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

        registrarBean.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

    }


     @Test
    public void onlyAttemptCountShouldBeIncrementedIfAlertNotDelivered() {
        String[] careGroups = new String[]{"ANC","TT1"};
        List<Facility> facilities = facilitiesFor("Central Region");

        Patient patient = new Patient(1);
        ExpectedEncounter enc = new ExpectedEncounter();
        enc.setId(1L);
        enc.setPatient(patient);
        enc.setName("ANC");
        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc);

        ExpectedObs obs = new ExpectedObs();
        obs.setId(1L);
        obs.setPatient(patient);
        obs.setName("TT1");
        List<ExpectedObs> expectedObservations = new ArrayList<ExpectedObs>();
        expectedObservations.add(obs);

        WebServicePatientModelConverterImpl converter = new WebServicePatientModelConverterImpl();
        converter.setRegistrarBean(registrarBean);

        CareConfiguration careConfigurationForANC = new CareConfiguration(1L, "ANC", 3);
        CareConfiguration careConfigurationForTT1 = new CareConfiguration(2L, "TT1", 3);

        Date someDate = new Date();

        Integer maxResults = 1;
        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(contextService.getAdministrationService()).andReturn(administrationService).times(2);
        expect(motechService.getBlackoutSettings()).andReturn(null);
        expect(motechService.getAllFacilities()).andReturn(facilities);
        expect(administrationService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString()).times(2);
        expect(motechService.getExpectedEncounter(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedEncounters);
        expect(motechService.getExpectedObs(null, facilities.get(0), careGroups, null, null, someDate, someDate, maxResults)).andReturn(expectedObservations);
        expect(motechService.getCommunityByPatient(same(patient))).andReturn(null).times(2);

        expect(encounterFilter.doFilter(expectedEncounters)).andReturn(expectedEncounters);
        expect(obsFilter.doFilter(expectedObservations)).andReturn(expectedObservations);


        expect(messageService.sendDefaulterMessage(EasyMock.<String>isNull(), eq("0123456789"), EasyMock.<Care[]>anyObject(),
                EasyMock.<CareMessageGroupingStrategy>anyObject(), EasyMock.<MediaType>anyObject(), EasyMock.<Date>anyObject(), EasyMock.<Date>isNull()))
                .andReturn(MessageStatus.FAILED);

        expect(motechService.getDefaultedEncounterAlertFor(enc)).andReturn(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1, 2));
        expect(motechService.getDefaultedObsAlertFor(obs)).andReturn((new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 1)));

        motechService.saveOrUpdateDefaultedEncounterAlert(equalsExpectedEncounterAlert(new DefaultedExpectedEncounterAlert(enc, careConfigurationForANC, 1 , 3)));
        expectLastCall();

        motechService.saveOrUpdateDefaultedObsAlert(equalsExpectedObsAlert(new DefaultedExpectedObsAlert(obs, careConfigurationForTT1, 1, 2)));
        expectLastCall();

        replay(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

        registrarBean.sendStaffCareMessages(someDate, someDate, someDate, someDate, careGroups, false, true);

        verify(contextService, motechService, administrationService, encounterFilter, obsFilter, messageService);

    }

    private List<Facility> facilitiesFor(String... regions) {
        List<Facility> facilities = new ArrayList<Facility>();
        for (String region : regions) {
            Facility facility = new Facility();
            facility.setId(1L);
            facility.setPhoneNumber("0123456789");
            Location location = new Location();
            location.setRegion(region);
            facility.setLocation(location);
            facilities.add(facility);
        }
        return facilities;
    }

    private static DefaultedExpectedEncounterAlert equalsExpectedEncounterAlert(DefaultedExpectedEncounterAlert alert) {
        reportMatcher(new DefaultedExpectedEncounterAlertMatcher(alert));
        return alert;
    }

    private static DefaultedExpectedObsAlert equalsExpectedObsAlert(DefaultedExpectedObsAlert alert) {
        reportMatcher(new DefaultedExpectedObsAlertMatcher(alert));
        return alert;
    }


}
