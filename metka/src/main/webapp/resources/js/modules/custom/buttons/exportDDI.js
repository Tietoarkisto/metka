define(function (require) {
    'use strict';

    return function(options) {
        this.click( function() {
            require('./../../modal')($.extend(true, require('./../../optionsBase')(), {
                '&title': {
                    default: "Lataa DDI"
                },
                $events: $({}),
                defaultLang: "DEFAULT",
                ignoreTranslate: true,
                dataConf: {
                    selectionLists: {
                        language_list: {
                            key: "language_list",
                            type: "VALUE",
                            default: "default",
                            options: [
                                {
                                    value: "default",
                                    title: "Suomi"
                                }, {
                                    value: "en",
                                    title: "Englanti"
                                }, {
                                    value: "sv",
                                    title: "Ruotsi"
                                }
                            ]
                        }
                    },
                    fields: {
                        language: {
                            key: "language",
                            type: "SELECTION",
                            selectionList: "language_list"
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
                                        title: "DDI kieli",
                                        field: {
                                            key: "language"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                buttons: [{
                    type: 'CUSTOM',
                    title: "Lataa",
                    parentData: options.data,
                    create: function(options) {
                        this.click(function() {
                            var $id = options.parentData.key.id;
                            var $no = options.parentData.key.no;
                            var request = {
                                id: $id,
                                no: $no,
                                language: require('./../../data')(options)("language").getByLang("DEFAULT")
                            };
                            require('./../../server')('/study/ddi/export', {
                                data: JSON.stringify(request),
                                success: function(response) {
                                    if(response.result === "OPERATION_SUCCESSFUL") {
                                        saveAs(new Blob([response.content], {type: "text/xml;charset=utf-8"}), "id_"+response.id+"_revision_"+response.no+"_ddi_"+response.language+".xml");
                                    }
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