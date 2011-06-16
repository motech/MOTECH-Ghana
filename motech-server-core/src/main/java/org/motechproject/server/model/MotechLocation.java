package org.motechproject.server.model;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MotechLocation implements Serializable {

    private String name;
    private LocationType locationType;

    private List<MotechLocation> childLocations = new ArrayList<MotechLocation>();

    public MotechLocation(String name, LocationType locationType) {
        this.name = name;
        this.locationType = locationType;
    }

    public String getName() {
        return name;
    }

    public District addDistrict(District district) {
        throw new NotImplementedException();
    }

    public SubDistrict addSubDistrict(SubDistrict subDistrict) {
        throw new NotImplementedException();
    }

    public HealthFacility addHealthFacility(HealthFacility healthFacility) {
        throw new NotImplementedException();
    }

    public boolean hasName() {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public boolean equals(Object o) {
        MotechLocation other = (MotechLocation) o;
        return locationType.equals(other.locationType) && name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    protected MotechLocation addChildLocation(MotechLocation location) {
        if (!location.hasName()) return location;

        if (hasChildLocation(location)) {
            return getChildLocation(location);
        }
        childLocations.add(location);
        return location;
    }

    protected MotechLocation getChildLocation(MotechLocation location) {
        for (MotechLocation childLocation : childLocations) {
            if (childLocation.equals(location))
                return childLocation;
        }
        return null;
    }

    protected boolean hasChildLocation(MotechLocation location) {
        return childLocations.contains(location);
    }

    protected List<MotechLocation> getChildLocations() {
        return sort(childLocations);
    }

    private List<MotechLocation> sort(List<MotechLocation> list) {
        Collections.sort(list, new Comparator<MotechLocation>() {
            public int compare(MotechLocation o1, MotechLocation o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        return list;
    }

}
