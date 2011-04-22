package org.motechproject.server.omod.web.model;

import flexjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class Region extends MotechLocation {

    private List<HealthFacility> healthFacilities = new ArrayList<HealthFacility>();

    public Region(String name) {
        super(name,LocationType.REGION);
    }

    @Override
    public District addDistrict(District district) {
        return (District) addChildLocation(district);
    }

    @Override
    public HealthFacility addHealthFacility(HealthFacility healthFacility) {
        healthFacilities.add(healthFacility);
        return healthFacility;
    }

    public District getDistrict(String district){
        return (District) getChildLocation(new District(district));
    }

    public boolean hasDistrict(String district) {
        return hasChildLocation(new District(district));
    }

    @JSON
    public List<HealthFacility> getHealthFacilities() {
        return healthFacilities;
    }

    @JSON
    public List<District> getDistricts(){
        List<District> regions = new ArrayList<District>();
        for (MotechLocation motechLocation : getChildLocations()) {
            regions.add((District) motechLocation);
        }
        return  regions;
    }
}
