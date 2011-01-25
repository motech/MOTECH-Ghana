function onPatientTypeSelection() {
    hidePregnancyRegistrationIfPatientIsNotPregnantMother();
    setGenderAsFemaleIfPatientIsPregnantMother();
    hideMothersMotechIdFieldIfPatientIsNotChild();
}

function onMediaTypeSelection() {
    hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText();
}

function onPhoneOwnershipSelection() {
    setVoiceOptionIfPhoneOwnershipIsPublic();
}

function setGenderAsFemaleIfPatientIsPregnantMother() {
    hideOptionAndSetBusinessDefault($j('#sex'), "FEMALE", "MALE", isPatientPregnantMother)
}

function hidePregnancyRegistrationIfPatientIsNotPregnantMother() {
    if (!isPatientPregnantMother()) {
        hide($j('#pregnancyRegistration'));
        return;
    }
    show($j('#pregnancyRegistration'));
}

function hideMothersMotechIdFieldIfPatientIsNotChild() {
    var parentRow = $j('#motherMotechId').parents('tr');
    if (!isPatientChildUnderFive()) {
        hide(parentRow);
        return;
    }
    show(parentRow);
}
function hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText() {
    var dayOfWeekRow = $j('#dayOfWeek').parents('tr');
    var timeOfDayRow = $j('#timeOfDay').parents('tr');

    if (isSelectedMediaTypeText()) {
        hide(dayOfWeekRow, timeOfDayRow);
        return;
    }
    show(dayOfWeekRow, timeOfDayRow);
}

function setVoiceOptionIfPhoneOwnershipIsPublic() {
    hideOptionAndSetBusinessDefault($j('#mediaType'), "VOICE", "TEXT", isPublicPhone);
}

function hideOptionAndSetBusinessDefault(comboBox, valueToSet, valueToRemove, predicate) {
    var optionToSelect = getOptionWithValue($j(comboBox).children(), valueToSet);
    var optionToRemove = getOptionWithValue($j(comboBox).children(), valueToRemove);
    if (predicate()) {
        $j(optionToSelect).attr('selected', 'selected');
        $j(optionToRemove).hide();
    } else {
        comboBox.val("");
        $j(optionToSelect).removeAttr('selected');
        $j(optionToRemove).show();
    }
}

function getOptionWithValue(options, val) {
    var option;
    options.each(function(index, ele) {
        if ($j(ele).val() == val) {
            option = $j(ele);
        }
    })
    return option;
}
function hide() {
    for (var i = 0; i < arguments.length; i++) {
        $j(arguments[i]).hide();
    }
}

function show() {
    for (var i = 0; i < arguments.length; i++) {
        $j(arguments[i]).show();
    }
}

function isPublicPhone() {
    return $j('#phoneType').val() == "PUBLIC";
}

function isPatientPregnantMother() {
    return $j('#registrantType').val() == "PREGNANT_MOTHER";
}

function isPatientChildUnderFive() {
    return $j('#registrantType').val() == "CHILD_UNDER_FIVE";
}

function isSelectedMediaTypeText() {
    return $j('#mediaType').val() == "TEXT";
}


