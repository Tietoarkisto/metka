define(function (require) {
    'use strict';

    return function(options) {
        function closeAndRefresh() {
            log('trying to trigger');
            options.$events.trigger('attachment.refresh');
            $('#'+options.modalTarget).modal('hide');
        }
        this.click(require('./../../remove')($.extend({
            success: {
                SUCCESS_LOGICAL: closeAndRefresh,
                SUCCESS_DRAFT: closeAndRefresh,
                FINAL_REVISION: closeAndRefresh
            }
        }, options)));
    };
});