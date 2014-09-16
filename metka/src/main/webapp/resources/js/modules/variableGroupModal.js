define(function (require) {
    'use strict';

    return function (key, requestOptions, onSaveSuccess) {
        var metka = require('./../metka');
        require('./server')('viewAjax', $.extend({
            page: 'study_variable'
        }, requestOptions), {
            method: 'GET',
            success: function (data) {
                var modalOptions = $.extend(data.gui, {
                    title: 'Muokkaa muuttujaryhmää',
                    data: data.transferData,
                    dataConf: data.configuration,
                    $events: $({}),
                    defaultLang: 'DEFAULT',
                    large: true,
                    content: [
                        {
                            "type": "COLUMN",
                            "columns": 1,
                            "rows": [
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Nimi",
                                            "horizontal": true,
                                            "field": {
                                                "key": "varname"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Kysymystekstit",
                                            "field": {
                                                "key": "vargrouptexts",
                                                "columnFields": [
                                                    "vargrouptext"
                                                ]
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    buttons: [{
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.buttons.save'))
                                .click(require('./formAction')('save')(modalOptions, function (response) {
                                        require('./server')('/references/referenceRowRequest', {
                                            data: JSON.stringify({
                                                type: metka.PAGE,
                                                id: metka.id,
                                                no: metka.no,
                                                path: key,
                                                reference: response.data.key.id
                                            }),
                                            success: function (data) {
                                                onSaveSuccess(data.row);
                                            }
                                        });
                                    },
                                    [
                                        'SAVE_SUCCESSFUL',
                                        'SAVE_SUCCESSFUL_WITH_ERRORS',
                                        'NO_CHANGES_TO_SAVE'
                                    ]));
                        }
                    }, {
                        type: 'CANCEL'
                    }]
                });
                require('./modal')(modalOptions);
            }
        });
    };
});