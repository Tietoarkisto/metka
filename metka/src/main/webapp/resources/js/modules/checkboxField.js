define(function (require) {
    'use strict';

    return function (options) {
        log(options, require('./data')(options).get())
        this
            .addClass('checkbox')
            .append(require('./label')(options)
                .prepend(require('./input').call($('<input type="checkbox">'), options)
                    .prop('disabled', require('./isFieldDisabled')(options))
                    .change(function () {
                        require('./data')(options).set($(this).prop('checked'));
                    })
                    .prop('checked', !!require('./data')(options).get())));
    };
});
