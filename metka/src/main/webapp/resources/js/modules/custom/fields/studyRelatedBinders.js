define(function (require) {
    'use strict';

    return {
        create: function create(options) {
            var fieldOptions = {
                $events: $({}),
                defaultLang: options.defaultLang,
                dataConf: {
                    fields: {
                        binderId: {
                            type: 'INTEGER'
                        },
                        binderDescription: {
                            type: 'STRING'
                        }
                    }
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
                    columnFields: [
                        'binderId',
                        'binderDescription'
                    ],
                    readOnly: true
                },
                '&title': options['&title']
            };

            var $field = this.children().first();
            require('./../../server')('/binder/listStudyBinderPages/{id}', {
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
