define(function (require) {
    'use strict';

    return function (options) {
        return {
            field: {
                onClick: function (transferRow, replaceTr) {
                    require('./../../variableModal')(options.field.key, {
                        id: transferRow.value
                    }, replaceTr);
                }
            }
        };
    };
});
