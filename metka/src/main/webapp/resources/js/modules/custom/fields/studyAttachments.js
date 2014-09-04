define(function (require) {
    'use strict';

    return {
        create: function create(options) {
            var fieldOptions = {
                $events: $({}),
                defaultLang: options.defaultLang,
                dataConf: {
                    fields: {}
                },
                data: {
                    fields: {
                        relatedBinders: {
                            type: 'CONTAINER',
                            rows: {}
                        }
                    }
                },
                field: {
                    key: 'relatedBinders',
                    displayType: 'CONTAINER',
                    columnFields: [],
                    showSaveInfo: true,
                    showReferenceKey: true,
                    readOnly: true
                },
                '&title': options['&title'],
                buttons: [{
                    "&title": {
                        "default": MetkaJS.L10N.get('general.table.add')
                    },
                    create: function () {
                        this
                            .click(function () {
                                require('./../../server')('create', {
                                    data: JSON.stringify({
                                        type: 'STUDY_ATTACHMENT',
                                        parameters: {
                                            study: require('./../../../metka').id
                                        }
                                    }),
                                    success: function (response) {
                                        if (response.result === 'REVISION_CREATED') {
                                            require('./../../server')('viewAjax', {
                                                id: response.data.key.id,
                                                no: response.data.key.no,
                                                page: 'study_attachment'
                                            }, {
                                                method: 'GET',
                                                success: function (data) {
                                                    /*metka.revision = metka.no = data.transferData.key.no;
                                                    options.readOnly = !data.transferData.state.draft || !(data.transferData.state.handler === MetkaJS.User.userName);
                                                    options.dataConf = data.configuration;
                                                    options.data = data.transferData;
                                                    options.header = function header($header) {
                                                    }*/

                                                    require('./../../modal')({
                                                        title: 'Muokkaa tiedostoa',
                                                        data: data.transferData,
                                                        dataConf: data.configuration,
                                                        $events: $({}),
                                                        defaultLang: fieldOptions.defaultLang,
                                                        content: [],
                                                        buttons: [{
                                                            create: function () {
                                                                this
                                                                    .text(MetkaJS.L10N.get('general.buttons.ok'))
                                                                    .click(function () {
                                                                        var $tr = onClose(transferRow);
                                                                        if (options.field.onRowChange) {
                                                                            options.field.onRowChange($tr, transferRow);
                                                                        }
                                                                    });
                                                            }
                                                        }, {
                                                            type: 'DISMISS'
                                                        }]
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            })
                    }
                }]
            };

            var $field = this.children().first();
            require('./../../server')('/studyAttachments', {
                method: 'GET',
                success: function (data) {
                    if (data.pages) {
                        var objectToTransferRow = require('./../../map/object/transferRow');
                        fieldOptions.data.fields.relatedBinders.rows.DEFAULT = data.pages.map(function (result) {
                            result.binderDescription = result.description;
                            return objectToTransferRow(result, fieldOptions.defaultLang);
                        });
                    }
                    require('./../../field').call($field, fieldOptions);
                }
            });
        }
    };
});
