package org.motechproject.server.model;

import java.util.Comparator;

public class FacilityComparator implements Comparator<Facility> {
    public int compare(Facility firstFacility, Facility secondFacility) {
        return firstFacility.name().compareToIgnoreCase(secondFacility.name());
    }
}
