var $j = jQuery.noConflict();


function addFacility(){
    var countryDropDown = new DynamicComboBox($j('#country'));
    var regionsDropDown = new DynamicComboBox($j('#region'));
    var districtsDropDown = new DynamicComboBox($j('#countyDistrict'));
    var provincesDropDown = new DynamicComboBox($j('#stateProvince'));

    var onLoad = function(){
        regionsDropDown.disable();
        districtsDropDown.disable();
        provincesDropDown.disable();
        $j("#phoneNumber").val('');
        $j("#location_data").hide();
        $j("#additionalPhoneNumber1-row").hide();
        $j("#additionalPhoneNumber2-row").hide();
        $j("#additionalPhoneNumber3-row").hide();
        $j('span[title="err_span"]').addClass('hideError');
        $j("#countries_data").children("li").each(function(){
            var item = $j(this).html();
            if(item == '')return;
            countryDropDown.appendOption(new Option(item,item));
       });
    };

    var bind = function(){
        $j("#country").change(onCountryChange);
        $j("#region").change(onRegionChange);
        $j("#countyDistrict").change(onDistrictChange);
        $j("#submit_facility").click(onSubmit);
        $j("#additionalPhoneNumber1-link").click(enableAdditionalPhoneNumber1);
        $j("#additionalPhoneNumber2-link").click(enableAdditionalPhoneNumber2);
        $j("#additionalPhoneNumber3-link").click(enableAdditionalPhoneNumber3);
    };

    var enableAdditionalPhoneNumber1 = function(e) {
         $j("#additionalPhoneNumber1-link").hide();
         $j("#additionalPhoneNumber1-row").show();
    }

    var enableAdditionalPhoneNumber2 = function(e) {
         $j("#additionalPhoneNumber2-link").hide();
         $j("#additionalPhoneNumber2-row").show();
    }

    var enableAdditionalPhoneNumber3 = function(e) {
         $j("#additionalPhoneNumber3-link").hide();
         $j("#additionalPhoneNumber3-row").show();
    }

    var onSubmit = function(e){
	    $j("span.error").addClass('hideError');
        verify("#name_err",$j("#name"), e);
        verify("#country_err",$j("#country"), e);
        verify("#region_err",$j("#region"), e);
        verify("#countyDistrict_err",$j("#countyDistrict"), e);
        verify("#stateProvince_err",$j("#stateProvince"), e);
        verifyPhone("#phoneNumber_err",$j("#phoneNumber"), e);
        verifyAdditionalPhone("#additional_phoneNumber1_err",$j("#additionalPhoneNumber1"), e);
        verifyAdditionalPhone("#additional_phoneNumber2_err",$j("#additionalPhoneNumber2"), e);
        verifyAdditionalPhone("#additional_phoneNumber3_err",$j("#additionalPhoneNumber3"), e);
    };

    var verify = function(errorSpan, selectElement, e){
        if(selectElement.attr('disabled')){
           return;
        }
        var selectedValue = $j.trim(selectElement.val());
        if(selectedValue == null || selectedValue == ''){		      
           $j(errorSpan).removeClass('hideError');
           e.preventDefault();
        }
    };

    var verifyPhone = function(errorSpan,selectElement,e){
       var selectedValue = selectElement.val();
       if(/^0[0-9]{9}$/i.test(selectedValue)){
          return;
       }
       $j(errorSpan).removeClass('hideError');
       e.preventDefault();
    };

     var verifyAdditionalPhone = function(errorSpan,selectElement,e){
         if (selectElement.is(':visible')) {
             var selectedValue = selectElement.val();
             if (/^0[0-9]{9}$/i.test(selectedValue)) {
                 return;
             }
             $j(errorSpan).removeClass('hideError');
             e.preventDefault();
         }
    };

    var handleChange = function(option, data, dropDown){
       var val = option.val();
       if(val == '') return;

       var selector = 'li[title="'+val+'"]';
       var items = $j(data).children(selector).children('ul').children('li');
       if(items.length == 0){
            return;
       }
       dropDown.enable();
       items.each(function(){
           var item = $j(this).html();
           if(item == '') return;
           dropDown.appendOption(new Option(item,item));
       });
    };

    var onCountryChange = function(){
       regionsDropDown.revert();
       districtsDropDown.revert();
       provincesDropDown.revert();
       handleChange($j(this),'ul#regions_data',regionsDropDown);
    };

    var onRegionChange = function(){
       districtsDropDown.revert();
       provincesDropDown.revert();
       handleChange($j(this),'ul#districts_data',districtsDropDown);
    };

    var onDistrictChange = function(district){
       provincesDropDown.revert();
       handleChange($j(this),'ul#provinces_data',provincesDropDown);
    };

    var bootstrap = function(){
       onLoad();
       bind();
    };
    $j(bootstrap);
};

$j(document).ready(addFacility);