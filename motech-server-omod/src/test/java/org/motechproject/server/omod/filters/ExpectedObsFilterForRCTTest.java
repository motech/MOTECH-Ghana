package org.motechproject.server.omod.filters;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.omod.builder.TestPatientBuilder;
import org.motechproject.server.omod.filters.condition.RCTCondition;
import org.motechproject.server.svc.RCTService;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

public class ExpectedObsFilterForRCTTest {


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
    public void shouldFilterOutExpectedObservations() {

        TestPatientBuilder patientBuilder = new TestPatientBuilder();

        Patient p0 = patientBuilder.withId(5717).create();
        Patient p1 = patientBuilder.withId(5718).create();
        Patient p2 = patientBuilder.withId(5719).create();
        Patient p3 = patientBuilder.withId(5720).create();


        Facility facilityInCentral = facilityInRegion("Central");
        Facility facilityInUpperEast = facilityInRegion("Upper East");


        ExpectedObs expectedObs0 = expectedObsFor(p0);
        ExpectedObs expectedObs1 = expectedObsFor(p1);
        ExpectedObs expectedObs2 = expectedObsFor(p2);
        ExpectedObs expectedObs3 = expectedObsFor(p3);

       List<ExpectedObs> obsList = new ArrayList<ExpectedObs>();
        obsList.add(expectedObs0);
        obsList.add(expectedObs1);
        obsList.add(expectedObs2);
        obsList.add(expectedObs3);

        expect(contextService.getMotechService()).andReturn(motechService).times(3);
        expect(motechService.facilityFor(p1)).andReturn(facilityInUpperEast);
        expect(motechService.facilityFor(p2)).andReturn(facilityInUpperEast);
        expect(motechService.facilityFor(p3)).andReturn(facilityInCentral);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p1)).andReturn(true);
        expect(rctService.isPatientRegisteredAndInTreatmentGroup(p2)).andReturn(false);

        replay(rctService, contextService, motechService);

        ExpectedObsFilterForRCT filterForRCT = new ExpectedObsFilterForRCT();
        RCTCondition condition = new RCTCondition();
        condition.setRctService(rctService);
        condition.setContextService(contextService);
        filterForRCT.setCondition(condition);
        List<ExpectedObs> filteredExpectedEncounters = filterForRCT.on(obsList);

        verify(rctService, contextService, motechService);

        assertTrue(filteredExpectedEncounters.size() == 3);
        assertTrue(filteredExpectedEncounters.contains(expectedObs0));
        assertTrue(filteredExpectedEncounters.contains(expectedObs1));
        assertTrue(filteredExpectedEncounters.contains(expectedObs3));
    }

    private Facility facilityInRegion(String region) {
        Facility facility = new Facility();
        Location location = new Location();
        location.setRegion(region);
        facility.setLocation(location);
        return facility;
    }

    private ExpectedObs expectedObsFor(Patient patient) {
        ExpectedObs expectedObs = new ExpectedObs();
        expectedObs.setId(new Long(patient.getId()));
        expectedObs.setPatient(patient);
        return expectedObs;
    }

}
