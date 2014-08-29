define(function (require) {
    'use strict';

    return function (data, lang) {
        if (!lang) {
            throw 'missing lang';
        }
        var fields = {};
        Object.keys(data).forEach(function (field) {
            fields[field] = {
                type: 'VALUE'
            };
            require('../../utils/setPropertyNS')(fields, field, 'values', lang, 'current', data[field]);
        });
        return {
            fields: fields
        };
    };
});
