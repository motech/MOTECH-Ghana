var $j = jQuery.noConflict();


function editFacility() {

    var onLoad = function() {
        $j("#phoneNumber_err").hide();
        for (var i = 1; i <= 3; i++) {
            initializeAdditionalPhoneNumber(i);
        }
    };

    var initializeAdditionalPhoneNumber = function(i) {
        if ($j("#additionalPhoneNumber" + i).val() === "") {
            hidePhoneNumberRow(i);
        } else {
            enableAdditionalPhoneNumber({data: {index: i}});
        }
        $j("#additional_phoneNumber" + i+"_err").hide();
    }

    var bind = function() {
        $j("#additionalPhoneNumber1-link").bind('click', {index: 1}, enableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber2-link").bind('click', {index: 2}, enableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber3-link").bind('click', {index: 3}, enableAdditionalPhoneNumber);

        $j("#additionalPhoneNumber1-del").bind('click', {index: 1}, disableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber2-del").bind('click', {index: 2}, disableAdditionalPhoneNumber);
        $j("#additionalPhoneNumber3-del").bind('click', {index: 3}, disableAdditionalPhoneNumber);

        $j("#submit_facility").click(onSubmit);
    };

    var onSubmit = function(e){
        verifyPhoneNumber("#phoneNumber_err", $j("#phoneNumber"), e);
        verifyPhoneNumber("#additional_phoneNumber1_err", $j("#additionalPhoneNumber1"), e);
        verifyPhoneNumber("#additional_phoneNumber2_err", $j("#additionalPhoneNumber2"), e);
        verifyPhoneNumber("#additional_phoneNumber3_err", $j("#additionalPhoneNumber3"), e);
    };

    var enableAdditionalPhoneNumber = function(e) {
        var serialNumber = e.data.index;
        $j("#additionalPhoneNumber" + serialNumber + "-link").hide();
        $j("#additional_phoneNumber" + serialNumber + "_err").hide();
        $j("#additionalPhoneNumber" + serialNumber + "-row").show();
    }

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

    var verifyPhoneNumber = function(errorSpan, selectElement,e){
         if (selectElement.is(':visible')) {
             var selectedValue = selectElement.val();
             if (/^0[0-9]{9}$/i.test(selectedValue)) {
                 return;
             }
             $j(errorSpan).show();
             e.preventDefault();
         }
    };

    var bootstrap = function() {
        onLoad();
        bind();
    };
    $j(bootstrap);
}
;

$j(document).ready(editFacility);