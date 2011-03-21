function PatientFormRegistrationEvents(isFirstTimeRegistration) {

    var firstTimeRegistration = isFirstTimeRegistration;
    var languages = new DynamicComboBox($j('#language'));
    var gender = new DynamicComboBox($j('#sex'));
    var media = new DynamicComboBox($j('#mediaType'));

    var bindEventHandlers = function() {
        if(firstTimeRegistration){
            $j('#registrantType').change(patientTypeSelected);
        }
        $j('#phoneType').change(phoneOwnershipSelected);
        $j('#mediaType').change(mediaTypeSelected);
        $j('#insured').change(insuranceSelected);
    }

    var initialSettings = function() {
        if(firstTimeRegistration){
            patientTypeSelected();
        }
        phoneOwnershipSelected();
        mediaTypeSelected();
        insuranceSelected();
    };

    var patientTypeSelected = function() {
        if (hasNonEmptySelection($j('#registrantType'))) {
            hidePregnancyRegistrationIfPatientIsNotPregnantMother();
            setGenderAsFemaleIfPatientIsPregnantMother();
            hideMothersMotechIdFieldIfPatientIsNotChild();
        }
    };

    var mediaTypeSelected = function() {
        if (hasNonEmptySelection($j('#mediaType'))) {
            hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText();
            setEnglishAsLanguageIfMessageFormatSelectedIsText();
        }
    };

    var phoneOwnershipSelected = function() {
        if (hasNonEmptySelection($j('#phoneType'))) {
            setVoiceOptionIfPhoneOwnershipIsPublic();
        }
    };

    var insuranceSelected = function() {
        if (hasNonEmptySelection($j('#insured'))) {
            hideInsuranceSectionIfNotInsured();
        }
    };

    var hideInsuranceSectionIfNotInsured = function() {
        var insuranceRow = getParentRow('#nhis');
        var insuranceExpiryDateRow = getParentRow('#nhisExpDate');
        if (insured()) {
            show(insuranceRow, insuranceExpiryDateRow)
            return;
        }
        hide(insuranceRow, insuranceExpiryDateRow);
    };

    var insured = function() {
        return "true" == $j('#insured').val();
    };

    var setGenderAsFemaleIfPatientIsPregnantMother = function() {
        if (isPatientPregnantMother()) {
            gender.showOnly('FEMALE');
            return;
        }
        gender.revert();
    };

    var hidePregnancyRegistrationIfPatientIsNotPregnantMother = function() {
        if (!isPatientPregnantMother()) {
            hide($j('#pregnancyRegistration'));
            return;
        }
        show($j('#pregnancyRegistration'));
    };

    var hideMothersMotechIdFieldIfPatientIsNotChild = function() {
        var parentRow = $j('#motherMotechId').parents('tr');
        if (!isPatientChildUnderFive()) {
            hide(parentRow);
            return;
        }
        show(parentRow);
    };

    var hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText = function() {
        var dayOfWeekRow = $j('#dayOfWeek').parents('tr');
        var timeOfDayRow = $j('#timeOfDay').parents('tr');

        if (isSelectedMediaTypeText()) {
            hide(dayOfWeekRow, timeOfDayRow);
            return;
        }
        show(dayOfWeekRow, timeOfDayRow);
    };

    var setVoiceOptionIfPhoneOwnershipIsPublic = function() {
        if (isPublicPhone()) {
            media.showOnly('VOICE');
            return;
        }
        media.revert();
    };

    var hide = function() {
        for (var i = 0; i < arguments.length; i++) {
            $j(arguments[i]).hide();
        }
    };

    var show = function() {
        for (var i = 0; i < arguments.length; i++) {
            $j(arguments[i]).show();
        }
    };

    var isPublicPhone = function() {
        return $j('#phoneType').val() == "PUBLIC";
    };

    var isPatientPregnantMother = function() {
        return $j('#registrantType').val() == "PREGNANT_MOTHER";
    };

    var isPatientChildUnderFive = function() {
        return $j('#registrantType').val() == "CHILD_UNDER_FIVE";
    };

    var isSelectedMediaTypeText = function() {
        return $j('#mediaType').val() == "TEXT";
    };

    var hasNonEmptySelection = function(comboBox) {
        return $j(comboBox).val() != "";
    };

    var getParentRow = function(ele) {
        return $j(ele).parents('tr');
    };

    var setEnglishAsLanguageIfMessageFormatSelectedIsText = function() {
        if (isSelectedMediaTypeText()) {
            languages.showOnly('en');
            return;
        }
        languages.revert();
    };

    var bootstrap = function() {
        bindEventHandlers();
        initialSettings();
    };

    $j(bootstrap);
}



