define(function (require) {
    'use strict';

    return function (options, success) {
        return function () {
            var operationType = options.data.state.uiState === 'DRAFT' ? 'draft' : 'logical';
            require('./modal')({
                title: MetkaJS.L10N.get('confirmation.remove.revision.title'),
                // TODO: simpler/unified way to supplement localization keys/texts
                body: MetkaJS.L10N.get('confirmation.remove.revision.{operationType}.text'.supplant({
                    operationType: operationType
                })).supplant(options.data.key).supplant({
                    target: MetkaJS.L10N.get('confirmation.remove.revision.{operationType}.data.{type}'.supplant({
                        operationType: operationType,
                        type: options.data.configuration.type
                    }))
                }),
                buttons: [{
                    type: 'YES',
                    create: function () {
                        $(this)
                            .click(function () {
                                require('./server')('remove', {
                                    data: JSON.stringify(options.data),
                                    success: success
                                });
                            });
                    }
                }, {
                    type: 'NO'
                }]
            });
        };
    };
});
