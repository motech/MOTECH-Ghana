package org.motechproject.server.omod.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedEvent;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.builder.TestPatientBuilder;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.svc.impl.RegistrarBeanImpl;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;


public class ExpectedEventFilterForRCTTest {

    private RCTService rctService;
    private MotechService motechService;
    private ContextService contextService;
    private RegistrarBeanImpl registrarBean;


    @Before
    public void setUp() {
        rctService = createMock(RCTService.class);
        contextService = createMock(ContextService.class);
        motechService = createMock(MotechService.class);
        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
    }


    @Test
    public void shouldFilterOutPatientsRegisteredAsRCTControl() {

        TestPatientBuilder patientBuilder = new TestPatientBuilder();

        Patient p0 = patientBuilder.withId(5717).create();
        Patient p1 = patientBuilder.withId(5718).create();
        Patient p2 = patientBuilder.withId(5719).create();
        Patient p3 = patientBuilder.withId(5720).create();


        Facility facilityInCentral = facilityInRegion("Central");
        Facility facilityInUpperEast = facilityInRegion("Upper East");

        ExpectedEncounter expEnc0 = expectedEncounterFor(p0);
        ExpectedEncounter expEnc1 = expectedEncounterFor(p1);
        ExpectedEncounter expEnc2 = expectedEncounterFor(p2);
        ExpectedEncounter expEnc3 = expectedEncounterFor(p3);

        ExpectedObs expectedObs0 = expectedObsFor(p0);
        ExpectedObs expectedObs1 = expectedObsFor(p1);
        ExpectedObs expectedObs2 = expectedObsFor(p2);
        ExpectedObs expectedObs3 = expectedObsFor(p3);

        List<ExpectedEvent> expectedObservations = new ArrayList<ExpectedEvent>();
        expectedObservations.add(expectedObs0);
        expectedObservations.add(expectedObs1);
        expectedObservations.add(expectedObs2);
        expectedObservations.add(expectedObs3);

        List<ExpectedEvent> encounters = new ArrayList<ExpectedEvent>();
        encounters.add(expEnc0);
        encounters.add(expEnc1);
        encounters.add(expEnc2);
        encounters.add(expEnc3);

        expect(contextService.getMotechService()).andReturn(motechService).times(6);
        expect(motechService.facilityFor(p1)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p2)).andReturn(facilityInUpperEast).times(2);
        expect(motechService.facilityFor(p3)).andReturn(facilityInCentral).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p1)).andReturn(true).times(2);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p2)).andReturn(false).times(2);

        replay(rctService, contextService, motechService);

        ExpectedPatientEventFilterForRCT filterForRCT = new ExpectedPatientEventFilterForRCT();
        filterForRCT.setRctService(rctService);
        filterForRCT.setRegistrarBean(registrarBean);
        List<ExpectedEvent> filteredExpectedEncounters = filterForRCT.filter(encounters);
        List<ExpectedEvent> filteredExpectedObservations = filterForRCT.filter(expectedObservations);

        verify(rctService, contextService, motechService);

        assertTrue(filteredExpectedEncounters.size() == 3);
        assertTrue(filteredExpectedEncounters.contains(expEnc0));
        assertTrue(filteredExpectedEncounters.contains(expEnc1));
        assertTrue(filteredExpectedEncounters.contains(expEnc3));

        assertTrue(filteredExpectedObservations.size() == 3);
        assertTrue(filteredExpectedObservations.contains(expectedObs0));
        assertTrue(filteredExpectedObservations.contains(expectedObs1));
        assertTrue(filteredExpectedObservations.contains(expectedObs3));
    }

    private Facility facilityInRegion(String region) {
        Facility facility = new Facility();
        Location location = new Location();
        location.setRegion(region);
        facility.setLocation(location);
        return facility;
    }

    private ExpectedEncounter expectedEncounterFor(Patient patient) {
        ExpectedEncounter expectedEncounter = new ExpectedEncounter();
        expectedEncounter.setId(new Long(patient.getId()));
        expectedEncounter.setPatient(patient);
        return expectedEncounter;
    }

    private ExpectedObs expectedObsFor(Patient patient) {
        ExpectedObs expectedObs = new ExpectedObs();
        expectedObs.setId(new Long(patient.getId()));
        expectedObs.setPatient(patient);
        return expectedObs;
    }

}
