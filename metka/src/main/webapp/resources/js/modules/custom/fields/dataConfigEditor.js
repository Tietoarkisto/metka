define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;
    };
});
