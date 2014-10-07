define(function (require) {
    'use strict';

    return function(options) {

        // TODO: Get study data and check state and handler
        options.preventDismiss = true;

        this.click(function () {
            $(this).removeAttr("data-dismiss");
            require('./../../formAction')('edit')(options, function (response) {
                if (options.parentModal && (response.result === 'REVISION_FOUND' || response.result === 'REVISION_CREATED')) {
                    options.parentModal.refresh({
                        data: response.data
                    })
                }
            },
            [
                'REVISION_FOUND',
                'REVISION_CREATED'
            ]).call(this, arguments);
        });
    }
});