define(function (require) {
    'use strict';

    return function (options) {
        var $input = $('<input type="checkbox">')
            .prop('disabled', require('./isFieldDisabled')(options))
            .change(function () {
                require('./data')(options).set($(this).prop('checked'));
            });

        require('./data')(options).onChange(function () {
            $input.prop('checked', !!require('./data')(options).get());
        });

        this
            .addClass('checkbox')
            .append(require('./label')(options)
                .prepend(require('./input').call($input, options)));
    };
});

