function Facility(facility) {
    var facility = facility;
    this.hasSameLocation = function(region, district, subDistrict) {
        var inSameRegion = inSame(region, facility.region);
        var inSameDistrict = inSame(district, facility.district);
        var inSameSubDistrict = inSame(subDistrict, facility.subDistrict);
        return inSameRegion && inSameDistrict && inSameSubDistrict;
    };

    var inSame = function(location, facilityAttribute) {
        return ((location.name == "none") || notAssignedToAny(facilityAttribute)) ? true : (facilityAttribute == location.name);
    };

    var notAssignedToAny = function(location) {
        return !(location && location.length > 0);
    }
}

function Country(country) {
    var country = country;
    var regionDropDown = new DynamicComboBox($j('#region'));
    var districtDropDown = new DynamicComboBox($j('#district'));
    var subDistrictDropDown = new DynamicComboBox($j('#subDistrict'));
    var facilityDropDown = new DynamicComboBox($j('#facility'));
    var communityDropDown = new DynamicComboBox($j('#communityId'));
    var NONE = { name : 'none'};

    var getSelectedRegion = function() {
        var selectedRegionName = $j('#region').val();
        var selectedRegion;
        $j(country.regions).each(function(index, region) {
            if (selectedRegionName == region.name) {
                selectedRegion = region;
            }
        });
        return selectedRegion;
    };

    var districtsInSelectedRegion = function() {
        var region = getSelectedRegion();
        return $j(region.districts);
    };

    var onRegionSelection = function() {
        districtDropDown.revert();
        var districts = districtsInSelectedRegion();
        districts.each(function(index, district) {
            districtDropDown.appendOption(new Option(district.name, district.name));
        });
        toggleDistrictVisibility();
        toggleCommunityVisibility();
        facilitiesToBeShown();
    };

    var toggleCommunityVisibility = function() {
        var regionName = $j('#region').val();
        var communityRow = getParentRow('#communityId');
        if (regionName == 'Upper East') {
            show(communityRow);
            return;
        }
        hide(communityRow);
    };

    var toggleDistrictVisibility = function() {
        var districtRow = getParentRow('#district')
        var subDistrictRow = getParentRow('#subDistrict')
        var regionName = $j('#region').val();
        if (regionName == 'Upper East' || regionName == 'Central') {
            show(districtRow);
            show(subDistrictRow);
            return;
        }
        hide(districtRow);
        hide(subDistrictRow);
    }

    var getSelectedDistrict = function() {
        var selectedDistrict = NONE;
        var districtName = $j('#district').val();
        var districts = districtsInSelectedRegion();
        $j(districts).each(function(index, district) {
            if (districtName == district.name) {
                selectedDistrict = district;
            }
        });
        return selectedDistrict;
    };

    var subDistrictsInSelectedDistrict = function() {
        var selectedDistrict = getSelectedDistrict();
        return $j(selectedDistrict.subDistricts);
    };

    var onDistrictSelection = function() {
        subDistrictDropDown.revert();
        var subDistricts = subDistrictsInSelectedDistrict();
        subDistricts.each(function(index, subDistrict) {
            subDistrictDropDown.appendOption(new Option(subDistrict.name, subDistrict.name));
        });
        toggleSubDistrictVisibility();
        facilitiesToBeShown();
    };

    var toggleSubDistrictVisibility = function() {
        var selectedDistrictName = $j('#subDistrict');
        var subDistrictRow = getParentRow('#subDistrict');
        if (selectedDistrictName == 'Other') {
            hide(subDistrictRow);
            return;
        }
        show(subDistrictRow);
    };

    var getSelectedSubDistrict = function() {
        var district = getSelectedDistrict();
        var subDistrictName = $j('#subDistrict').val();
        var selectedSubDistrict = NONE;
        $j(district.subDistricts).each(function(index, subDistrict) {
            if (subDistrictName == subDistrict.name) {
                selectedSubDistrict = subDistrict;
            }
        });
        return selectedSubDistrict;
    };

    var onSubDistrictSelection = function() {
        facilitiesToBeShown();
    };

    var facilitiesToBeShown = function() {
        facilityDropDown.revert();
        var region = getSelectedRegion();
        var district = getSelectedDistrict();
        var subDistrict = getSelectedSubDistrict();

        $j(region.healthFacilities).each(function(index, facility) {
            var healthFacility = new Facility(facility);
            if (healthFacility.hasSameLocation(region, district, subDistrict)) {
                facilityDropDown.appendOption(new Option(facility.name, facility.id));
            }
        });
    };

    var getSelectedFacility = function() {
        var region = getSelectedRegion();
        var facilityId = $j('#facility').val();
        var selectedFacility;
        $j(region.healthFacilities).each(function(index, facility) {
            if (facilityId = facility.id) {
                selectedFacility = facility;
            }
        });
        return selectedFacility;
    }

    var onFacilitySelection = function() {
        communityDropDown.revert();
        var facility = getSelectedFacility();
        $j(facility.communities).each(function(index, community) {
            communityDropDown.appendOption(new Option(community.name, community.communityId));
        });
    };

    var hide = function(element) {
        $j(element).hide();
    };

    var show = function(element) {
        $j(element).show();
    };

    var getParentRow = function(ele) {
        return $j(ele).parents('tr');
    };

    var bind = function() {
        $j('#region').change(onRegionSelection);
        $j('#district').change(onDistrictSelection);
        $j('#subDistrict').change(onSubDistrictSelection);
        $j('#facility').change(onFacilitySelection);
    };

    var populateRegion = function() {
        var that = this;
        $j(country.regions).each(function(index, region) {
            regionDropDown.appendOption(new Option(region.name, region.name));
        });
    };

    var bootstrap = function() {
        bind();
        populateRegion();

    };

    $j(bootstrap);
}