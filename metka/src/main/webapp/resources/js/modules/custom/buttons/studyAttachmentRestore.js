define(function (require) {
    'use strict';

    return function(options) {
        options.type = "RESTORE";

        options.request = {
            success: options.$events.trigger('attachment.refresh')
        };
    };
});