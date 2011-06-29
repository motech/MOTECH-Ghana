function Facility(facility) {
    var facility = facility;

    this.inLocation = function(regions, districts, subDistricts) {
        if (regions.length > 0) {
            return inSame(regions, facility.region) && notAssignedToAny(facility.district) && notAssignedToAny(facility.subDistrict);
        }
        if (districts.length > 0) {
            return inSame(districts, facility.district) && notAssignedToAny(facility.subDistrict);
        }
        return inSame(subDistricts, facility.subDistrict);
    };

    var inSame = function(location, facilityAttribute) {
        var present = false;
        $j(location).each(function(index, locationElement) {
            if (locationElement === facilityAttribute) {
                present = true;
            }
        });
        return present;
    };

    var notAssignedToAny = function(location) {
        return !(location && location.length > 0);
    };
}


function Country(country) {

    var getParentRow = function(ele) {
        return $j(ele).parents('tr');
    };

    var regionDropDown = new DynamicComboBox($j('#region'));
    var districtDropDown = new DynamicComboBox($j('#district'));
    var subDistrictDropDown = new DynamicComboBox($j('#subDistrict'));
    var facilityDropDown = new DynamicComboBox($j('#facility'));
    var districtRow = getParentRow('#district');
    var subDistrictRow = getParentRow('#subDistrict');
    var facilityRow = getParentRow('#facility');

    var populateRegions = function() {

        $j(country.regions).each(function(index, region) {
            if (region.name != 'Field Agent') {
                regionDropDown.appendOption(new Option(region.name, region.name));
                $j("#region").parent(".checkbox-list-wrapper").find(".checkbox-list-selector").append("<input type='checkbox' value='" + region.name + "'/>");
            }
        });
        $j('#region').attr('size', country.regions.length);
        hide(districtRow);
        hide(subDistrictRow);
        hide(facilityRow);
    };

    var onRegionChange = function(regionName, selected) {
        handleChange(regionName, getDistrictsInRegion, selected, districtDropDown, "district", onDistrictChange);
        toggleDistrictVisibility();
        toggleSubDistrictVisibility();
        facilitiesToBeShown(regionName, null, null, selected);
    };

    var onDistrictChange = function(districtName, selected) {
        handleChange(districtName, getSubDistrictsInDistrict, selected, subDistrictDropDown, "subDistrict", onSubDistrictChange);
        toggleSubDistrictVisibility();
        facilitiesToBeShown(null, districtName, null, selected);
    };

    var onSubDistrictChange = function(subDistrictName, selected) {
        facilitiesToBeShown(null, null, subDistrictName, selected);
    };

    var facilitiesToBeShown = function(regionName, districtName, subDistrictName, selected) {
        var regionNames = (regionName) ? [regionName] : [];
        var districtNames = (districtName) ? [districtName] : [];
        var subDistrictNames = (subDistrictName) ? [subDistrictName] : [];

        $j(country.regions).each(function(index, region) {
            $j(region.healthFacilities).each(function(index, facility) {
                var healthFacility = new Facility(facility);
                if (healthFacility.inLocation(regionNames, districtNames, subDistrictNames)) {
                    if (selected) {
                        facilityDropDown.appendOption(new Option(facility.name, facility.facilityId));
                        $j("#facility").parent(".checkbox-list-wrapper").find(".checkbox-list-selector").append("<input type='checkbox' value='" + facility.facilityId + "'/>");
                    } else {
                        facilityDropDown.removeOptionsWhen(function(val) {
                            var optElement = $j("#facility").find("option[value='" + val + "']");
                            var optIndex = $j("#facility").find('option').index(optElement);
                            var selectorDiv = $j(optElement).closest('.checkbox-list-wrapper').find(".checkbox-list-selector");
                            var checkbox = $j(selectorDiv).find('input[type="checkbox"]')[optIndex];
                            $j(checkbox).remove();
                            return true;
                        });
                    }

                }
            });
        });
        $j('#facility').attr('size', $j("#facility option").length);
        toggleFacilityVisibility();
        updatePhoneNumbers();
    };

    var handleChange = function(selection, childElementsQueryFunction, selected, childList, locationType, cascadeFunction){
        var childElements = childElementsQueryFunction(selection);

        if (selected) {
            $j(childElements).each(function(index, child) {
                childList.appendOption(new Option(child, child));
                $j("#"+ locationType +"").parent(".checkbox-list-wrapper").find(".checkbox-list-selector").append("<input type='checkbox' value='" + child + "'/>");
            });
        } else {
            childList.removeOptionsWhen(function(val) {
                return removeOptionsPredicate(val, childElements, $j("#"+ locationType +""), cascadeFunction);
            });
        }
        $j("#"+ locationType +"").attr('size', $j("#"+ locationType +" option").length);
    };

    var removeOptionsPredicate = function(val, optionsToBeRemoved, listElement, cascadeFunction) {
        var inArray = $j.inArray(val, optionsToBeRemoved);
        if (inArray !== -1) {
            cascadeFunction(val,false);
            var optElement = listElement.find("option[value='" + val + "']");
            var optIndex = listElement.find('option').index(optElement);
            var selectorDiv = $j(optElement).closest('.checkbox-list-wrapper').find(".checkbox-list-selector");
            var checkbox = $j(selectorDiv).find('input[type="checkbox"]')[optIndex];
            $j(checkbox).remove();
        }
        return ( inArray !== -1);
    };

    var updatePhoneNumbers = function() {
        revertPhoneNumbers();
        var phoneNumbers = [];
        var selectedFacilities = getSelectedFacilities();
        $j(selectedFacilities).each(function(index, selectedFacility) {
            var facilityPhoneNumbers = getPhoneNumbers(selectedFacility);
            if (facilityPhoneNumbers.length > 0) {
                phoneNumbers.push(facilityPhoneNumbers.join(','));
            }
        });
        $j('#recipients').val(phoneNumbers.join(','));
    };

    var getDistrictsInRegion = function(regionName) {
        var result = [];
        $j(country.regions).each(function(index, region) {
            if (region.name === regionName) {
                $j(region.districts).each(function(i, district) {
                    result.push(district.name);
                });
            }
        });
        return result;
    };

    var getSubDistrictsInDistrict = function(districtName) {
        var result = [];
        $j(country.regions).each(function(index, region) {
            $j(region.districts).each(function(i, district) {
                if (district.name === districtName) {
                    $j(district.subDistricts).each(function(i, subDistrict) {
                        result.push(subDistrict.name);
                    });
                }
            });
        });
        return result;
    };

    var getSelectedRegions = function() {
        var selectedRegionName = $j('#region').val();
        selectedRegionName = (selectedRegionName instanceof Array) ? selectedRegionName : [selectedRegionName];
        var selectedRegion = [];
        $j(country.regions).each(function(index, region) {
            if ($j.inArray(region.name, selectedRegionName) !== -1) {
                selectedRegion.push(region);
            }
        });
        return selectedRegion;
    };

    var getSelectedFacilities = function() {
        var regions = getSelectedRegions();
        var selectedFacilities = [];
        var facilityIds = $j('#facility').val();

        if (!facilityIds) {
            return selectedFacilities;
        }

        facilityIds = (facilityIds instanceof Array) ? facilityIds : [facilityIds];
        $j(regions).each(function(index, region) {
            $j(region.healthFacilities).each(function(index, facility) {
                if ($j.inArray(facility.facilityId.toString(), facilityIds) !== -1) {
                    selectedFacilities.push(facility);
                }
            });
        });
        return selectedFacilities;
    };

    var getPhoneNumbers = function(selectedFacility) {
        var result = [];
        if (selectedFacility.phoneNumber) {
            result.push(selectedFacility.phoneNumber);
        }
        if (selectedFacility.additionalPhoneNumber1) {
            result.push(selectedFacility.additionalPhoneNumber1);
        }
        if (selectedFacility.additionalPhoneNumber2) {
            result.push(selectedFacility.additionalPhoneNumber2);
        }
        if (selectedFacility.additionalPhoneNumber3) {
            result.push(selectedFacility.additionalPhoneNumber3);
        }
        return result;
    };

    var toggleDistrictVisibility = function() {

        if ($j("#district").find('option').length > 0) {
            show(districtRow);
        } else {
            hide(districtRow);
        }
    };

    var toggleSubDistrictVisibility = function() {

        if ($j("#subDistrict").find('option').length > 0) {
            show(subDistrictRow);
        } else {
            hide(subDistrictRow);
        }
    };

    var toggleFacilityVisibility = function() {

        if ($j("#facility").find('option').length > 0) {
            show(facilityRow);
        } else {
            hide(facilityRow);
        }
    };

    var revertPhoneNumbers = function() {
        $j('#recipients').val('');
    };

    var hide = function(element) {
        $j(element).hide();
    };

    var show = function(element) {
        $j(element).show();
    };

    var bindToOnChange = function(ele, changeHandler) {
        $j(ele).change(changeHandler);
    };

    var onCheckboxClicked = function(e) {
        var elementClicked = $j(e.target);
        var checkbox = elementClicked.filter('input')
        if (checkbox) {
            var selectList = elementClicked.closest('.checkbox-list-wrapper').find('.checkbox-list');
            var selectedValue = (elementClicked.is(':checked')) ? 'selected' : '';
            var checkboxValue = elementClicked.attr('value');
            var option = $j(selectList).find('option[value="'+ checkboxValue +'"]');
            $j(option).attr('selected', selectedValue);
            if (checkbox.closest(".checkbox-list-wrapper").find("#region").length !== 0) {
                onRegionChange($j(option).val(), checkbox.is(":checked"));
            } else if (checkbox.closest(".checkbox-list-wrapper").find("#district").length !== 0) {
                onDistrictChange($j(option).val(), checkbox.is(":checked"));
            } else if (checkbox.closest(".checkbox-list-wrapper").find("#subDistrict").length !== 0) {
                onSubDistrictChange($j(option).val(), checkbox.is(":checked"));
            } else {
                updatePhoneNumbers();
            }
        }
    };

    var validate = function(e){
        hideErrors();
        if(!$j("#recipients").val()){
            $j("#error-number-required").show();
            e.preventDefault();
        }
        if(!$j("textarea[name='content']").val()){
            $j("#error-message-required").show();
            e.preventDefault();
        }
        if(!(/([0-9]*[,]?)*[0-9]$/i.test($j("#recipients").val()))){
            $j("#error-number-invalid").show();
            e.preventDefault();
        }
    };

    var hideErrors = function(){
        $j(".error").hide();
    }


    var bind = function() {
        $j('.checkbox-list-selector').click(onCheckboxClicked);
        $j("#submitMessage").click(validate);
    };

    var bootstrap = function() {
        bind();
        populateRegions();
        hideErrors();
    };

    $j(bootstrap);
}