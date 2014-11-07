define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;
        this.click(require('./../../formAction')('edit')(options, function (response) {
            $.extend(options.data, response.data);
            $(this).trigger('refresh.metka');
        }, [
            'REVISION_FOUND',
            'REVISION_CREATED'
        ]));
    };
});