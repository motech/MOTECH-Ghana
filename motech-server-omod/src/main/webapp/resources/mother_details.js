function MotherDetails(country,patientEvents) {
    var country = country;
    var patientEvents = patientEvents;
    
    var mothersMotechIdEntered = function(data) {
        var mothersMotechId = $j('#motherMotechId').val();
        if (isNotBlank(mothersMotechId)) {
            $j.getJSON("/openmrs/module/motechmodule/patient/getMotherInfo.form", {'motechId': mothersMotechId}, function(data) {
                if (data.lastName == null) {
                    markError($j('#motherMotechId'), "<span class = 'error'> Specified MoTeCH ID does not exist. </span>");
                } else {
                    markError($j('#motherMotechId'), "");
                }
                setElementValue($j('#lastName'), data.lastName, findDuplicates);
                setElementValue($j('#region'), data.region, country.regionSelected);
                setElementValue($j('#district'), data.district, country.districtSelected);
                setElementValue($j('#subDistrict'), data.subDistrict, country.subDistrictSelected);
                setElementValue($j('#facility'), data.facility, country.facilitySelected);
                setElementValue($j("#communityId"), data.communityId, findDuplicates);
                setElementValue($j('#address'), data.address);
                if (data.consent) {
                    setElementValue($j('#consent1'), data.consent.toString());
                }
                setElementValue($j('#phoneNumber'), data.phoneNumber);
                setElementValue($j('#phoneType'), data.phoneType, patientEvents.phoneOwnershipSelected);
                setElementValue($j('#mediaType'), data.mediaType, patientEvents.mediaTypeSelected);
                setElementValue($j('#language'), data.language);
                setElementValue($j('#dayOfWeek'), data.dayOfWeek);
                if (data.timeOfDay) {
                    setElementValue($j('#timeOfDay'), getFormattedTime(data.timeOfDay));
                }
                setElementValue($j('#interestReason'), data.interestReason);
                setElementValue($j('#howLearned'), data.howLearned);
                setElementValue($j('#messagesStartWeek'), data.messagesStartWeek);
            });
        }
    };

    var isNotBlank = function(value) {
        return $j.trim(value).length > 0;
    };


    var setElementValue = function(target, value, handler) {
        if (value) {
            target.val(value);
            if (handler) {
                handler();
            }
        }
    };

    var markError = function(element,error) {
        $j(element).parent().next().html(error);
    };

    var getFormattedTime = function(timeInMillisecs) {
        var date = new Date(timeInMillisecs);
        var hour = date.getHours().toString();
        var minutes = date.getMinutes().toString();
        if (date.getHours() < 10) {
            hour = "0" + hour;
        }
        if (date.getMinutes() < 10) {
            minutes = "0" + minutes;
        }
        var formatted_date = hour + ":" + minutes;
        return formatted_date;
    };

    var bootstrap = function() {
        $j('#motherMotechId').blur(mothersMotechIdEntered);
    };

    $j(bootstrap);

}
