define(function (require) {
    'use strict';

    return function (options, lang) {
        var reverseBoolean = !!options.field.reverseBoolean;
        var $input = $('<input type="checkbox">')
            .prop('disabled', require('./isFieldDisabled')(options))
            .change(function () {
                require('./data')(options).setByLang(lang, reverseBoolean !== $(this).prop('checked'));
            });

        require('./data')(options).onChange(function () {
            var value = require('./data')(options).getByLang(lang);
            var boolValue = typeof value === 'string' ? value.bool() : !!value;
            $input.prop('checked', reverseBoolean !== boolValue);
        });

        this
            .append(require('./label')(options, lang))
            .append(require('./input').call($input, options));
    };
});
