define(function (require) {
    var addRow;
    var $query;
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
                                        "savedAt",
                                        "remove"
                                    ]
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
        buttons: [{
            "&title": {
                "default": "Tee haku"
            },
            create: function () {
                this
                    .click(function () {
                        require('./../server')('/expertSearch/query', {
                            data: JSON.stringify({
                                query: require('./../data').get(options, 'search')
                            }),
                            success: function (data) {
                                var fieldOptions = {
                                    dataConf: {
                                        fields: {
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
                                        }
                                    },
                                    style: 'primary',
                                    readOnly: true,
                                    field: {
                                        displayType: 'CONTAINER',
                                        "key": "searchResults",
                                        "columnFields": [
                                            "title",
                                            "type",
                                            "id",
                                            "revision",
                                            "state"
                                        ]
                                    }
                                };
                                require('./../data').set(fieldOptions, 'searchResults', data.results.map(function (result) {
                                    return {
                                        title: result.title,
                                        type: MetkaJS.L10N.get('type.{type}.title'.supplant(result)),
                                        TYPE: result.type,
                                        id: result.id,
                                        revision: result.no,
                                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                                    };
                                }));
                                $('#searchResultTable').remove();
                                var $field = require('./../field').call($('<div>'), fieldOptions)
                                    .attr('id', 'searchResultTable');

                                $field.find('table')
                                    .addClass('table-hover')
                                    .find('tbody')
                                    .on('click', 'tr', function () {
                                        var $this = $(this);
                                        require('./../assignUrl')('view', {
                                            id: $this.data('id'),
                                            revision: $this.data('revision'),
                                            page: $this.data('TYPE').toLowerCase()
                                        });
                                    });

                                $field.find('.panel-heading')
                                    .text(MetkaJS.L10N.get('search.result.title'))
                                    .append($('<div class="pull-right">')
                                        .text(MetkaJS.L10N.get('search.result.amount').supplant(data.results)));

                                $('.content').append($field);
                            }
                        });
                    })
            }
        }, {
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
        }],
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
                },
                remove: {
                    type: "BUTTON"
                }
            }
        }
    };
    return options;
});