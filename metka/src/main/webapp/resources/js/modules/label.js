define(function (require) {
    return function (options) {
        var $label = $('<label>')
            .text(MetkaJS.L10N.localize(options, 'title'));
        if (options.required) {
            $label.append('<span class="glyphicon glyphicon-asterisk"></span>');
        }
        return $label;
    };
});
