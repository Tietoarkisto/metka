define(function (require) {
    'use strict';

    return function (transferRow) {
        var object = {};

        Object.keys(transferRow.fields).forEach(function (field) {
            object[field] = transferRow.fields[field].value.current;
        });
        return object;
    };
});
