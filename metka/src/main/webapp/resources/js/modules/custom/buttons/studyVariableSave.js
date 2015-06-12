define(function (require) {
    'use strict';

    return function(options) {
        this.click(require('./../../save')(options, function (response) {
            options.$events.trigger('attachment.refresh');
            if(response.result === "OPERATION_SUCCESSFUL_WITH_ERRORS") {
                $.extend(options.data, response.data);
                options.$events.trigger('refresh.metka');
            } else {
                $('#'+options.modalTarget).modal('hide');
            }
        }));
    };
});