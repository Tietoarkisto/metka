define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        function partialRefresh() {
            options.$events.trigger('refresh.metka');
        }

        return {
            field: {
                onClick: function (transferRow, replaceTr) {
                    require('./../../variableModal')(options.field.key, {
                        id: transferRow.value,
                        no: ''
                    }, partialRefresh);
                }
            }
        };
    };
});
