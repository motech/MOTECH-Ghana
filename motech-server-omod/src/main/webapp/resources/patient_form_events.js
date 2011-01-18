function onPatientTypeSelection() {
    hidePregnancyRegistrationIfPatientIsNotPregnantMother();
    hideMothersMotechIdFieldIfPatientIsNotChild();
}
function hidePregnancyRegistrationIfPatientIsNotPregnantMother() {
    if (!isPatientPregnantMother()) {
        $('#pregnancyRegistration').hide();
        return;
    }
    $('#pregnancyRegistration').show();
}
function hideMothersMotechIdFieldIfPatientIsNotChild() {
    var parentRow = $('#motherMotechId').parents('tr');
    if (!isPatientChildUnderFive()) {
        $(parentRow).hide();
        return;
    }
    $(parentRow).show();
}

function isPatientPregnantMother() {
    return $('#registrantType').val() == "PREGNANT_MOTHER";
}

function isPatientChildUnderFive() {
    return $('#registrantType').val() == "CHILD_UNDER_FIVE";
}
