define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;

        function closeAndRefresh() {
            options.$events.trigger('attachment.refresh');
            $('#'+options.modalTarget).modal('hide');
        }
        this.click(require('./../../remove')($.extend({
            success: {
                SUCCESS_LOGICAL: closeAndRefresh,
                SUCCESS_DRAFT: closeAndRefresh,
                SUCCESS_REVISIONABLE: closeAndRefresh
            }
        }, options)));
    };
});