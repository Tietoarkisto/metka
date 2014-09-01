define(function (require) {
    'use strict';

    var addRow;
    return function (options, onLoad) {
        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            dataConf: {
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
            },
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
                                    ],
                                    onClick: function () {
                                        require('./../assignUrl')('view', {
                                            id: $(this).data('transferRow').fields.id.values.DEFAULT.current,
                                            no: '???'
                                        });
                                    }
                                },
                                create: function (options) {
                                    var $containerField = $(this).children();
                                    addRow = function (data) {
                                        $containerField.data('addRowFromDataObject')(data);
                                    };
                                    var data = {
                                        pages: [{
                                            studyId: 'FSD123',
                                            studyTitle: 'wqewqeewrerw',
                                            savedBy: 'me',
                                            binderId: '30',
                                            description: 'asfasffdafsa'
                                        }]
                                    }
                                    //require('./../server')('/binder/listBinderPages', {
                                    //    success: function (data) {
                                            data.pages.forEach(addRow);
                                    //    }
                                    //});
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
                    }
                }
            ]
        });
        onLoad();
    };
});