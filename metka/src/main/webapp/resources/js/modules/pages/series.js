define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans');
        return function (options, onLoad) {
            $.extend(options, {
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
                                            "displayType": "SELECTION",
                                            "key": "seriesabbr"
                                        },
                                        create: function () {
                                            var $input = $(this).find('select');
                                            require('./../server')('/series/getAbbreviations', {
                                                method: 'GET',
                                                success: function (data) {
                                                    if (data.abbreviations) {
                                                        $input.append(data.abbreviations.map(function (option) {
                                                            return $('<option>')
                                                                .val(option)
                                                                .text(option);
                                                        }));
                                                    }
                                                }
                                            });
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
                    require('./../searchButton')('searchAjax', function () {
                        return commonSearchBooleans.requestData(options, {
                            type: require('./../../metka').PAGE,
                            values: {
                                'key.id': data('key.id').getByLang(options.defaultLang),
                                seriesabbr: data('seriesabbr').getByLang(options.defaultLang),
                                seriesname: data('seriesname').getByLang(options.defaultLang)
                            }
                        });
                    }, function (data) {
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
                    ]),
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
                                                    no: response.data.key.no,
                                                    page: response.data.configuration.type.toLowerCase()
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