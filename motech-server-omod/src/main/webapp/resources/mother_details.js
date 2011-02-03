$j(document).ready(function(){
    $j('#motherMotechId').blur(function(data){
        var motech_id = $j('#motherMotechId').val();
        $j.getJSON("/openmrs/module/motechmodule/patient/getMotherInfo.form",{'motechId': motech_id}, function(data){
            set_val_to_html($j('#lastName'), data.lastName, findDuplicates());
            set_val_to_html($j('#region'), data.region, regionDistrictUpdated);
            set_val_to_html($j('#district'), data.district, regionDistrictUpdated);
            set_val_to_html($j("#communityId"), data.communityId, findDuplicates);
            set_val_to_html($j('#address'), data.address);
            if (data.consent) {
                set_val_to_html($j('#consent1'), data.consent.toString());
            }
            set_val_to_html($j('#phoneNumber'), data.phoneNumber);
            set_val_to_html($j('#phoneType'), data.phoneType, onPhoneOwnershipSelection);
            set_val_to_html($j('#mediaType'), data.mediaType, onMediaTypeSelection);
            set_val_to_html($j('#language'), data.language);
            set_val_to_html($j('#dayOfWeek'), data.dayOfWeek);
            if (data.timeOfDay) {
                var formatted_date = getFormattedTime(data.timeOfDay);
                set_val_to_html($j('#timeOfDay'), formatted_date);
            }
            set_val_to_html($j('#interestReason'), data.interestReason);
            set_val_to_html($j('#howLearned'), data.howLearned);
            set_val_to_html($j('#messagesStartWeek'), data.messagesStartWeek);
        });
    });
});

function set_val_to_html(target,value,handler){
    if(value){
        target.val(value);
        if(handler){
            handler();
        }
    }
}

function getFormattedTime(timeInMillisecs) {
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
}