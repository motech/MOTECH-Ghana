function onPatientTypeSelection() {
    if (hasNonEmptySelection($j('#registrantType'))) {
        hidePregnancyRegistrationIfPatientIsNotPregnantMother();
        setGenderAsFemaleIfPatientIsPregnantMother();
        hideMothersMotechIdFieldIfPatientIsNotChild();
    }
}

function onMediaTypeSelection() {
    if (hasNonEmptySelection($j('#mediaType'))) {
        hideDayOfWeekAndTimeOfDayFieldsIfMessageFormatSelectedIsText();
    }
}

function onPhoneOwnershipSelection() {
    if (hasNonEmptySelection($j('#phoneType'))) {
        setVoiceOptionIfPhoneOwnershipIsPublic();
    }
}

function setGenderAsFemaleIfPatientIsPregnantMother() {
    var gender = $j('#sex');
    if (isPatientPregnantMother()) {
        gender.val("FEMALE");
        var male = getOptionWithValue(gender.children(), "MALE");
        $j(male).remove();
    } else {
        if (!optionExists(gender, "MALE")) {
            gender.append('<option value="MALE">Male</option>');
        }
    }
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
    var media = $j('#mediaType');
    if (isPublicPhone()) {
        media.val("VOICE");
        var textOption = getOptionWithValue(media.children(), "TEXT");
        $j(textOption).remove();
    } else {
        if (!optionExists(media, "TEXT")) {
            media.append('<option value="TEXT">Text</option>');
        }
    }
}

function toggleOptions(comboBox, optionToSet, optionToToggle, shouldSetNewValue) {
    if (shouldSetNewValue()) {
        $j(comboBox).val($j(optionToSet).val());
        var optionToRemove = getOptionWithValue($j(comboBox).children(), $j(optionToToggle).val());
        $j(optionToRemove).remove();
    } else {
        if (!optionExists(comboBox, $j(optionToToggle).val())) {
            var options = $j(comboBox).attr('options');
            options[options.length] = new Option($j(optionToToggle).text(), $j(optionToToggle).val(), true, true);
        }
    }
}

function optionExists(comboBox, value) {
    var found = false;
    $j(comboBox).children().each(function(index, ele) {
        if ($j(ele).val() == value) {
            found = true;
        }
    });
    return found;
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

function hasNonEmptySelection(comboBox) {
    return $j(comboBox).val() != "";
}


