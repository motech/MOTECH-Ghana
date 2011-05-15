package org.motechproject.server.omod.web.model;

import flexjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class District extends MotechLocation {

    public District(String name) {
        super(name, LocationType.DISTRICT);
    }

    @Override
    public SubDistrict addSubDistrict(SubDistrict subDistrict) {
        return (SubDistrict) addChildLocation(subDistrict);
    }

    @JSON
    public List<SubDistrict> getSubDistricts() {
        List<SubDistrict> subDistricts = new ArrayList<SubDistrict>();
        for (MotechLocation motechLocation : getChildLocations()) {
            subDistricts.add((SubDistrict) motechLocation);
        }
        return subDistricts;
    }
}
