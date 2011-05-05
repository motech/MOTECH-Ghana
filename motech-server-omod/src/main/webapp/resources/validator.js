RequiredField = function(ele) {
    var element = ele;

    this.validate = function(evaluate) {
        if (isVisible()) {
            var hasData = evaluate ? evaluate(element) : isNotBlank(element);
            var errorMessageCell = getErrorMessageCell();
            if (!hasData) {
                errorMessageCell.show();
                return false;
            }
        }
        return true;
    };

    var isVisible = function() {
        return $j(ele).is(":visible");
    }

    var getErrorMessageCell = function() {
        var parentCell = getParentCell(element);
        return parentCell.siblings('.hideme');
    };

    var isNotBlank = function(ele) {
        return $j(ele).val().length > 0;
    };

    var getParentCell = function(ele) {
        return $j(ele).parents('td');
    };
};

CommunityValidator = function(community, region) {
    var community = community;
    var region = region;

    this.validate = function() {
        if (region.val() == 'Upper East') {
            return new RequiredField(community).validate();
        }
        return true;
    };

};

RequiredFieldValidator = function(patientForm) {
    var elementsToValidate = new Array();

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
        var isDataValid = true;
        $j(elementsToValidate).each(function(index, element) {
            if (isVisible(element)) {
                isDataValid = new RequiredField(element).validate() && isDataValid;
            }
        });
        return isDataValid;
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
        return true;

    };

};

MidwifeDataValidator = function(midWifeSection, consent) {

    var midWifeDataSection = midWifeSection;
    var consent = consent;
    var validators = new Array();

    this.add = function(validator) {
        validators[validators.length] = validator;
        return this;
    };


    this.validate = function() {
        if (midWifeDataSection.is(":visible")) {
            var consentGiven = new RequiredField(consent).validate(isChecked);
            var isDataValid = true;
            $j(validators).each(function(index, validator) {
                isDataValid = validator.validate() && isDataValid;
            });
            return consentGiven && isDataValid;
        }
        return true;
    };

    var isChecked = function(ele) {
        return ele.is(":checked");
    };

};

EnrollmentValidator = function(patientType, reason, startWeek, howLearnt) {
    var patientType = patientType;
    var reason = reason;
    var startWeek = startWeek;
    var howLearnt = howLearnt;


    this.validate = function() {
        var reasonAndWeekSelected = true;
        if (patientType.val() == 'OTHER') {
            var selectedReason = new RequiredField(reason).validate();
            var selectedStartWeek = new RequiredField(startWeek).validate();
            reasonAndWeekSelected = selectedReason && selectedStartWeek;
        }
        var howLearntSelected = new RequiredField(howLearnt).validate();
        return reasonAndWeekSelected && howLearnt;
    };
};

Validators = function(form) {

    var form = form;
    var validators = new Array();

    this.add = function(validator) {
        validators[validators.length] = validator;
        return this;
    };

    var validate = function() {
        removePreviousErrors();
        var isValid = true;
        $j(validators).each(function(index, validator) {
            isValid = validator.validate() && isValid;
        });
        return isValid;
    };

    var removePreviousErrors = function() {
        $j(form).find('.hideme').each(function(index, ele) {
            $j(ele).hide();
        });
    };

    var onSubmit = function() {
        return validate();
    };

    var bootstrap = function() {
        $j(form).submit(onSubmit);
    };

    $j(bootstrap);

};

