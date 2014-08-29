define(function (require) {
    'use strict';

    var addRow;
    return function (options, onLoad) {
        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
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
                                        "studyNo",
                                        "studyName",
                                        "mapNo",
                                        "mapped"
                                    ],
                                    onRemove: function ($row, remove) {
                                        require('./../server')('/expertSearch/remove/{id}', require('./../map/transferRow/object')($row.data('transferRow'), options.defaultLang), {
                                            success: function () {
                                                $row.remove();
                                            }
                                        });
                                    },
                                    onClick: function () {
                                        $query
                                            .val($(this).data('transferRow').fields.query.values.DEFAULT.current)
                                            .change();
                                    }
                                },
                                create: function (options) {
                                    var $containerField = $(this).children();
                                    addRow = function (data) {
                                        data.name = data.title;
                                        delete data.title;
                                        $containerField.data('addRowFromDataObject')(data);
                                    };
                                    require('./../server')('/expertSearch/list', {
                                        success: function (data) {
                                            data.queries.forEach(addRow);
                                        }
                                    });
                                }
                            },
                            {
                                "type": "CELL",
                                "field": {

                                },
                                create: function create(options) {
                                    require('./../searchResultContainer')(
                                        '/binder/listBinderPages',
                                        function () {
                                            return '';
                                        },
                                        function (data) {
                                            return data.pages || [];
                                        }, function (result) {
                                            return result;
                                        }, {
                                            title: {
                                                type: 'STRING'
                                            },
                                            id: {
                                                type: 'INTEGER'
                                            }
                                        }, ['title'], function (transferRow) {
                                            require('./../assignUrl')('view', {
                                                id: transferRow.fields.id.values.DEFAULT.current,
                                                no: '???'
                                            });
                                        }
                                    );
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