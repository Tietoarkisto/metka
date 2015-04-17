define(function (require) {
    'use strict';

    return function(options) {
        this.click(require('./../../remove')($.extend({
            success: {
                SUCCESS_LOGICAL: options.$events.trigger('attachment.refresh'),
                SUCCESS_DRAFT: options.$events.trigger('attachment.refresh'),
                FINAL_REVISION: options.$events.trigger('attachment.refresh')
            }
        }, options)));
    };
});