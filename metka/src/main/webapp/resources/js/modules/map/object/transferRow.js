define(function (require) {
    'use strict';

    return function (data) {
        var fields = {};
        Object.keys(data).forEach(function (field) {
            fields[field] = {
                type: 'VALUE',
                value: {
                    current: data[field]
                }
            };
        });
        return {
            fields: fields
        };
    };
});
