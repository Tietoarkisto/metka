define(function (require) {
    'use strict';

    return function (url, requestData, getResults, mapResult, fields, columnFields, trOnClick) {
        var args = arguments;
        return {
            "&title": {
                "default": "Tee haku"
            },
            create: function () {
                this
                    .click(function () {
                        require('./searchResultContainer').apply(this, args);
                    })
            }
        };
    };
});