define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;
        var $this = $(this);

        this.click(require('./../../save')(options, function(response) {
            // TODO: Check that if result is SAVE_SUCCESSFUL_WITH_ERRORS then don't close the dialog and instead reload data from TransferData
            if(response.result === 'SAVE_SUCCESSFUL_WITH_ERRORS') {
                $.extend(true, options.data, response.data);
                //options.$events.trigger('refresh.metka');
                $this.trigger('refresh.metka');
            } else {
                $('#'+options.modalTarget).modal('hide');
            }
            options.$events.trigger('attachment.refresh');
        }));
    };
});