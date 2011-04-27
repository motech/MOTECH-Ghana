function PatientFormRegistrationEvents() {

    var languages = new DynamicComboBox($j('#language'));
    var gender = new DynamicComboBox($j('#sex'));
    var media = new DynamicComboBox($j('#mediaType'));
    var weekToBeginMessages = new DynamicComboBox($j("#messagesStartWeek"));

    var bindEventHandlers = function() {
        bindToOnChangeIfElementExists($j('#registrantType'), patientTypeSelected);
        bindToOnChangeIfElementExists($j('#registrationMode'), registrationModeSelected);
        bindToOnChangeIfElementExists($j('#phoneType'), phoneOwnershipSelected);
        bindToOnChangeIfElementExists($j('#mediaType'), mediaTypeSelected);
        bindToOnChangeIfElementExists($j('#insured'), insuranceSelected);
    };

    //Invoke change handler as well to initialize
    var bindToOnChangeIfElementExists = function(ele, changeHandler) {
        if (elementExists(ele)) {
            $j(ele).change(changeHandler);
            changeHandler();
        }
    };

    var registrationModeSelected = function() {
        if (hasNonEmptySelection($j('#registrationMode'))) {
            hideMotechIdInputIfAutoGenerationModeSelected();
        }
    };

    var patientTypeSelected = function() {
        if (hasNonEmptySelection($j('#registrantType'))) {
            hidePregnancyRegistrationIfPatientIsNotPregnantMother();
            setGenderAsFemaleIfPatientIsPregnantMother();
            hideMothersMotechIdFieldIfPatientIsNotChild();
            modifyWeekToBeginMessageAccordingToPatientType();
            showReasonsForJoiningMotechOnlyIfPatientTypeIsOther();
        }
    };

    var showReasonsForJoiningMotechOnlyIfPatientTypeIsOther = function() {
       var reasons =  getParentRow($j('#interestReason')) ;
        if (isOtherPatient()) {
           reasons.show();
            return;
        }
        reasons.hide();
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

    var hideMotechIdInputIfAutoGenerationModeSelected = function() {
        var row = getParentRow($j('#motechId'));
        if (motechIdToBeAutoGenerated()) {
            hide(row);
            return;
        }
        show(row);
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
        var parentRow = getParentRow($j('#motherMotechId'));
        if (!isPatientChildUnderFive()) {
            hide(parentRow);
            return;
        }
        show(parentRow);
    };

    var modifyWeekToBeginMessageAccordingToPatientType = function() {
        weekToBeginMessages.revert();
        if (isPatientChildUnderFive()) {
            weekToBeginMessages.removeOptionsWhen(function(val) {
                if (blankData(val))return false;
                return val < 41;
            });
            return;
        }

        if (isPatientPregnantMother()) {
            weekToBeginMessages.removeOptionsWhen(function(val) {
                if (blankData(val))return false;
                return val > 40;
            });
            return;
        }

        weekToBeginMessages.revert();
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

    var isOtherPatient = function() {
        return $j('#registrantType').val() == "OTHER";
    };

    var isSelectedMediaTypeText = function() {
        return $j('#mediaType').val() == "TEXT";
    };

    var motechIdToBeAutoGenerated = function() {
        return $j('#registrationMode').val() == "AUTO_GENERATE_ID";
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

    var blankData = function(val) {
        return val == "";
    };

    var elementExists = function(ele) {
        return $j(ele).length > 0;
    };

    var bootstrap = function() {
        bindEventHandlers();
    };

    $j(bootstrap);
}



