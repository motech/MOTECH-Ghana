var $j = jQuery.noConflict();


function addFacility(){
    var countryDropDown = new DynamicComboBox($j('#country'));
    var regionsDropDown = new DynamicComboBox($j('#region'));
    var districtsDropDown = new DynamicComboBox($j('#countyDistrict'));
    var provincesDropDown = new DynamicComboBox($j('#stateProvince'));

    var onLoad = function(){
        $j("#location_data").hide();
        $j("span.error").addClass('hideError');
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
    };

    var onSubmit = function(e){
	    $j("span.error").addClass('hideError');
        verify("#name_err",$j("#name").val(), e);
        verify("#country_err",$j("#country").val(), e);
        verify("#region_err",$j("#region").val(), e);
        verify("#countyDistrict_err",$j("#countyDistrict").val(), e);
        verify("#stateProvince_err",$j("#stateProvince").val(), e);
    };

    var verify = function(errorSpan, selectedValue, e){
        if(selectedValue == null || selectedValue == ''){		      
           $j(errorSpan).removeClass('hideError');
           e.preventDefault();
        }
    };

    var handleChange = function(option, data, dropDown){
       var val = option.val();
       if(val == '') return;

       var selector = 'li[title="'+val+'"]';
       var items = $j(data).children(selector).children('ul').children('li');

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