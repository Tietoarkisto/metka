define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans');
        return function (options, onLoad) {
            $.extend(options, {
                header: MetkaJS.L10N.get('type.SERIES.search'),
                // TODO: try to add reference options request and remove custom "getAbbreviations" request
                dataConf: {
                    key: {
                        version: 1,
                        type: 'SERIES'
                    },
                    selectionLists: {
                        seriesabbr_list: {
                            includeEmpty: true,
                            key: 'seriesabbr_list',
                            type: 'REFERENCE',
                            reference: 'seriesabbr_ref'
                        }
                    },
                    references: {
                        seriesabbr_ref: {
                            key: 'seriesabbr_ref',
                            type: 'REVISIONABLE',
                            target: 'SERIES',
                            valuePath: 'seriesabbr',
                            titlePath: 'seriesabbr'
                        }
                    },
                    fields: {
                        seriesabbr: {
                            key: 'seriesabbr',
                            type: 'SELECTION',
                            selectionList: 'seriesabbr_list'
                        }
                    }
                },
                fieldTitles: {
                    "id": {
                        "title" : "ID"
                    },
                    "seriesabbr": {
                        "title" : "Lyhenne"
                    },
                    "seriesname": {
                        "title" : "Nimi"
                    },
                    "state": {
                        "title" : "Tila"
                    }
                },
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
                                            "key": "key.id"
                                        }
                                    },
                                    commonSearchBooleans.cells.approved
                                ]
                            },
                            {
                                "type": "ROW",
                                "cells": [
                                    {
                                        "type": "CELL",
                                        "title": "Lyhenne",
                                        "colspan": 2,
                                        "field": {
                                            "key": "seriesabbr"
                                        }
                                    },
                                    commonSearchBooleans.cells.draft
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
                                    commonSearchBooleans.cells.removed
                                ]
                            }
                        ]
                    }
                ],
                buttons: [
                    require('./../searchButton')('searchAjax', [
                        'key.id',
                        {
                            key: 'seriesabbr',
                            exactValue: true
                        },
                        'seriesname'
                    ], function (data) {
                        return data.rows;
                    }, function (result) {
                        return {
                            id: result.id,
                            no: result.no,
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
                    ], null, options),
                    /*{
                        "&title": {
                            "default": "Tyhjennä"
                        },
                        create: function () {
                            this.click(function () {
                                log('TODO: tyhjennä lomake')
                            });
                        }
                    },*/
                    {
                        "&title": {
                            "default": "Lisää uusi"
                        },
                        "permissions": [
                            "canCreateRevision"
                        ],
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
                                                    no: response.data.key.no
                                                });
                                            }
                                        }
                                    });
                                });
                        }
                    }
                ],
                data: commonSearchBooleans.initialData({})
            });
            var data = require('./../data')(options);
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});