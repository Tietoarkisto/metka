define(function (require) {
    'use strict';

    // FIXME: Unify row edit and add new functionality so that it's only implemented once.

    var setContent;
    var dataConf = {
        fields: {
            pages: {
                type: 'CONTAINER',
                subfields: [
                    "studyId",
                    "studyTitle",
                    "binderId",
                    "description"
                ]
            },
            studyId: {
                type: 'STRING',
                subfield: true
            },
            studyTitle: {
                type: 'STRING',
                subfield: true
            },
            binderId: {
                type: 'INTEGER',
                subfield: true
            },
            description: {
                type: 'STRING',
                subfield: true
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
        function navigateToStudy(e, $tr, columns) {
            $tr.children().eq(columns.indexOf('studyId')).wrapInner('<a href="{url}"></a>'.supplant({
                url: require('./../url')('view', {
                    PAGE: 'STUDY',
                    id: $tr.data('transferRow').fields.study.values.DEFAULT.current,
                    no: ''
                })
            }));
        }

        function openBinder(e, $tr, columns) {
            var transferRow = $tr.data('transferRow');
            $tr.children().eq(columns.indexOf('binderId')).wrapInner('<a></a>').click(function() {
                var supplant = {
                    binderId: transferRow.fields.binderId.values.DEFAULT.current
                };
                require('./../modal')($.extend(true, require('./../optionsBase')(), {
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
                                            onRemove: MetkaJS.User.role.permissions.canEditBinderPages ? function ($tr) {
                                                require('./../server')('/binder/removePage/{pageId}', {
                                                    pageId: $tr.data('transferRow').fields.pageId.values.DEFAULT.current
                                                }, {
                                                    method: 'GET'
                                                });
                                                $tr.remove();
                                            } : false
                                        },
                                        preCreate: function () {
                                            var $containerField = $(this).children();
                                            $containerField.find('table')
                                                .removeClass('table-hover')
                                                .children('tbody').off('click', 'tr');
                                            $containerField.find('tbody').first().on('rowAppended', navigateToStudy);
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
                }));
            });
        }

        function addContainerRow($containerField) {
            return function binderToTransferRow(data) {
                var transferRow = require('./../map/object/transferRow')(data, options.defaultLang);
                delete transferRow.fields.saved;
                transferRow.saved = data.saved;
                $containerField.data('addRow')(transferRow);
            };
        }

        function reloadBinderData() {
            require('./../server')('/binder/listBinderPages', {
                method: 'GET',
                success: setContent
            });
        }

        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            fieldTitles: fieldTitles,
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
                                    "key": "pages",
                                    //"displayType": "CONTAINER",
                                    "columnFields": [
                                        "binderId",
                                        "studyId",
                                        "studyTitle",
                                        "description"
                                    ],
                                    onClick: function (transferRow) {
                                        require('./../modal')($.extend(true, require('./../optionsBase')(options), {
                                            data: {
                                                fields: transferRow.fields
                                            },
                                            fieldTitles: options.fieldTitles,
                                            title: "Muokkaa mapitusta",
                                            dataConf: {
                                                fields: {
                                                    binderId: {
                                                        type: "INTEGER"
                                                    },
                                                    studyId: {
                                                        type: "STRING"
                                                    },
                                                    studyTitle: {
                                                        type: "STRING"
                                                    },
                                                    description: {
                                                        type: "STRING"
                                                    }
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
                                                                    colspan: 1,
                                                                    readOnly: true,
                                                                    field: {
                                                                        key: "binderId"
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }, {
                                                    type: "COLUMN",
                                                    columns: 1,
                                                    rows: [
                                                        {
                                                            type: "ROW",
                                                            cells: [
                                                                {
                                                                    type: "CELL",
                                                                    colspan: 1,
                                                                    readOnly: true,
                                                                    field: {
                                                                        key: "studyId"
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }, {
                                                    type: "COLUMN",
                                                    columns: 1,
                                                    rows: [
                                                        {
                                                            type: "ROW",
                                                            cells: [
                                                                {
                                                                    type: "CELL",
                                                                    colspan: 1,
                                                                    readOnly: true,
                                                                    field: {
                                                                        key: "studyTitle"
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }, {
                                                    type: "COLUMN",
                                                    columns: 1,
                                                    rows: [
                                                        {
                                                            type: "ROW",
                                                            cells: [
                                                                {
                                                                    type: "CELL",
                                                                    colspan: 1,
                                                                    field: {
                                                                        key: "description",
                                                                        multiline: true
                                                                    }
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ],
                                            buttons: [
                                                {
                                                    type: "CUSTOM",
                                                    title: "Tallenna",
                                                    permissions: [
                                                        "canEditBinderPages"
                                                    ],
                                                    create: function(options) {
                                                        this.click(function() {
                                                            require('./../server')('/binder/saveBinderPage', {
                                                                data: JSON.stringify({
                                                                    pageId: require('./../data')(options)('pageId').getByLang(options.defaultLang),
                                                                    binderId: require('./../data')(options)('binderId').getByLang(options.defaultLang),
                                                                    studyId: require('./../data')(options)('studyId').getByLang(options.defaultLang),
                                                                    description: require('./../data')(options)('description').getByLang(options.defaultLang)
                                                                }),
                                                                success: function(data) {
                                                                    require('./../resultViewer')(data.result, null, function() {
                                                                        if (data.result === 'PAGE_UPDATED') {
                                                                            require('./../assignUrl')('/binder');
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        });
                                                    }
                                                },
                                                "CANCEL"
                                            ]
                                        }));
                                    }
                                },
                                postCreate: function (options) {
                                    var $containerField = $(this).children().first();
                                    var $tbody = $containerField.find('tbody').first();
                                    $tbody
                                        .on('rowAppended', navigateToStudy)
                                        .on('rowAppended', openBinder)
                                        .on('click', 'tr > td > a', function (event) {
                                            // prevent default row click action (open map dialog) from happening
                                            event.stopPropagation();
                                        });

                                    setContent = function (data) {
                                        data.pages && data.pages.forEach(addContainerRow($containerField));
                                    };
                                    reloadBinderData();
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
                        'canEditBinderPages'
                    ],
                    create: function () {
                        this
                            .click(function () {
                                var containerOptions = $.extend(true, require('./../optionsBase')(), {
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
                                                            pageId: null,
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
                                });

                                require('./../modal')(containerOptions);
                            });
                    }
                }
            ]
        });
        onLoad();
    };
});