define(function (require) {
    'use strict';

    return function(options) {
        this.click( function() {
            //require('./../../assignUrl')('ddiexport');
            require('./../../modal')({
                '&title': {
                    default: "Tuo DDI"
                },
                $events: $({}),
                defaultLang: "DEFAULT",
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
                    parentData: options.data,
                    create: function(options) {
                        this.click(function() {
                            var $page = options.parentData.configuration.type;
                            var $id = options.parentData.key.id;
                            var $no = options.parentData.key.no;
                            require('./../../server')('/revision/ddi/import', {
                                data: JSON.stringify({
                                    path: require('./../../data')(options)("ddiPath").getByLang("DEFAULT"),
                                    transferData: options.parentData
                                }),
                                success: function (response) {
                                    if (response.result === 'OPERATION_SUCCESSFUL') {
                                        require('./../../url')('view', {
                                            PAGE: $page,
                                            id: $id,
                                            no: $no
                                        })
                                    }
                                }
                            });
                        });
                    }
                }, {
                    type: 'DISMISS'
                }]
            });
        });
    };
});