define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        $.extend(true, options, {
            field: {
                key: "relatedBinders",
                columnFields: [
                    'binderId',
                    'binderDescription'
                ]
            },
            dataConf: {
                fields: {
                    relatedBinders: {
                        type: "CONTAINER",
                        fixedOrder: true,
                        subfields: [
                            "binderId",
                            "binderDescription"
                        ]
                    },
                    binderId: {
                        type: 'INTEGER',
                        subfield: true
                    },
                    binderDescription: {
                        type: 'STRING',
                        subfield: true
                    }
                }
            },
            readOnly: true,
            '&title': options['&title']
        });

        return {
            create: function create(options) {
                var $field = this.children().first();
                require('./../../server')('/binder/listStudyBinderPages/{id}', {
                    method: 'GET',
                    success: function (data) {
                        if (data.pages) {
                            var objectToTransferRow = require('./../../map/object/transferRow');
                            $field.find("tbody").empty();
                            data.pages.map(function (result) {
                                result.binderDescription = result.description;
                                delete result.description;
                                $field.data("addRow")(objectToTransferRow(result, options.defaultLang));
                            });
                        }
                    }
                });
            }
        };
    }
});
