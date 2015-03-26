define(function (require) {
    'use strict';

    var addRow;
    var $query;
    return function (options, onLoad) {
        var commonSearchBooleans = require('./../commonSearchBooleans');

        $.extend(options, {
            header: MetkaJS.L10N.get('topmenu.expert'),
            fieldTitles: {
                "id": {
                    "title" : "ID"
                },
                "name": {
                    "title" : "Nimi"
                },
                "no": {
                    "title" : "Revisio"
                },
                "state": {
                    "title" : "Tila"
                },
                "title": {
                    "title" : "Otsikko"
                },
                "type": {
                    "title" : "Tyyppi"
                }
            },
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
                                    "readOnly": true,
                                    "field": {
                                        "displayType": "CONTAINER",
                                        "showSaveInfo": true,
                                        "columnFields": [
                                            "name"
                                        ],
                                        onRemove: function ($row, remove) {
                                            require('./../server')('/expert/remove/{id}', require('./../map/transferRow/object')($row.data('transferRow'), options.defaultLang), {
                                                method: 'GET',
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
                                        addRow = function (query) {
                                            $containerField.data('addRow')(require('./../map/savedExpertSearchQuery/transferRow')(query, options.defaultLang));
                                        };
                                        require('./../server')('/expert/list', {
                                            method: 'GET',
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
                    return {
                        PAGE: transferRow.fields.TYPE.values.DEFAULT.current
                    };
                }, options),
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
                                require('./../modal')($.extend(true, require('./../optionsBase')(), {
                                    //title: 'Tallenna haku',
                                    type: "ADD",
                                    dialogTitle: {
                                        "ADD": "Tallenna haku"
                                    },
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
                                }));
                            });
                    }
                }
            ],
            data: commonSearchBooleans.initialData({}),
            dataConf: {
                fields: {
                    name: {
                        type: "STRING"
                    },
                    search: {
                        type: "STRING"
                    }
                }
            }
        });
        onLoad();
    }
});