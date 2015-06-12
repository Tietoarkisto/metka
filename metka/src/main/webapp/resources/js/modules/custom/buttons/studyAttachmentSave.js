define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;

        this.click(require('./../../save')(options, function(response) {
            if(response.result === 'OPERATION_SUCCESSFUL_WITH_ERRORS') {
                $.extend(options.data, response.data);
                options.$events.trigger('refresh.metka');
            } else {
                $('#'+options.modalTarget).modal('hide');
            }
            options.$events.trigger('attachment.refresh');
        }));
    };
});