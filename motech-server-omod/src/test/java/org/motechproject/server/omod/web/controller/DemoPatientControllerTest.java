package org.motechproject.server.omod.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.omod.web.localization.LocationController;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;

import java.util.Collections;

import static org.easymock.EasyMock.*;


public class DemoPatientControllerTest {

    private DemoPatientController demoPatientController;

    @Before
    public void setUp(){
       demoPatientController =  new DemoPatientController();
    }

    @Test
    public void testGetRegions() throws Exception {
        ContextService contextService = createMock(ContextService.class);
        MotechService motechService = createMock(MotechService.class);
        LocationController locationController = new LocationController();

        locationController.setContextService(contextService);
        demoPatientController.setContextService(contextService);
        demoPatientController.setLocationController(locationController);

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllRegions()).andReturn(Collections.<String>emptyList()).once();

        replay(contextService, motechService);
        demoPatientController.getRegions();
        verify(contextService, motechService);
    }

    @Test
    public void testGetDistricts() throws Exception {
        ContextService contextService = createMock(ContextService.class);
        MotechService motechService = createMock(MotechService.class);
        LocationController locationController = new LocationController();

        locationController.setContextService(contextService);
        demoPatientController.setContextService(contextService);
        demoPatientController.setLocationController(locationController);

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllDistricts()).andReturn(Collections.<String>emptyList()).once();

        replay(contextService, motechService);
        demoPatientController.getDistricts();
        verify(contextService, motechService);

    }

    @Test
    public void testGetCommunities() throws Exception {
        ContextService contextService = createMock(ContextService.class);
        MotechService motechService = createMock(MotechService.class);
        LocationController locationController = new LocationController();

        locationController.setContextService(contextService);
        demoPatientController.setContextService(contextService);
        demoPatientController.setLocationController(locationController);

        expect(contextService.getMotechService()).andReturn(motechService);
        expect(motechService.getAllCommunities(false)).andReturn(Collections.<Community>emptyList()).once();

        replay(contextService, motechService);
        demoPatientController.getCommunities();
        verify(contextService, motechService);

    }
}
