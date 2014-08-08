define(function (require) {
    'use strict';

    var addRow;
    var $query;
    return function (onLoad) {
        var options = {
            header: MetkaJS.L10N.get('topmenu.expert'),
            content: [
                {
                    "type": "COLUMN",
                    "columns": 2,
                    "rows": [
                        {
                            "type": "ROW",
                            "cells": [
                                {
                                    "type": "CELL",
                                    "title": "Hakulause",
                                    "colspan": 1,
                                    "field": {
                                        "displayType": "STRING",
                                        "key": "search",
                                        "multiline": true
                                    },
                                    create: function () {
                                        $query = this.find('textarea');
                                    }
                                },
                                {
                                    "type": "CELL",
                                    "title": "Tallennetut haut",
                                    "colspan": 1,
                                    "field": {
                                        "readOnly": true,
                                        "displayType": "CONTAINER",
                                        "columnFields": [
                                            "name",
                                            "savedBy",
                                            "savedAt"
                                        ],
                                        onRemove: function ($row, remove) {
                                            require('./../server')('/expertSearch/remove/{id}', require('./../map/transferRow/object')($row.data('transferRow')), {
                                                success: function () {
                                                    $row.remove();
                                                }
                                            });
                                        },
                                        onClick: function () {
                                            $query
                                                .val($(this).data('transferRow').fields.query.value.current)
                                                .change();
                                        }
                                    },
                                    create: function (options) {
                                        var $containerField = $(this);
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
                                }
                            ]
                        }
                    ]
                }
            ],
            buttons: [
                require('./../searchButton')('/expertSearch/query', function () {
                    return {
                        query: require('./../data')(options)('search').get()
                    };
                }, function (data) {
                    return data.results;
                }, function (result) {
                    return {
                        title: result.title,
                        type: MetkaJS.L10N.get('type.{type}.title'.supplant(result)),
                        TYPE: result.type,
                        id: result.id,
                        revision: result.no,
                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                    };
                }, {
                    title: {
                        type: 'STRING'
                    },
                    type: {
                        type: 'STRING'
                    },
                    id: {
                        type: 'INTEGER'
                    },
                    revision: {
                        type: 'INTEGER'
                    },
                    state: {
                        type: 'STRING'
                    }
                }, [
                    "title",
                    "type",
                    "id",
                    "revision",
                    "state"
                ], function () {
                    var transferRow = $(this).data('transferRow');
                    require('./../assignUrl')('view', {
                        id: transferRow.fields.id.value.current,
                        revision: transferRow.fields.revision.value.current,
                        page: transferRow.fields.TYPE.value.current.toLowerCase()
                    });
                }),
                {
                    "&title": {
                        "default": "Tyhjennä"
                    },
                    create: function () {
                        this.click(function () {
                            $query
                                .val('')
                                .change();
                        });
                    }
                }, {
                    "&title": {
                        "default": "Tallenna haku"
                    },
                    create: function () {
                        this
                            .click(function () {
                                var containerOptions = {
                                    data: {},
                                    dataConf: {},
                                    content: [{
                                        type: 'COLUMN',
                                        columns: 1,
                                        rows: [
                                            {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "title": "Nimi",
                                                        "colspan": 1,
                                                        "field": {
                                                            "displayType": "STRING",
                                                            "key": "title"
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }]
                                };
                                require('./../modal')({
                                    title: 'Tallenna haku',
                                    body: require('./../container').call($('<div>'), containerOptions),
                                    buttons: [{
                                        "&title": {
                                            "default": 'Tallenna'
                                        },
                                        create: function () {
                                            this
                                                .click(function () {
                                                    require('./../server')('/expertSearch/save', {
                                                        data: JSON.stringify({
                                                            query: require('./../data')(options)('search').get(),
                                                            title: require('./../data')(containerOptions)('title').get()
                                                        }),
                                                        success: addRow
                                                    });
                                                });
                                        }
                                    }, {
                                        type: 'CANCEL'
                                    }]
                                });
                            });
                    }
                }
            ],
            data: {
            },
            dataConf: {
                fields: {
                    name: {
                        type: "STRING"
                    },
                    savedBy: {
                        type: "STRING"
                    },
                    savedAt: {
                        type: "DATE"
                    }
                }
            }
        };
        onLoad(options);
    }
});