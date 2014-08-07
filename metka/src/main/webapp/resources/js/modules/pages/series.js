define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var data = require('./../data');
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
                                        "key": "id"
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
                require('./../searchButton')('/revision/ajax/search', function () {
                    return {
                        type: require('./../../metka').PAGE,
                        searchApproved: data.get(options, 'searchApproved'),
                        searchDraft: data.get(options, 'searchDraft'),
                        searchRemoved: data.get(options, 'searchRemoved'),
                        values: {
                            id: data.get(options, 'id'),
                            seriesabbr: data.get(options, 'seriesabbr'),
                            seriesname: data.get(options, 'seriesname')
                        }
                    };
                }, function (data) {
                    return data.rows;
                }, function (result) {
                    return {
                        id: result.id,
                        revision: result.revision,
                        seriesabbr: result.values.seriesabbr,
                        seriesname: result.values.seriesname,
                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                    };
                }, {
                    id: {
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
                    "id",
                    "seriesabbr",
                    "seriesname",
                    "state"
                ], function () {
                    var transferRow = $(this).data('transferRow');
                    require('./../assignUrl')('view', {
                        id: transferRow.fields.id.value.current,
                        revision: transferRow.fields.revision.value.current
                    });
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
                                require('./../server')('create', {
                                    data: JSON.stringify({
                                        type: 'SERIES'
                                    }),
                                    success: function (response) {
                                        if (response.result === 'REVISION_CREATED') {
                                            require('./../assignUrl')('view', {
                                                id: response.data.key.id,
                                                revision: response.data.key.no,
                                                page: response.data.configuration.type.toLowerCase()
                                            });
                                        }
                                    }
                                });
                            });
                    }
                }
            ],
            data: {
                fields: {
                    searchApproved: {
                        value: {
                            current: true
                        }
                    },
                    searchDraft: {
                        value: {
                            current: true
                        }
                    }
                }
            },
            dataConf: {}
        };
        return function (onLoad) {
            onLoad(options);
        };
    } else {
        return require('./defaults');
    }
});