package org.motechproject.server.model.ghana;

import flexjson.JSON;
import org.openmrs.Location;

import java.io.Serializable;
import java.util.*;

public class HealthFacility implements Serializable {
    private String name;
    private String region;
    private String district;
    private String subDistrict;
    private Integer facilityId;
    private List<Community> communities = new ArrayList<Community>();

    public HealthFacility(Facility facility) {
        Location location = facility.getLocation();
        this.facilityId = facility.getFacilityId();
        this.name = location.getName();
        this.region = location.getRegion();
        this.district = location.getCountyDistrict();
        this.subDistrict = location.getStateProvince();
        addBasicCommunityInformation(facility.getCommunities());
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

    public Integer getFacilityId() {
        return facilityId;
    }

    public String getSubDistrict() {
        return subDistrict;
    }

    @JSON
    public List<Community> getCommunities() {
        Collections.sort(communities, new Comparator<Community>() {
            public int compare(Community o1, Community o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return communities;
    }

    private void addBasicCommunityInformation(Set<Community> communities) {
        if (communities == null) return;
        for (Community community : communities) {
            this.communities.add(community.basicInfo());
        }
    }
}
