define(function (require) {
    'use strict';

    return function (options, lang) {
        var $input = $('<input type="checkbox">')
            .prop('disabled', require('./isFieldDisabled')(options))
            .change(function () {
                require('./data')(options).setByLang(lang, $(this).prop('checked'));
            });

        require('./data')(options).onChange(function () {
            var value = require('./data')(options).getByLang(lang);
            $input.prop('checked', typeof value === 'string' ? value.bool() : !!value);
        });

        this
            .addClass('checkbox')
            .append(require('./label')(options)
                .prepend(require('./input').call($input, options)));
    };
});

