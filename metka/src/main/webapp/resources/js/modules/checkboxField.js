define(function (require) {
    'use strict';

    return function (options) {
        var key = options.field.key;
        options.transferField.value = options.transferField.value || {
            current: undefined
        };

        this
            .addClass('checkbox')
            .append(require('./label')(options)
                .prepend(require('./input').call($('<input type="checkbox">'), options)
                    .prop('disabled', require('./isFieldDisabled')(options))
                    .change(function () {
                        options.transferField.value.current = $(this).prop('checked');
                    })
                    .prop('checked', !!options.transferField.value.current)));
    };
});
