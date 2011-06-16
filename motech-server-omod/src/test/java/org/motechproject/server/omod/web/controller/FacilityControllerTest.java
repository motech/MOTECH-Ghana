package org.motechproject.server.omod.web.controller;

import org.junit.*;
import org.mockito.Mock;
import org.motechproject.server.model.Facility;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.motechproject.server.omod.web.model.WebFacility;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FacilityControllerTest {
    private FacilityController controller;
    @Mock
    private ContextService contextService;
    @Mock
    private LocationService locationService;
    @Mock
    private MotechService motechService;
    @Mock
    private WebFacility webFacility;
    @Mock
    private Errors errors;
    @Mock
    private Facility facility;
    @Mock
    private Location location;
    @Mock
    private RegistrarBean registrarBean;

    @Before
    public void setup() {
        initMocks(this);
        controller = new FacilityController(contextService);
        when(contextService.getLocationService()).thenReturn(locationService);
        when(contextService.getMotechService()).thenReturn(motechService);
        when(contextService.getRegistrarBean()).thenReturn(registrarBean);
    }

    @Test
    public void shouldPopulateWithLocationDataWhenShowingNewFacilityForm() {
        Location l1 = getLocation("C1", "R1", "D1", "P1");
        Location l2 = getLocation("C1", "R1", "D2", "P2");
        Location l3 = getLocation("C1", "R2", "D3", "P3");
        Location l4 = getLocation("C1", "R2", "D4", "P4");
        when(locationService.getAllLocations()).thenReturn(Arrays.asList(l1, l2, l3, l4));

        ModelMap modelMap = new ModelMap();
        String path = controller.viewAddFacilityForm(modelMap);

        assertEquals("/module/motechmodule/addfacility", path);
        Set<String> countries = (Set<String>) modelMap.get("countries");
        assertTrue(countries.contains("C1"));

        Map<String, TreeSet<String>> m1 = (Map<String, TreeSet<String>>) modelMap.get("regions");
        TreeSet<String> regions = m1.get("C1");
        assertTrue(regions.containsAll(Arrays.asList("R1", "R2")));

        Map<String, TreeSet<String>> m2 = (Map<String, TreeSet<String>>) modelMap.get("districts");
        TreeSet<String> districts = m2.get("R1");
        assertTrue(districts.containsAll(Arrays.asList("D1", "D2")));

        Map<String, TreeSet<String>> m3 = (Map<String, TreeSet<String>>) modelMap.get("provinces");
        TreeSet<String> provinces = m3.get("D1");
        assertTrue(provinces.containsAll(Arrays.asList("P1")));

        verify(contextService).getLocationService();
        verify(locationService).getAllLocations();
    }

    @Test
    public void shouldFetchLocationDataInCaseOfDuplicateLocationError() {
        ModelMap modelMap = new ModelMap();
        when(webFacility.getFacility()).thenReturn(facility);
        when(webFacility.getName()).thenReturn("name");
        when(facility.getLocation()).thenReturn(location);
        when(motechService.getLocationByName("name")).thenReturn(location);
        when(errors.hasErrors()).thenReturn(true);

        String path = controller.submitAddFacility(webFacility, errors, modelMap, null);

        assertEquals(path,"/module/motechmodule/addfacility");
        verify(errors).rejectValue("name","motechmodule.Facility.duplicate.location");
        verify(locationService,never()).saveLocation(location);
        verify(registrarBean,never()).saveNewFacility(facility);
        verify(locationService).getAllLocations();
    }

    @Test
    public void shouldSaveNewLocationForNoErrors() {
        ModelMap modelMap = new ModelMap();
        when(webFacility.getFacility()).thenReturn(facility);
        when(webFacility.getName()).thenReturn("name");
        when(facility.getLocation()).thenReturn(location);
        when(motechService.getLocationByName("name")).thenReturn(null);
        when(errors.hasErrors()).thenReturn(false);

        String path = controller.submitAddFacility(webFacility, errors, modelMap, null);

        assertEquals(path,"redirect:/module/motechmodule/facility.form");
        verify(locationService).saveLocation(location);
        verify(registrarBean).saveNewFacility(facility);
        verify(locationService,never()).getAllLocations();
    }

    private Location getLocation(String country, String region, String district, String province) {
        Location location = new Location();
        location.setCountry(country);
        location.setRegion(region);
        location.setStateProvince(province);
        location.setCountyDistrict(district);
        return location;
    }
}
