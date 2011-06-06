package org.motechproject.server.svc.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechModuleActivator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.filters.*;
import org.motechproject.server.omod.filters.condition.RCTCondition;
import org.motechproject.server.svc.RCTService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

public class RegistrarBeanDefaultAlertsTest {


    static MotechModuleActivator activator;


    private RegistrarBeanImpl registrarBean;

    private ContextService contextService;
    private MotechService motechService;
    private AdministrationService administrativeService;
    private RCTService rctService;


    @Before
    public void setUp() throws Exception {
        contextService = createMock(ContextService.class);
        motechService = createMock(MotechService.class);
        administrativeService = createMock(AdministrationService.class);
        rctService = createMock(RCTService.class);
        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
        registrarBean.setExpectedEncountersFilter(setUpExpectedEncountersFilter());
    }

    private FilterChain setUpExpectedEncountersFilter(){
        ExpectedEncounterFilterChain filterChain = new ExpectedEncounterFilterChain();
        List<Filter<ExpectedEncounter>> filters = new ArrayList<Filter<ExpectedEncounter>>();

        ExpectedEncounterFilterForRCT forRCT = new ExpectedEncounterFilterForRCT();
        RCTCondition condition = new RCTCondition();
        condition.setContextService(contextService);
        condition.setRctService(rctService);
        forRCT.setCondition(condition);

        filters.add(forRCT);

        ExpectedEncounterMaxAlertsFilter maxAlertsFilter = new ExpectedEncounterMaxAlertsFilter();
        maxAlertsFilter.setContextService(contextService);

        filters.add(maxAlertsFilter);

        filterChain.setFilters(filters);


        return filterChain;
    }

    @After
    public void tearDown() throws Exception {
        activator.shutdown();
    }

    @Test
    @Ignore
    //TODO : INCOMPLETE TEST
    public void defaulterAlertsTest() {

        List<Facility> facilities = facilities();
        String[] careGroups = {"ANC", "TT"};

        Patient p1 = new Patient(1);
        Patient p2 = new Patient(2);
        Patient p3 = new Patient(2);

        ExpectedEncounter enc1 = new ExpectedEncounter();
        enc1.setId(1L);
        enc1.setPatient(p1);

        ExpectedEncounter enc2 = new ExpectedEncounter();
        enc2.setId(2L);
        enc2.setPatient(p2);

        ExpectedEncounter enc3 = new ExpectedEncounter();
        enc3.setId(3L);
        enc3.setPatient(p3);


        List<ExpectedEncounter> expectedEncounters = new ArrayList<ExpectedEncounter>();
        expectedEncounters.add(enc1);
        expectedEncounters.add(enc2);
        expectedEncounters.add(enc3);

        expect(contextService.getMotechService()).andReturn(motechService).anyTimes();
        expect(motechService.getAllFacilities()).andReturn(facilities);
        Integer maxResults = 10;

        expect(administrativeService.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_MAX_QUERY_RESULTS)).andReturn(maxResults.toString());
        expect(motechService.getExpectedEncounter(null,facilities.get(0),careGroups,null,null,new Date(),new Date(),maxResults)).andReturn(expectedEncounters);

        registrarBean.sendStaffCareMessages(new Date(), new Date(), new Date(), new Date(), careGroups, false, true);
    }

    private List<Facility> facilities() {
        List<Facility> facilities = new ArrayList<Facility>();
        Facility facility = new Facility();
        facility.setPhoneNumber("0123456789");
        Location location = new Location();
        location.setRegion("Upper East");
        facility.setLocation(location);
        facilities.add(facility);
        return facilities;
    }


}
