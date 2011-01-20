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
    hideOptionAndSetBusinessDefault($('#sex'), "MALE", "FEMALE", isPatientPregnantMother)
}

function hidePregnancyRegistrationIfPatientIsNotPregnantMother() {
    if (!isPatientPregnantMother()) {
        hide($('#pregnancyRegistration'));
        return;
    }
    show($('#pregnancyRegistration'));
}

function hideMothersMotechIdFieldIfPatientIsNotChild() {
    var parentRow = $('#motherMotechId').parents('tr');
    if (!isPatientChildUnderFive()) {
        hide(parentRow);
        return;
    }
    show(parentRow);
}
function hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText() {
    var dayOfWeekRow = $('#dayOfWeek').parents('tr');
    var timeOfDayRow = $('#timeOfDay').parents('tr');

    if (isSelectedMediaTypeText()) {
        hide(dayOfWeekRow, timeOfDayRow);
        return;
    }
    show(dayOfWeekRow, timeOfDayRow);
}

function setVoiceOptionIfPhoneOwnershipIsPublic() {
    hideOptionAndSetBusinessDefault($('#mediaType'), "TEXT", "VOICE", isPublicPhone);
}

function hideOptionAndSetBusinessDefault(comboBox, valueToHide, valueToSet, predicate) {
    var options = $(comboBox).children();
    var optionToHide = getOptionWithValue(options, valueToHide);
    if (predicate()) {
        hide(optionToHide);
        comboBox.val(valueToSet);
        return;
    }
    show(optionToHide);
    comboBox.val("");
}

function getOptionWithValue(options, val) {
    var option;
    options.each(function(index, ele) {
        if ($(ele).val() == val) {
            option = $(ele);
        }
    })
    return option;
}
function hide() {
    for (var i = 0; i < arguments.length; i++) {
        $(arguments[i]).hide();
    }
}

function show() {
    for (var i = 0; i < arguments.length; i++) {
        $(arguments[i]).show();
    }
}

function isPublicPhone() {
    return $('#phoneType').val() == "PUBLIC";
}

function isPatientPregnantMother() {
    return $('#registrantType').val() == "PREGNANT_MOTHER";
}

function isPatientChildUnderFive() {
    return $('#registrantType').val() == "CHILD_UNDER_FIVE";
}

function isSelectedMediaTypeText() {
    return $('#mediaType').val() == "TEXT";
}


