define(function (require) {
    'use strict';

    return function (options) {
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
                        this
                            .click(function () {
                                require('./server')('remove', {
                                    data: JSON.stringify(options.data),
                                    success: function (response) {
                                        var success = $.extend({
                                            SUCCESS_LOGICAL: function () {
                                                require('./assignUrl')('view');
                                            },
                                            SUCCESS_DRAFT: function () {
                                                require('./assignUrl')('view', {no: ''});
                                            },
                                            FINAL_REVISION: function () {
                                                require('./assignUrl')('searchPage');
                                            }
                                        }, options.success);

                                        success = success[response.result] || function (response) {
                                            require('./resultViewer')(response.result, "remove");
                                            /*require('./modal')({
                                                title: MetkaJS.L10N.get('alert.error.title'),
                                                body: response.result *//*data.errors.map(function (error) {
                                                 return MetkaJS.L10N.get(error.msg);
                                                 })*//*,
                                                buttons: [{
                                                    type: 'DISMISS'
                                                }]
                                            });*/
                                        };

                                        success(response);
                                    }
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
