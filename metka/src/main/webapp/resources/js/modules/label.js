define(function (require) {
    'use strict';

    return function (options, lang, customclass) {
        function setLabel() {
            $label.empty();
            $label.text(MetkaJS.L10N.localize((options.title || options['&title']) ? options : options.fieldTitles[options.field.key], 'title'));

            if (options.required) {
                $label.append('<span class="glyphicon glyphicon-asterisk"></span>');
            }
            require('./langLabel')($label, lang);
        }

        var $label = $('<label class="control-label">');
        if(customclass) {
            $label.addClass(customclass);
        }
        options.$events.on('label-update-{key}-{lang}'.supplant({
            key: options.fieldOptions.key,
            lang: lang
        }), setLabel);
        setLabel();
        return $label;
    };
});
