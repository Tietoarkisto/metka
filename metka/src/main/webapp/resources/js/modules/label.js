define(function (require) {
    'use strict';

    return function (options, lang) {
        var $label = $('<label class="control-label">')
            .text(MetkaJS.L10N.localize((options.title || options['&title']) ? options : options.fieldTitles[options.field.key], 'title'));

        if (options.required) {
            $label.append('<span class="glyphicon glyphicon-asterisk"></span>');
        }
        require('./langLabel')($label, lang);

        return $label;
    };
});
