define(function (require) {
    'use strict';

    return function(options) {
        var $elem = $(this);

        this.click(function() {
            /*log('elem');
            $(this).trigger('refresh.metka');*/
            log('options');
            options.$events.trigger('refresh.metka');
        });
    };
});