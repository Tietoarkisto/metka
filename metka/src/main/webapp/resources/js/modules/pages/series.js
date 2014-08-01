define(function (require) {
    if (location.pathname.split('/').indexOf('search') !== -1) {
        var options = {
            header: MetkaJS.L10N.get('type.SERIES.search'),
            content: [
                {
                    "type": "COLUMN",
                    "columns": 3,
                    "rows": [
                        {
                            "type": "ROW",
                            "cells": [
                                {
                                    "type": "CELL",
                                    "title": "ID",
                                    "colspan": 2,
                                    "field": {
                                        "displayType": "INTEGER",
                                        "key": "seriesid"
                                    }
                                },
                                {
                                    "type": "CELL",
                                    "title": "Hyväksyttyjä",
                                    "colspan": 1,
                                    "field": {
                                        "displayType": "CHECKBOX",
                                        "key": "searchApproved"
                                    }
                                }
                            ]
                        },
                        {
                            "type": "ROW",
                            "cells": [
                                // TODO: tyypiksi select ja vaihtoehdot {searchData.abbreviations}
                                {
                                    "type": "CELL",
                                    "title": "Lyhenne",
                                    "colspan": 2,
                                    "field": {
                                        "displayType": "STRING",
                                        "key": "seriesabbr"
                                    }
                                },
                                {
                                    "type": "CELL",
                                    "title": "Luonnoksia",
                                    "colspan": 1,
                                    "field": {
                                        "displayType": "CHECKBOX",
                                        "key": "searchDraft"
                                    }
                                }
                            ]
                        },
                        {
                            "type": "ROW",
                            "cells": [
                                {
                                    "type": "CELL",
                                    "title": "Nimi",
                                    "colspan": 2,
                                    "field": {
                                        "displayType": "STRING",
                                        "key": "seriesname"
                                    }
                                },
                                {
                                    "type": "CELL",
                                    "title": "Poistettuja",
                                    "colspan": 1,
                                    "field": {
                                        "displayType": "CHECKBOX",
                                        "key": "searchRemoved"
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            buttons: [
                {
                    "&title": {
                        "default": "Tee haku"
                    },
                    create: function () {
                        this
                            .click(function () {
                                require('./../server')('search', {
                                    data: JSON.stringify({
                                        query: {
                                            searchApproved: require('./../data').get(options, 'searchApproved'),
                                            searchDraft: require('./../data').get(options, 'searchDraft'),
                                            searchRemoved: require('./../data').get(options, 'searchRemoved'),
                                            values: {
                                                seriesid: require('./../data').get(options, 'seriesid'),
                                                seriesabbr: require('./../data').get(options, 'seriesabbr'),
                                                seriesname: require('./../data').get(options, 'seriesname')
                                            }
                                        }
                                    }),
                                    success: function (data) {
                                        require('./../data').set(options, 'searchResults', data.searchData.results.map(function (result) {
                                            return {
                                                id: result.id,
                                                revision: result.revision,
                                                seriesid: result.id,
                                                seriesabbr: result.values.seriesabbr,
                                                seriesname: result.values.seriesname,
                                                state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                                            };
                                        }));
                                        $('#searchResultTable').remove();
                                        var $field = require('./../field').call($('<div>'), {
                                            dataConf: {
                                                fields: {
                                                    seriesid: {
                                                        type: 'INTEGER'
                                                    },
                                                    seriesabbr: {
                                                        type: 'STRING'
                                                    },
                                                    seriesname: {
                                                        type: 'STRING'
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
                                                key: "searchResults",
                                                columnFields: [
                                                    "seriesid",
                                                    "seriesabbr",
                                                    "seriesname",
                                                    "state"
                                                ]
                                            }
                                        })
                                            .attr('id', 'searchResultTable');
                                        $field.find('table')
                                            .addClass('table-hover')
                                            .find('tbody')
                                            .on('click', 'tr', function () {
                                                var $this = $(this);
                                                MetkaJS.view($this.data('id'), $this.data('revision'));
                                            });
                                        $field.find('.panel-heading')
                                            .text(MetkaJS.L10N.get('search.result.title'))
                                            .append($('<div class="pull-right">')
                                                .text(MetkaJS.L10N.get('search.result.amount').supplant(data.searchData.results)));

                                        $('.content').append($field);
                                    }
                                });
                            })
                    }
                },
                {
                    "&title": {
                        "default": "Tyhjennä"
                    },
                    create: function () {
                        this.click(function () {
                            log('TODO: tyhjennä lomake')
                        });
                    }
                },
                {
                    "&title": {
                        "default": "Lisää uusi"
                    },
                    create: function () {
                        this
                            .click(function () {
                                require('./../assignUrl')('seriesAdd');
                            });
                    }
                }
            ],
            data: {},
            dataConf: {}
        };
        return options;
    } else {
        return require('./defaults');
    }
});