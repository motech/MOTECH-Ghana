package org.motechproject.server.omod.web.model;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.openmrs.Location;

import java.io.Serializable;
import java.util.Set;

public class HealthFacility implements Serializable{
    private String name;
    private String region;
    private String district;
    private Integer id;
    private Set<Community> communities;

    public HealthFacility(Facility facility) {
        Location location = facility.getLocation();
        this.id = facility.getFacilityId();
        this.name = location.getName();
        this.region = location.getRegion();
        this.district = location.getCountyDistrict();
        this.communities = facility.getCommunities();
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public String getDistrict() {
        return district;
    }

    public Integer getId() {
        return id;
    }

    public Set<Community> getCommunities() {
        return communities;
    }
}
