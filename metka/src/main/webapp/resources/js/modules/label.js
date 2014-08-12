define(function (require) {
    'use strict';

    return function (options) {
        var $label = $('<label class="control-label">')
            .text(MetkaJS.L10N.localize(options, 'title'));
        if (options.required) {
            $label.append('<span class="glyphicon glyphicon-asterisk"></span>');
        }
        return $label;
    };
});
