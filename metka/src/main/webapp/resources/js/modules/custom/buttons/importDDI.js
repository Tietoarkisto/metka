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
                            require('./../../server')('/study/ddi/import', {
                                data: JSON.stringify({
                                    path: require('./../../data')(options)("ddiPath").getByLang("DEFAULT"),
                                    transferData: options.parentData
                                }),
                                success: function (response) {
                                    var dismiss = {
                                        type: 'DISMISS'
                                    };

                                    require('./../../modal')({
                                        title: MetkaJS.L10N.get(response === 'OPERATION_SUCCESSFUL' ? 'alert.notice.title' : 'alert.error.title'),
                                        body: response,
                                        buttons: [dismiss],
                                        modalEvents: {
                                            'hidden.bs.modal': function() {
                                                if (response === 'OPERATION_SUCCESSFUL') {
                                                    var $metka = require('../../../metka');
                                                    require('../../assignUrl')('view', {
                                                        PAGE: $metka.PAGE,
                                                        id: $metka.id,
                                                        no: $metka.no
                                                    });
                                                }
                                            }
                                        }
                                    });
                                }



                                /*var isExpectedResult = successConditions ? successConditions.some(function (condition) {
                                    return condition === response.result;
                                }) : true;
                                var dismiss = {
                                    type: 'DISMISS'
                                };
                                if (isExpectedResult) {
                                    dismiss.create = function () {
                                        this.click(function () {
                                            onSuccess.call(that, response);
                                        });
                                    };
                                }
                                require('./modal')({
                                    title: MetkaJS.L10N.get(isExpectedResult ? 'alert.notice.title' : 'alert.error.title'),
                                    body: response.result *//*data.errors.map(function (error) {
                                     return MetkaJS.L10N.get(error.msg);
                                     })*//*,
                                    buttons: [dismiss]
                                });*/



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