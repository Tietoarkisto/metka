define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
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
                                            "key": "id"
                                        }
                                    },
                                    {
                                        "type": "CELL",
                                        "title": "Hyväksyttyjä",
                                        "colspan": 1,
                                        "field": {
                                            // TODO: BOOLEAN
                                            "displayType": "CHECKBOX",
                                            "key": "searchApproved"
                                        }
                                    }
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
                                    {
                                        "type": "CELL",
                                        "title": "Luonnoksia",
                                        "colspan": 1,
                                        "field": {
                                            // TODO: BOOLEAN
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
                                            // TODO: BOOLEAN
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
                    require('./../searchButton')('searchAjax', function () {
                        log(options.defaultLang, data('searchApproved').getByLang(options.defaultLang))
                        return {
                            type: require('./../../metka').PAGE,
                            searchApproved: data('searchApproved').getByLang(options.defaultLang),
                            searchDraft: data('searchDraft').getByLang(options.defaultLang),
                            searchRemoved: data('searchRemoved').getByLang(options.defaultLang),
                            values: {
                                id: data('id').getByLang(options.defaultLang),
                                seriesabbr: data('seriesabbr').getByLang(options.defaultLang),
                                seriesname: data('seriesname').getByLang(options.defaultLang)
                            }
                        };
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
                    ], function (transferRow) {
                        require('./../assignUrl')('view', {
                            id: transferRow.fields.id.values.DEFAULT.current,
                            no: transferRow.fields.no.values.DEFAULT.current
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
                data: {
                    fields: {
                        searchApproved: {
                            type: 'VALUE',
                            values: {
                                DEFAULT: {
                                    current: true
                                }
                            }
                        },
                        searchDraft: {
                            type: 'VALUE',
                            values: {
                                DEFAULT: {
                                    current: true
                                }
                            }
                        }
                    }
                }
            });
            var data = require('./../data')(options);
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});