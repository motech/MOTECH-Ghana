function DynamicComboBox(combo) {
    var combo = $j(combo);
    var originalOptions = $j(combo).children('option').clone();
    var modified = false;

    this.showOnly = function(value) {
        var option = this.optionWithValue(value);
        empty();
        this.appendOption(option);
    };

    this.removeOptionsWhen = function(predicate) {
        this.options().each(function(index, opt) {
            var val = $j(opt).val();
            if (predicate(val)) {
                $j(opt).remove();
            }
        });
        modified = true ;
    };

    // combo.append(option) is not working across browsers
    this.appendOption = function(option) {
        var text = $j(option).text();
        var val = $j(option).val();
        var html = '<option value="' + val + '">' + text + "</option>";
        combo.append(html);
        modified = true;
    };

    var empty = function() {
        combo.empty();
        modified = true;
    };

    this.revert = function() {
        if (!modified)return;

        empty();
        var that = this;
        originalOptions.each(function(index, option) {
            that.appendOption(option);
        });
        modified = false;
    };

    this.optionWithValue = function(val) {
        var selected;
        this.options().each(function(index, option) {
            if ($j(option).val() == val) {
                selected = $j(option);
            }
        });
        return selected;
    };

    this.options = function() {
        return combo.children('option');
    };
}