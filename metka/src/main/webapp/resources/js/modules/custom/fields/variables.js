define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;
        return {
            field: {
                onClick: function (transferRow, replaceTr) {
                    require('./../../variableModal')(options.field.key, {
                        id: transferRow.value,
                        no: ''
                    }, replaceTr);
                }
            }
        };
    };
});
