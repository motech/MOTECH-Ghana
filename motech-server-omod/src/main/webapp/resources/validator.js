RequiredField = function(ele) {
    var element = ele;

    this.validate = function(evaluate) {
        var hasData = evaluate ? evaluate(element): hasValidData(element);
        var parentCell = getParentCell(element);
        var errorMessageCell = parentCell.siblings('.hideme');
        if (!hasData) {
            errorMessageCell.show();
            return false;
        }
        errorMessageCell.hide();
        return true;
    };

    var hasValidData = function(ele){
       return $j(ele).val().length > 0 ;
    };

    var getParentCell = function(ele) {
        return $j(ele).parents('td');
    };
};

RequiredFieldValidator = function(patientForm) {
    var form = patientForm;
    var elementsToValidate = new Array();
    var hasValidData = true;
    var index = 0;

    this.add = function(element) {
        elementsToValidate[index] = element;
        index = index + 1;
        return this;
    };

    this.addAll = function() {
        var that = this;
        $j(arguments).each(function(index, ele) {
            that.add(ele);
        });
    };

    this.validate = function() {
        $j(elementsToValidate).each(function(index, element) {
            if (isVisible(element)) {
                hasValidData = new RequiredField(element).validate() && hasValidData;
            }
        });
        alert('required field validator ' + hasValidData);
        return hasValidData;
    };

    var isVisible = function(ele) {
        return $j(ele) && $j(ele).is(":visible");
    };

};

PhoneDetailsValidator = function(phoneNumber, phoneType) {
    var number = phoneNumber;
    var phoneType = phoneType;

    this.validate = function() {
        var hasPhoneType = new RequiredField(phoneType).validate();
        if (!hasPhoneType) return false;
        var phoneTypeSelected = phoneType.val();
        var numberRequired = ( phoneTypeSelected == 'PERSONAL' || phoneTypeSelected == 'HOUSEHOLD' );
        if (numberRequired) {
            return new RequiredField(number).validate();
        }
        return true;
    };

};

MediaTypeValidator = function(mediaType, language, dayOfWeek, timeOfDay) {
    var mediaType = mediaType;
    var dayOfWeek = dayOfWeek;
    var timeOfDay = timeOfDay;
    var language = language;

    this.validate = function() {
        var hasMediaType = new RequiredField(mediaType).validate();

        if (!hasMediaType)return false;

        var mediaTypeSelected = mediaType.val();
        if (mediaTypeSelected == 'VOICE') {
            var languageSpecified = new RequiredField(language).validate();
            var dayOfWeekSpecified = new RequiredField(dayOfWeek).validate();
            var timeOfDaySpecified = new RequiredField(timeOfDay).validate();
            return languageSpecified && dayOfWeekSpecified
                    && timeOfDaySpecified;
        }

    };

};

MidwifeDataValidator = function(midWifeSection, consent, phoneDetailsValidator, mediaTypeValidator) {

    var midWifeDataSection = midWifeSection;
    var consent = consent;
    var phoneDetailsValidator = phoneDetailsValidator;
    var mediaTypeValidator = mediaTypeValidator;


    this.validate = function() {
        if (midWifeDataSection.is(":visible")) {
            var consentGiven = new RequiredField(consent).validate(isChecked);
            var phoneDetailsGiven = phoneDetailsValidator.validate();
            var messageDetailsGiven = mediaTypeValidator.validate();
            var hasValidData = consentGiven && phoneDetailsGiven && messageDetailsGiven;
            alert('mm field validator ' + hasValidData);
            return hasValidData;
        }
        return true;
    };

    var isChecked = function(ele) {
        return ele.is(":checked") ;
    };

};

Validators = function(form) {

    var form = form;
    var validators = new Array();
    var hasValidData = true;

    this.add = function(validator) {
        validators[validators.length] = validator;
        return this;
    };

    var validate = function() {
        $j(validators).each(function(index, validator) {
            hasValidData = validator.validate() && hasValidData;
        });
        return hasValidData;
    };

    var onSubmit = function() {
        return validate();
    };

    var bootstrap = function() {
        $j(form).submit(onSubmit);
    };

    $j(bootstrap);

};

