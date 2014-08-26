define(function (require) {
    'use strict';

    return function (transferRow, lang) {
        var object = {};

        Object.keys(transferRow.fields).forEach(function (field) {
            object[field] = transferRow.fields[field].values[lang].current;
        });
        return object;
    };
});
