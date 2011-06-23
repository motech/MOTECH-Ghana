var $j = jQuery.noConflict();


function addFacility() {
    var countryDropDown = new DynamicComboBox($j('#country'));
    var regionsDropDown = new DynamicComboBox($j('#region'));
    var districtsDropDown = new DynamicComboBox($j('#countyDistrict'));
    var provincesDropDown = new DynamicComboBox($j('#stateProvince'));

    var onLoad = function() {
        regionsDropDown.disable();
        districtsDropDown.disable();
        provincesDropDown.disable();
        $j("#phoneNumber").val('');
        $j("#location_data").hide();
        $j('span[title="err_span"]').addClass('hideError');
        $j("#countries_data").children("li").each(function() {
            var item = $j(this).html();
            if (item == '')return;
            countryDropDown.appendOption(new Option(item, item));
        });

        for (var i = 1; i <= 3; i++) {
            initializeAdditionalPhoneNumber(i);
        }
    };

    var initializeAdditionalPhoneNumber = function(i) {
        hidePhoneNumberRow(i);
        $j("#additional_phoneNumber" + i + "_err").hide();
    }

    var bind = function() {
        $j("#country").change(onCountryChange);
        $j("#region").change(onRegionChange);
        $j("#countyDistrict").change(onDistrictChange);
        $j("#submit_facility").click(onSubmit);
        $j("#additionalPhoneNumber1-link").bind('click', {index: 1}, enableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber2-link").bind('click', enableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber3-link").bind('click', enableAdditionalPhoneNumber);

        $j("#additionalPhoneNumber1-del").bind('click', {index: 1}, disableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber2-del").bind('click', {index: 2}, disableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber3-del").bind('click', {index: 3}, disableAdditionalPhoneNumber);
    };

    var disableAdditionalPhoneNumber = function(e) {
        var serialNumber = e.data.index;
        var shouldDelete = confirm("You are deleting a phone number. Do you wish to continue?");
        if (shouldDelete) {
            maintainSerialNumber(serialNumber);
        }
    }

    var maintainSerialNumber = function(serialNumberOfDeletedPhoneNumber) {
        var visiblePhoneNumbers = $j(".additional-phone-number-row").filter(":visible");
        for (var i = serialNumberOfDeletedPhoneNumber - 1, j = i + 1; j < visiblePhoneNumbers.length; i++,j++) {
            $j(visiblePhoneNumbers[i]).find(".error").hide();
            var nextValue = $j(visiblePhoneNumbers[j]).find(".additional-phone-number").val();
            $j(visiblePhoneNumbers[i]).find(".additional-phone-number").val(nextValue);
        }
        var lastVisiblePhoneNumberIndex = j;
        hidePhoneNumberRow(lastVisiblePhoneNumberIndex);
    }

    var hidePhoneNumberRow = function(serialNumber) {
        $j("#additionalPhoneNumber" + serialNumber).val("");
        $j("#additionalPhoneNumber" + serialNumber + "-link").show();
        $j("#additionalPhoneNumber" + serialNumber + "-row").hide();
    }

    var enableAdditionalPhoneNumber = function(e) {
        var serialNumber = e.data.index;
        $j("#additionalPhoneNumber" + serialNumber + "-link").hide();
        $j("#additional_phoneNumber" + serialNumber + "_err").hide();
        $j("#additionalPhoneNumber" + serialNumber + "-row").show();
    }

    var onSubmit = function(e) {
        $j("span.error").addClass('hideError');
        verify("#name_err", $j("#name"), e);
        verify("#country_err", $j("#country"), e);
        verify("#region_err", $j("#region"), e);
        verify("#countyDistrict_err", $j("#countyDistrict"), e);
        verify("#stateProvince_err", $j("#stateProvince"), e);
        verifyPhone("#phoneNumber_err", $j("#phoneNumber"), e);
        verifyAdditionalPhone("#additional_phoneNumber1_err", $j("#additionalPhoneNumber1"), e);
        verifyAdditionalPhone("#additional_phoneNumber2_err", $j("#additionalPhoneNumber2"), e);
        verifyAdditionalPhone("#additional_phoneNumber3_err", $j("#additionalPhoneNumber3"), e);
    };

    var verify = function(errorSpan, selectElement, e) {
        if (selectElement.attr('disabled')) {
            return;
        }
        var selectedValue = $j.trim(selectElement.val());
        if (selectedValue == null || selectedValue == '') {
            $j(errorSpan).removeClass('hideError');
            e.preventDefault();
        }
    };

    var verifyPhone = function(errorSpan, selectElement, e) {
        var selectedValue = selectElement.val();
        if (/^0[0-9]{9}$/i.test(selectedValue)) {
            return;
        }
        $j(errorSpan).removeClass('hideError');
        e.preventDefault();
    };

    var verifyAdditionalPhone = function(errorSpan, selectElement, e) {
        if (selectElement.is(':visible')) {
            var selectedValue = selectElement.val();
            if (/^0[0-9]{9}$/i.test(selectedValue)) {
                return;
            }
            $j(errorSpan).removeClass('hideError');
            e.preventDefault();
        }
    };

    var handleChange = function(option, data, dropDown) {
        var val = option.val();
        if (val == '') return;

        var selector = 'li[title="' + val + '"]';
        var items = $j(data).children(selector).children('ul').children('li');
        if (items.length == 0) {
            return;
        }
        dropDown.enable();
        items.each(function() {
            var item = $j(this).html();
            if (item == '') return;
            dropDown.appendOption(new Option(item, item));
        });
    };

    var onCountryChange = function() {
        regionsDropDown.revert();
        districtsDropDown.revert();
        provincesDropDown.revert();
        handleChange($j(this), 'ul#regions_data', regionsDropDown);
    };

    var onRegionChange = function() {
        districtsDropDown.revert();
        provincesDropDown.revert();
        handleChange($j(this), 'ul#districts_data', districtsDropDown);
    };

    var onDistrictChange = function(district) {
        provincesDropDown.revert();
        handleChange($j(this), 'ul#provinces_data', provincesDropDown);
    };

    var bootstrap = function() {
        onLoad();
        bind();
    };
    $j(bootstrap);
}
;

$j(document).ready(addFacility);