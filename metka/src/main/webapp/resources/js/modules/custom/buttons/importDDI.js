define(function (require) {
    'use strict';

    return function(options) {
        this.click( function() {
            //require('./../../assignUrl')('ddiexport');
            require('./../../modal')($.extend(true, require('./../../optionsBase')(options), {
                '&title': {
                    default: "Tuo DDI"
                },
                $events: $({}),
                defaultLang: "DEFAULT",
                ignoreTranslate: true,
                dataConf: {
                    fields: {
                        ddiPath: {
                            key: "ddiPath",
                            type: "STRING"
                        }
                    }
                },
                data: {
                    fields: {

                    }
                },
                content: [
                    {
                        type: "COLUMN",
                        columns: 1,
                        rows: [
                            {
                                type: "ROW",
                                cells: [
                                    {
                                        type: "CELL",
                                        title: "DDI polku",
                                        field: {
                                            key: "ddiPath"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                buttons: [{
                    type: 'CUSTOM',
                    title: "Tuo",
                    create: function(options) {
                        this.click(function() {
                            var $page = require('./../../root')(options).data.configuration.type;
                            var $id = require('./../../root')(options).data.key.id;
                            var $no = require('./../../root')(options).data.key.no;
                            require('./../../server')('/study/ddi/import', {
                                data: JSON.stringify({
                                    path: require('./../../data')(options)("ddiPath").getByLang("DEFAULT"),
                                    transferData: require('./../../root')(options).data
                                }),
                                success: function (response) {
                                    require('./../../resultViewer')(response, null, function() {
                                        if (response === 'OPERATION_SUCCESSFUL') {
                                            var $metka = require('../../../metka');
                                            require('../../assignUrl')('view', {
                                                PAGE: $metka.PAGE,
                                                id: $metka.id,
                                                no: $metka.no
                                            });
                                        }
                                    });
                                }
                            });
                        });
                    }
                }, {
                    type: 'DISMISS'
                }]
            }));
        });
    };
});