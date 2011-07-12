package org.motechproject.server.model;

import org.motechproject.server.model.ghana.Community;
import org.motechproject.server.model.ghana.Facility;
import org.openmrs.Location;
import org.openmrs.PersonAddress;

public class PatientAddress {
    private Location location;
    private Community community;
    private String addressLine1;

    public PatientAddress near(Facility facility) {
        this.location = facility.getLocation();
        return this;
    }

    public PatientAddress in(Location location) {
        this.location = location;
        return this;
    }

    public PatientAddress in(Community community) {
        this.community = community;
        return this;
    }

    public PatientAddress at(String addressLine1) {
        this.addressLine1 = addressLine1;
        return this;
    }

    public PersonAddress build() {
        PersonAddress address = new PersonAddress();
        address.setAddress1(addressLine1);
        if (community != null) {
            address.setAddress2(community.getName());
        }
        address.setRegion(location.getRegion());
        address.setCountyDistrict(location.getCountyDistrict());
        address.setStateProvince(location.getStateProvince());
        address.setNeighborhoodCell(location.getName());
        return address;
    }

}
