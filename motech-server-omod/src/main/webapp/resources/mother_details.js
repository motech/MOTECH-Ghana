$j(document).ready(function(){
    $j('#motherMotechId').blur(function(data){
        var motech_id = $j('#motherMotechId').val();
        $j.getJSON("/openmrs/module/motechmodule/patient/getMotherInfo.form",{'motechId': motech_id}, function(data){
            if(data.lastName != null) {
                $j('#lastName').val(data.lastName);
                findDuplicates();
            }
            if(data.region != null){
                $j('#region').val(data.region);
                regionDistrictUpdated();
            }
            if(data.district != null){
                $j('#district').val(data.district);
                regionDistrictUpdated()
            }
            if(data.communityId != null){
                $j("#communityId").val(data.communityId);
                findDuplicates();
            }
            if(data.address != null){
                $j('#address').val(data.address);
            }
            if(data.enroll != null){
                $j('#enroll').val(data.enroll.toString());
            }
            if(data.consent != null){
                $j('#consent1').val(data.consent.toString());
            }
            if(data.phoneNumber != null){
                $j('#phoneNumber').val(data.phoneNumber);
            }
            if(data.phoneType != null){
                $j('#phoneType').val(data.phoneType);
                onPhoneOwnershipSelection();
            }
            if(data.mediaType != null){
                $j('#mediaType').val(data.mediaType);
                onMediaTypeSelection();
            }
            if(data.language != null){
                $j('#language').val(data.language);
            }
            if(data.dayOfWeek != null){
                $j('#dayOfWeek').val(data.dayOfWeek);
            }
            if(data.timeOfDay != null){
                var date = new Date(data.timeOfDay);
                var hour = date.getHours().toString();
                var minutes = date.getMinutes().toString();
                if(date.getHours() < 10){
                    hour = "0" + hour;
                }
                if(date.getMinutes() < 10){
                    minutes = "0" + minutes;
                }
                var formatted_date = hour + ":" + minutes;
                $j('#timeOfDay').val(formatted_date);
            }
            if(data.interestReason != null){
                $j('#interestReason').val(data.interestReason);
            }
            if(data.howLearned != null){
                $j('#howLearned').val(data.howLearned);
            }
            if(data.messagesStartWeek != null){
                $j('#messagesStartWeek').val(data.messagesStartWeek);
            }
        });
    });
});
