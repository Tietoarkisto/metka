define(function (require) {
    'use strict';

    return function (options) {
        options.field.displayType = "BOOLEAN";
        // TODO: Try to get rid of this or standardize it or something
        options.field.reverseBoolean = true;

        return {
            hidden: typeof require('./../../utils/getPropertyNS')(options.data, 'fields.parsed.values.DEFAULT.current') === 'undefined'
        }
    };
});