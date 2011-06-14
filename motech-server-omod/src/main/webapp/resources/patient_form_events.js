
function PatientFormRegistrationEvents() {

    var languages = new DynamicComboBox($j('#language'));
    var gender = new DynamicComboBox($j('#sex'));
    var media = new DynamicComboBox($j('#mediaType'));
    var weekToBeginMessages = new DynamicComboBox($j("#messagesStartWeek"));

    var bindEventHandlers = function() {
        bindToOnChangeIfElementExists($j('#registrantType'), onPatientTypeSelection);
        bindToOnChangeIfElementExists($j('#registrationMode'), onRegistrationModeSelection);
        bindToOnChangeIfElementExists($j('#phoneType'), onPhoneOwnershipSelection);
        bindToOnChangeIfElementExists($j('#mediaType'), onMediaTypeSelection);
        bindToOnChangeIfElementExists($j('#insured'), onInsuranceSelection);
        bindToOnChangeIfElementExists($j('#enroll'), onMobileMidwifeInformationOptionSelection);
    };

    this.phoneOwnershipSelected = function(){
      onPhoneOwnershipSelection();  
    };

    this.mediaTypeSelected = function(){
        onMediaTypeSelection();
    }

    //Invoke change handler as well to initialize
    var bindToOnChangeIfElementExists = function(ele, changeHandler) {
        if (elementExists(ele)) {
            $j(ele).change(changeHandler);
            changeHandler();
        }
    };

    var onMobileMidwifeInformationOptionSelection = function() {
        var dataCollectionSection = $j('#mobileMidwifeInformation');
        if (!wantsToEnroll()) {
            hide(dataCollectionSection);
            return;
        }
        show(dataCollectionSection);
    };

    var onRegistrationModeSelection = function() {
        if (hasNonEmptySelection($j('#registrationMode'))) {
            hideMotechIdInputIfAutoGenerationModeSelected();
        }
    };

    var onPatientTypeSelection = function() {
        if (hasNonEmptySelection($j('#registrantType'))) {
            hidePregnancyRegistrationIfPatientIsNotPregnantMother();
            showWeekToBeginMessagesOnlyIfPatientTypeIsOther();
            setGenderAsFemaleIfPatientIsPregnantMother();
            hideMothersMotechIdFieldIfPatientIsNotChild();
            modifyWeekToBeginMessageAccordingToPatientType();
            showReasonsForJoiningMotechOnlyIfPatientTypeIsOther();
        }
    };

    var showReasonsForJoiningMotechOnlyIfPatientTypeIsOther = function() {
        var reasons = getParentRow($j('#interestReason'));
        if (isOtherPatient()) {
            reasons.show();
            return;
        }
        reasons.hide();
    };

    var onMediaTypeSelection = function() {
        if (hasNonEmptySelection($j('#mediaType'))) {
            hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText();
            setEnglishAsLanguageIfMessageFormatSelectedIsText();
        }
    };

    var onPhoneOwnershipSelection = function() {
        if (hasNonEmptySelection($j('#phoneType'))) {
            setVoiceOptionIfPhoneOwnershipIsPublic();
        }
    };

    var onInsuranceSelection = function() {
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
	
	var showWeekToBeginMessagesOnlyIfPatientTypeIsOther = function() {
        if (isPatientPregnantMother() || isPatientChildUnderFive()) {
            hide(getParentRow($j('#messagesStartWeek')));
            return;
        }
        show(getParentRow($j('#messagesStartWeek')));
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

    var wantsToEnroll = function() {
        return $j('#enroll').val() == "true";
    }

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



