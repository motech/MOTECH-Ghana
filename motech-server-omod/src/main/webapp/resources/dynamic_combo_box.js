function DynamicComboBox(combo) {
    this.combo = $j(combo);
    this.originalOptions = $j(combo).children('option').clone();

    this.showOnly = function(value) {
        var option = this.optionWithValue(value);
        this.empty();
        this.appendOption(option) ;
    };

    this.appendOption = function(option){
        var text = $j(option).text();
        var val = $j(option).val();
        var html = '<option value="' + val + '">' + text + "</option>";
        alert(html);
        this.combo.append(html);
    };

    this.empty = function(){
        this.combo.empty();
    };

    this.revert = function() {
        this.empty();
        var that = this ;
        this.originalOptions.each(function(index, option) {
           that.appendOption(option);
        });
    };

    this.optionWithValue = function(val) {
        var selected ;
        this.options().each(function(index, option) {
            if ($j(option).val() == val) {
                selected =  $j(option);
            }
        }) ;
        return selected;
    };

    this.options = function(){
        return this.combo.children('option');
    };
}