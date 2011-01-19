function onPatientTypeSelection() {
    hidePregnancyRegistrationIfPatientIsNotPregnantMother();
    hideMothersMotechIdFieldIfPatientIsNotChild();
}

function onMediaTypeSelection() {
    hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText();
}

function onPhoneOwnershipSelection() {
    removeTextOptionIfPhoneOwnershipIsPublic();
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

function removeTextOptionIfPhoneOwnershipIsPublic() {
    var mediaType = $('#mediaType');
    var mediaTypeOptions = mediaType.children();
    var textOption = getOptionWithValue(mediaTypeOptions, "TEXT");
    if (isPublicPhone()) {
        hide(textOption);
        var voiceOption = getOptionWithValue(mediaTypeOptions, "VOICE");
        mediaType.val("VOICE");
        return;
    }
    show(textOption)
    mediaType.val("");
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


