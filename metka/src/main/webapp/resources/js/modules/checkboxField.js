define(function (require) {
    'use strict';

    return function (options) {
        var key = options.field.key;
        this
            .addClass('checkbox')
            .append(require('./label')(options)
                .prepend(require('./input').call($('<input type="checkbox">'), options)
                    .prop('disabled', require('./isFieldDisabled')(options))
                    .change(function () {
                        require('./data').set(options, key, $(this).prop('checked'));
                    })
                    .prop('checked', !!require('./data').get(options, key))));
    };
});
