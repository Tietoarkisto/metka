define(function (require) {
    'use strict';

    return function(options) {
        this
            .removeAttr('data-dismiss')
            .click(require('./../../formAction')('edit')(options, function (response) {
                $.extend(options.data, response.data.data);
                $(this).trigger('refresh.metka');
            }, [
                'REVISION_FOUND',
                'REVISION_CREATED'
            ]));
    };
});