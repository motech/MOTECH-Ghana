function Country(country) {
    var country = country;
    var regionDropDown = new DynamicComboBox($j('#region'));
    var districtDropDown = new DynamicComboBox($j('#district'));
    var subDistrictDropDown = new DynamicComboBox($j('#subDistrict'));
    var facilityDropDown = new DynamicComboBox($j('#facility'));
    var communityDropDown = new DynamicComboBox($j('#communityId'));

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
        facilitiesToBeShown();
    };

    var getSelectedDistrict = function() {
        var selectedDistrict;
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
    };

    var onSubDistrictSelection = function() {
        facilitiesToBeShown();
    };

    var facilitiesToBeShown = function() {
        facilityDropDown.revert();
        var region = getSelectedRegion();
        var facilities = region.healthFacilities;
        $j(facilities).each(function(index, facility) {
            if (facility.region == region.name) {
                facilityDropDown.appendOption(new Option(facility.name, facility.id));
            }
        });
    };

    var bind = function() {
        $j('#region').change(onRegionSelection);
        $j('#district').change(onDistrictSelection);
        $j('#subDistrict').change(onSubDistrictSelection);
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