package org.motechproject.server.omod.web.model;

import flexjson.JSON;
import flexjson.JSONSerializer;
import org.motechproject.server.model.Facility;
import org.openmrs.Location;

import java.util.ArrayList;
import java.util.List;

public class Country extends MotechLocation {

    public Country(String name) {
        super(name,LocationType.COUNTRY);
    }

    public void withFacilities(List<Facility> facilities) {
        for (Facility facility : facilities) {
            Location location = facility.getLocation();
            Region region = addRegion(location.getRegion());
            region.addDistrict(new District(location.getCountyDistrict())).addSubDistrict(new SubDistrict(location.getStateProvince()));
            region.addHealthFacility(new HealthFacility(facility));
        }
    }

    private Region addRegion(String region) {
        return (Region) addChildLocation(new Region(region));
    }


    public Boolean hasRegion(String region) {
        return hasChildLocation(new Region(region));
    }

    public Region getRegion(String region) {
        return hasRegion(region) ? (Region) getChildLocation(new Region(region)) : null;
    }

    @JSON
    public List<Region> getRegions(){
        List<Region> regions = new ArrayList<Region>();
        for (MotechLocation motechLocation : getChildLocations()) {
            regions.add((Region) motechLocation);
        }
        return  regions;
    }

    @Override
    public String toString() {
        return new JSONSerializer().prettyPrint(true).exclude("childLocations").serialize(this);
    }
}
