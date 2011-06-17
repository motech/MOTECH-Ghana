package org.motechproject.server.omod.web.model;

import org.junit.Test;
import org.motechproject.server.model.ghana.Facility;
import org.motechproject.server.model.ghana.Country;
import org.motechproject.server.model.ghana.Region;
import org.openmrs.Location;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CountryTest {

    @Test
    public void countryShouldHaveProperRegions() {
        Country country = new Country("Ghana");
        ArrayList<Facility> facilities = new ArrayList<Facility>();
        facilities.add(facilityFor(location("a","a-d-1","a-s-1")));
        facilities.add(facilityFor(location("a","a-d-1","a-s-2")));
        facilities.add(facilityFor(location("a","a-d-1",null)));
        facilities.add(facilityFor(location("a","a-d-2","a-s-3")));
        facilities.add(facilityFor(location("b","b-d-1","b-s-1")));
        facilities.add(facilityFor(location("b","b-d-2","b-s-2")));
        country.withFacilities(facilities);
        assertTrue(country.hasRegion("a"));
        assertTrue(country.hasRegion("b"));
        Region regionA = country.getRegion("a");
        assertNotNull(regionA);
        assertTrue(regionA.hasDistrict("a-d-1"));
        assertTrue(regionA.hasDistrict("a-d-2"));
        Region regionB = country.getRegion("b");
        assertNotNull(regionB);
        assertTrue(regionB.hasDistrict("b-d-1"));
        assertTrue(regionB.hasDistrict("b-d-2"));
    }

    private Facility facilityFor(Location location){
        Facility facility = new Facility();
        facility.setLocation(location);
        return facility;
    }

    private Location location(String region, String district, String subDistrict) {
        Location location = new Location();
        location.setRegion(region);
        location.setCountyDistrict(district);
        location.setStateProvince(subDistrict);
        return location;
    }
}
