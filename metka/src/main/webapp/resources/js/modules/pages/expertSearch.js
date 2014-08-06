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
                                    "elementId": "savedSearches",
                                    "field": {
                                        "readOnly": true,
                                        "displayType": "CONTAINER",
                                        "key": "savedSearches",
                                        "columnFields": [
                                            "name",
                                            "savedBy",
                                            "savedAt"
                                        ],
                                        onRemove: function (row, remove) {
                                            require('./../server')('/expertSearch/remove/{id}', {
                                                id: row.data('id')
                                            }, function () {
                                                $row.remove();
                                            });
                                        }
                                    },
                                    create: function (options) {
                                        addRow = function (data) {
                                            data.name = data.title;
                                            delete data.title;
                                            options.addRow(data);
                                        };
                                        this
                                            .find('table')
                                                .addClass('table-hover')
                                                .find('tbody')
                                                    .on('click', 'tr', function () {
                                                        $query
                                                            .val($(this).data('query'))
                                                            .change();
                                                    });
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
                        query: require('./../data').get(options, 'search')
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
                    var $this = $(this);
                    require('./../assignUrl')('view', {
                        id: $this.data('id'),
                        revision: $this.data('revision'),
                        page: $this.data('TYPE').toLowerCase()
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
                                                            query: require('./../data').get(options, 'search'),
                                                            title: require('./../data').get(containerOptions, 'title')
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