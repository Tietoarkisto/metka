define(function (require) {
    'use strict';

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
                require('./../searchButton')('search', function () {
                    return {
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
                    };
                }, function (data) {
                    return data.searchData.results;
                }, function (result) {
                    return {
                        id: result.id,
                        revision: result.revision,
                        seriesid: result.id,
                        seriesabbr: result.values.seriesabbr,
                        seriesname: result.values.seriesname,
                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                    };
                }, {
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
                }, [
                    "seriesid",
                    "seriesabbr",
                    "seriesname",
                    "state"
                ], function () {
                    var $this = $(this);
                    MetkaJS.view($this.data('id'), $this.data('revision'));
                }),
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