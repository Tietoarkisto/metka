define(function (require) {
    'use strict';

    var setContent;
    var dataConf = {
        fields: {
            studyId: {
                type: 'STRING'
            },
            studyTitle: {
                type: 'STRING'
            },
            binderId: {
                type: 'INTEGER'
            },
            description: {
                type: 'STRING'
            }
        }
    };

    return function (options, onLoad) {
        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            fieldTitles: {
                "studyId": {
                    "title": "Aineistonro"
                },
                "studyTitle": {
                    "title": "Aineiston nimi"
                },
                "savedBy": {
                    "title": "Käsittelijä"
                },
                "binderId": {
                    "title": "Mappinro"
                },
                "description": {
                    "title": "Mapitettu aineisto"
                }
            },
            dataConf: dataConf,
            content: [
                {
                    "type": "COLUMN",
                    "columns": 1,
                    "rows": [{
                        "type": "ROW",
                        "cells": [
                            {
                                "type": "CELL",
                                "title": "Mapitukset",
                                "colspan": 1,
                                "field": {
                                    "readOnly": true,
                                    "displayType": "CONTAINER",
                                    "columnFields": [
                                        "studyId",
                                        "studyTitle",
                                        "savedBy",
                                        "binderId",
                                        "description"
                                    ]
                                },
                                create: function (options) {
                                    var $containerField = $(this).children().first();
                                    var $tbody = $containerField.find('tbody').first();
                                    $tbody
                                        .on('rowAppended', function (e, $tr, columns) {
                                            $tr.children().eq(columns.indexOf('binderId')).wrapInner('<a href="javascript:void 0;"></a>')
                                        })
                                        .on('click', 'tr', function (event) {
                                            var $tr = $(this);
                                            require('./../assignUrl')('view', {
                                                page: 'study',
                                                id: $tr.data('transferRow').fields.study.values.DEFAULT.current
                                            });
                                        })
                                        .on('click', 'tr > td > a', function (event) {
                                            var supplant = {
                                                binderId: $(this).parent().parent().data('transferRow').fields.binderId.values.DEFAULT.current
                                            };
                                            require('./../modal')({
                                                title: 'Mapin {binderId} sisältö'.supplant(supplant),
                                                data: {},
                                                dataConf: dataConf,
                                                $events: $({}),
                                                defaultLang: options.defaultLang,
                                                large: true,
                                                content: [{
                                                    type: 'COLUMN',
                                                    columns: 1,
                                                    rows: [
                                                        {
                                                            "type": "ROW",
                                                            "cells": [
                                                                {
                                                                    "type": "CELL",
                                                                    "title": "Sisältö",
                                                                    "colspan": 1,
                                                                    "field": {
                                                                        "readOnly": true,
                                                                        "displayType": "CONTAINER",
                                                                        "columnFields": [
                                                                            "studyId",
                                                                            "studyTitle",
                                                                            "savedBy",
                                                                            "description"
                                                                        ],
                                                                        onRemove: function ($tr) {
                                                                            require('./../server')('/binder/removePage/{pageId}', {
                                                                                pageId: $tr.data('transferRow').fields.pageId.values.DEFAULT.current
                                                                            }, {
                                                                                method: 'GET'
                                                                            });
                                                                            $tr.remove();
                                                                        }
                                                                    },
                                                                    create: function () {
                                                                        var $containerField = $(this).children();
                                                                        require('./../server')('/binder/binderContent/{binderId}', supplant, {
                                                                            method: 'GET',
                                                                            success: function (data) {
                                                                                data.pages && data.pages.forEach(function (data) {
                                                                                    $containerField.data('addRowFromDataObject')(data);
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }],
                                                buttons: [{
                                                    type: 'DISMISS'
                                                }]
                                            });

                                            return false;
                                        })
                                        .parent().addClass('table-hover');

                                    setContent = function (data) {
                                        data.pages && data.pages.forEach(function (data) {
                                            $containerField.data('addRowFromDataObject')(data);
                                        });
                                    };
                                    require('./../server')('/binder/listBinderPages', {
                                        method: 'GET',
                                        success: setContent
                                    });
                                }
                            }
                        ]
                    }]
                }
            ],
            buttons: [
                {
                    "&title": {
                        "default": "Lataa CSV"
                    }
                },
                {
                    "&title": {
                        "default": "Lisää aineisto mappiin"
                    },
                    create: function () {
                        this
                            .click(function () {
                                var containerOptions = {
                                    title: 'Lisää aineisto mappiin',
                                    data: {},
                                    dataConf: {},
                                    $events: $({}),
                                    defaultLang: options.defaultLang,
                                    content: [{
                                        type: 'COLUMN',
                                        columns: 1,
                                        rows: [
                                            {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "title": "Mappinumero",
                                                        "colspan": 1,
                                                        "field": {
                                                            "displayType": "STRING",
                                                            "key": "binderId"
                                                        }
                                                    }
                                                ]
                                            }, {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "title": "Aineistonumero",
                                                        "colspan": 1,
                                                        "field": {
                                                            "displayType": "STRING",
                                                            "key": "studyId"
                                                        }
                                                    }
                                                ]
                                            }, {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "title": "Mapitettu aineisto",
                                                        "colspan": 1,
                                                        "field": {
                                                            "displayType": "STRING",
                                                            "key": "description",
                                                            multiline: true
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }],
                                    buttons: [{
                                        create: function () {
                                            this
                                                .text(MetkaJS.L10N.get('general.buttons.ok'))
                                                .click(function () {
                                                    require('./../server')('/binder/saveBinderPage', {
                                                        data: JSON.stringify({
                                                            binderId: require('./../data')(containerOptions)('binderId').getByLang(options.defaultLang),
                                                            studyId: require('./../data')(containerOptions)('studyId').getByLang(options.defaultLang),
                                                            description: require('./../data')(containerOptions)('description').getByLang(options.defaultLang)
                                                        }),
                                                        success: setContent
                                                    });
                                                });
                                        }
                                    }, {
                                        type: 'CANCEL'
                                    }]
                                };

                                require('./../modal')(containerOptions);
                            });
                    }
                }
            ]
        });
        onLoad();
    };
});