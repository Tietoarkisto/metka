define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            prepareModal: function(modalOptions) {
                log('test');
            }
        }
    };
});
