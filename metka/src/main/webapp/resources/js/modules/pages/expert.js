define(function (require) {
    'use strict';

    var addRow;
    var $query;
    return function (options, onLoad) {
        $.extend(options, {
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
                                            require('./../server')('/expert/remove/{id}', require('./../map/transferRow/object')($row.data('transferRow'), options.defaultLang), {
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
                                        require('./../server')('/expert/list', {
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
                require('./../searchButton')('/expert/query', function () {
                    return {
                        query: require('./../data')(options)('search').getByLang(options.defaultLang)
                    };
                }, function (data) {
                    return data.results;
                }, function (result) {
                    return {
                        title: result.title,
                        type: MetkaJS.L10N.get('type.{type}.title'.supplant(result)),
                        TYPE: result.type,
                        id: result.id,
                        no: result.no,
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
                    no: {
                        type: 'INTEGER'
                    },
                    state: {
                        type: 'STRING'
                    }
                }, [
                    "title",
                    "type",
                    "id",
                    "no",
                    "state"
                ], function (transferRow) {
                    log(transferRow);
                    require('./../assignUrl')('view', {
                        id: transferRow.fields.id.values.DEFAULT.current,
                        no: transferRow.fields.no.values.DEFAULT.current,
                        page: transferRow.fields.TYPE.values.DEFAULT.current.toLowerCase()
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
                                                    require('./../server')('/expert/save', {
                                                        data: JSON.stringify({
                                                            query: require('./../data')(options)('search').getByLang(options.defaultLang),
                                                            title: require('./../data')(containerOptions)('title').getByLang(options.defaultLang)
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
        });
        onLoad();
    }
});