RequiredFieldValidator = function(patientForm) {
    var form = patientForm;
    var elementsToValidate = new Array();
    var errors = 0;
    var index = 0;

    this.add = function(element) {
        elementsToValidate[index] = element;
        index = index + 1;
        return this;
    };

    this.addAll = function(){
        var that = this;
        $j(arguments).each(function(index,ele){
          that.add(ele);
      });
    };

    var validateAll = function() {
        errors = 0;
        $j(elementsToValidate).each(function(index, element) {
            if (isVisible(element)) {
                errors = errors + validateForNonEmptyValue(element);
            }
        });
        return errors;
    };

    var validateForNonEmptyValue = function(ele) {
        var hasData = $j(ele).val().length > 0;
        var parentCell = getParentCell(ele);
        var errorMessageCell = parentCell.siblings('.hideme');
        if (!hasData) {
            errorMessageCell.show();
            return 1;
        }
        errorMessageCell.hide();
        return 0;
    };

    var isVisible = function(ele) {
        return $j(ele) && $j(ele).is(":visible");
    };

    var getParentCell = function(ele) {
        return $j(ele).parents('td');
    };

    var onSubmit = function() {
        var errors = validateAll();
        return errors == 0;
    };

    var bootstrap = function() {
        $j(form).submit(onSubmit);
    };

    $j(bootstrap);

};

