define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;

        this.click(require('./../../formAction')('edit')(options, function (response) {
            $.extend(options.data, response.data);
            $.extend(require('./../../root')(options).content, response.gui.content);
            options.$events.trigger('refresh.metka');
        }, [
            'REVISION_FOUND',
            'REVISION_CREATED'
        ], "edit"));
    };
});