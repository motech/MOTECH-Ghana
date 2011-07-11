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
        modified = true;
    };

    this.currentValue = function() {
        return $j(combo).val();
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
        if (!modified)return this;

        empty();
        var that = this;
        originalOptions.each(function(index, option) {
            that.appendOption(option);
        });
        modified = false;
        return this;
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

    this.setValue = function(value) {
        $j(combo).val(value);
    };

    this.enable = function() {
        $j(combo).attr('disabled', '');
        return this;
    };

    this.disable = function() {
        $j(combo).attr('disabled', 'disabled');
        return this;
    };

    this.show = function() {
        $j(combo).show();
        showParentRow();
        return this;
    };

    this.hide = function() {
        $j(combo).hide();
        hideParentRow();
        return this;
    };

    var showParentRow = function() {
        var row = parentRow();
        if (row) {
            row.show();
        }
    };

    var hideParentRow = function() {
        var row = parentRow();
        if (row) {
            row.hide();
        }
    };

    var parentRow = function() {
        return $j(combo).parents('tr');
    };
}