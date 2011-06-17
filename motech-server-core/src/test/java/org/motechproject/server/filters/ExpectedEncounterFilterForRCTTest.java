package org.motechproject.server.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.builder.TestPatientBuilder;
import org.motechproject.server.filters.condition.RCTCondition;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.svc.RCTService;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;


public class ExpectedEncounterFilterForRCTTest {

    private RCTService rctService;
    private MotechService motechService;
    private ContextService contextService;


    @Before
    public void setUp() {
        rctService = createMock(RCTService.class);
        contextService = createMock(ContextService.class);
        motechService = createMock(MotechService.class);
    }


    @Test
    public void shouldFilterOutExpectedEncounters() {

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


       List<ExpectedEncounter> encounters = new ArrayList<ExpectedEncounter>();
        encounters.add(expEnc0);
        encounters.add(expEnc1);
        encounters.add(expEnc2);
        encounters.add(expEnc3);

        expect(contextService.getMotechService()).andReturn(motechService).times(3);
        expect(motechService.facilityFor(p1)).andReturn(facilityInUpperEast);
        expect(motechService.facilityFor(p2)).andReturn(facilityInUpperEast);
        expect(motechService.facilityFor(p3)).andReturn(facilityInCentral);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p1)).andReturn(true);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p2)).andReturn(false);

        replay(rctService, contextService, motechService);

        ExpectedEncounterFilterForRCT filterForRCT = new ExpectedEncounterFilterForRCT();
        RCTCondition condition = new RCTCondition();
        condition.setRctService(rctService);
        condition.setContextService(contextService);
        filterForRCT.setCondition(condition);
        List<ExpectedEncounter> filteredExpectedEncounters = filterForRCT.on(encounters);

        verify(rctService, contextService, motechService);

        assertTrue(filteredExpectedEncounters.size() == 3);
        assertTrue(filteredExpectedEncounters.contains(expEnc0));
        assertTrue(filteredExpectedEncounters.contains(expEnc1));
        assertTrue(filteredExpectedEncounters.contains(expEnc3));
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

}
