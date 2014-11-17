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
    var fieldTitles = {
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
    };

    return function (options, onLoad) {
        var commonSearchBooleans = require('./../commonSearchBooleans');

        function addContainerRow($containerField) {
            return function binderToTransferRow(data) {
                var transferRow = require('./../map/object/transferRow')(data, options.defaultLang);
                delete transferRow.fields.saved;
                transferRow.saved = data.saved;
                $containerField.data('addRow')(transferRow);
            };
        }

        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            fieldTitles: fieldTitles,
            data: commonSearchBooleans.initialData({}),
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
                                "readOnly": true,
                                "field": {
                                    "displayType": "CONTAINER",
                                    "columnFields": [
                                        "studyId",
                                        "studyTitle",
                                        "binderId",
                                        "description"
                                    ],
                                    onClick: function (transferRow) {
                                        var supplant = {
                                            binderId: transferRow.fields.binderId.values.DEFAULT.current
                                        };
                                        require('./../modal')({
                                            title: 'Mapin {binderId} sisältö'.supplant(supplant),
                                            fieldTitles: fieldTitles,
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
                                                                "readOnly": true,
                                                                "field": {
                                                                    "displayType": "CONTAINER",
                                                                    "showSaveInfo": true,
                                                                    "columnFields": [
                                                                        "studyId",
                                                                        "studyTitle",
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
                                                                            data.pages && data.pages.forEach(addContainerRow($containerField));
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
                                        /*require('./../assignUrl')('view', {
                                            PAGE: 'STUDY',
                                            id: transferRow.fields.study.values.DEFAULT.current
                                        });*/
                                    }
                                },
                                create: function (options) {
                                    var $containerField = $(this).children().first();
                                    var $tbody = $containerField.find('tbody').first();
                                    $tbody
                                        .on('rowAppended', function (e, $tr, columns) {
                                            $tr.children().eq(columns.indexOf('studyId')).wrapInner('<a href="{url}"></a>'.supplant({
                                                url: require('./../url')('view', {
                                                    PAGE: 'STUDY',
                                                    id: $tr.data('transferRow').fields.study.values.DEFAULT.current,
                                                    no: ''
                                                })
                                            }));
                                        })
                                        .on('click', 'tr > td > a', function (event) {
                                            // prevent default row click action (open map dialog) from happening
                                            event.stopPropagation();
                                        });

                                    setContent = function (data) {
                                        data.pages && data.pages.forEach(addContainerRow($containerField));
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
                        "default": "Lisää aineisto mappiin"
                    },
                    permissions: [
                        "canEditBinderPages"
                    ],
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