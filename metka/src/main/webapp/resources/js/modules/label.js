define(function (require) {
    'use strict';

    return function (options, lang) {
        var $label = $('<label class="control-label">')
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (options.required || require('./utils/getPropertyNS')(options, 'dataConf.fields' , options.field.key, 'required')) {
            $label.append('<span class="glyphicon glyphicon-asterisk"></span>');
        }
        require('./langLabel')($label, lang);

        return $label;
    };
});
