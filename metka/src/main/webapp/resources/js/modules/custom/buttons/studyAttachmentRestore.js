define(function (require) {
    'use strict';

    return function(options) {
        options.type = "RESTORE";

        options.request = {
            success: function() {options.$events.trigger('attachment.refresh')}
        };
    };
});