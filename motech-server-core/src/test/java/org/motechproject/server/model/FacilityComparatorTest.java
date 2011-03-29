package org.motechproject.server.model;

import org.junit.Test;
import org.openmrs.Location;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class FacilityComparatorTest {

    @Test
    public void shouldSortFacilitiesAccrodingToAlphabeticalOrderOfLocationNames() {
        Facility navio = facilityWithLocationName("Navio CHPS");
        Facility kyutu = facilityWithLocationName("Kyutu");
        Facility chiuru = facilityWithLocationName("Chiuru");

        List facilities = new ArrayList();
        facilities.add(navio);
        facilities.add(kyutu);
        facilities.add(chiuru);
        Collections.sort(facilities,new FacilityComparator());
        assertEquals(chiuru,facilities.get(0)) ;
        assertEquals(kyutu,facilities.get(1)) ;
        assertEquals(navio,facilities.get(2)) ;
    }

    private Facility facilityWithLocationName(String name) {
        Facility facility = new Facility();
        Location location = new Location();
        location.setName(name);
        facility.setLocation(location);
        return facility;
    }
}
