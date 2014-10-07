define(function (require) {
    'use strict';

    return {
        create: function create(options) {
            function refreshData(callback) {

                require('./../../server')('/study/listErrors/{id}', {
                    method: 'GET',
                    success: function (response) {
                        var objectToTransferRow = require('./../../map/object/transferRow');
                        fieldOptions.data.fields.errors.rows.DEFAULT = response.errors.map(function (result) {
                            result.errorscore = result.errorscore + '';
                            var transferRow = objectToTransferRow(result, fieldOptions.defaultLang);
                            transferRow.saved = {
                                time : result.savedAt,
                                user : result.savedBy
                            };
                            return transferRow;
                        });
                        fieldOptions.$events.trigger('dataChanged');
                    }
                });
            }

            var fieldOptions = {
                $events: $({}),
                defaultLang: options.defaultLang,
                dataConf: {
                    fields: {
                        errors: {
                            subfields: [
                                'id',
                                'errorscore',
                                'errordatasetpart',
                                'errorpartsection',
                                'errorlanguage',
                                'errorlabel',
                                'errornotes',
                                'errortriggerdate',
                                'errortriggerpro'
                            ]
                        },
                        id: {
                            type: "INTEGER"
                        },
                        errorscore: {
                            key: 'errorscore',
                            type: 'SELECTION',
                            selectionList: 'errorscore_list'
                        },
                        errordatasetpart: {
                            key: 'errordatasetpart',
                            type: 'SELECTION',
                            selectionList: 'errordatasetpart_list'
                        },
                        errorpartsection: {
                            key: 'errorpartsection',
                            type: 'SELECTION',
                            selectionList: 'errorpartsection_list'

                        },
                        errorlanguage: {
                            key: 'errorlanguage',
                            type: 'SELECTION',
                            selectionList: 'errorlanguage_list'
                        },
                        errorlabel: {
                            key: 'errorlabel',
                            type: 'STRING'
                        },
                        errornotes: {
                            key: 'errornotes',
                            type: 'STRING',
                            "multiline": true
                        },
                        errortriggerdate: {
                            key: 'errortriggerdate',
                            type: 'DATE'
                        },
                        errortriggerpro: {
                            key: 'errortriggerpro',
                            type: 'SELECTION'
                        }
                    },
                    "selectionLists": {
                        "errorscore_list": {
                            "key": "errorscore_list",
                            "type": "LITERAL",
                            "options": [
                                {
                                    "value": "1"
                                },
                                {
                                    "value": "2"
                                },
                                {
                                    "value": "3"
                                },
                                {
                                    "value": "4"
                                },
                                {
                                    "value": "5"
                                }
                            ]
                        },
                        "errordatasetpart_list": {
                            "key": "errordatasetpart_list",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": "Perustiedot"
                                    },
                                    "value": "1"
                                },
                                {
                                    "&title": {
                                        "default": "Mapit"
                                    },
                                    "value": "2"
                                },
                                {
                                    "&title": {
                                        "default": "Arkistointisopimus"
                                    },
                                    "value": "3"
                                },
                                {
                                    "&title": {
                                        "default": "Aineisto"
                                    },
                                    "value": "4"
                                },
                                {
                                    "&title": {
                                        "default": "Muuttujat"
                                    },
                                    "value": "5"
                                },
                                {
                                    "&title": {
                                        "default": "Tiedostot"
                                    },
                                    "value": "6"
                                },
                                {
                                    "&title": {
                                        "default": "Koodikirja"
                                    },
                                    "value": "7"
                                }
                            ]
                        },
                        "errorpartsection_list": {
                            "key": "errorpartsection_list",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": "Perustiedot"
                                    },
                                    "value": "1"
                                },
                                {
                                    "&title": {
                                        "default": "Versiotiedot"
                                    },
                                    "value": "2"
                                },
                                {
                                    "&title": {
                                        "default": "Muut nimet"
                                    },
                                    "value": "3"
                                },
                                {
                                    "&title": {
                                        "default": "Tekijät ja tuottajat"
                                    },
                                    "value": "4"
                                },
                                {
                                    "&title": {
                                        "default": "Asiasanat, tieteenalat ja abstrakti"
                                    },
                                    "value": "5"
                                },
                                {
                                    "&title": {
                                        "default": "Kattavuus"
                                    },
                                    "value": "6"
                                },
                                {
                                    "&title": {
                                        "default": "Aineistonkeruu"
                                    },
                                    "value": "7"
                                },
                                {
                                    "&title": {
                                        "default": "Aineiston käyttö"
                                    },
                                    "value": "8"
                                },
                                {
                                    "&title": {
                                        "default": "Muut materiaalit"
                                    },
                                    "value": "9"
                                },
                                {
                                    "&title": {
                                        "default": "Tekijät"
                                    },
                                    "value": "10"
                                }
                            ]
                        },
                        "errorlanguage_list": {
                            "key": "errorlanguage_list",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": "Suomi"
                                    },
                                    "value": "default"
                                },
                                {
                                    "&title": {
                                        "default": "Englanti"
                                    },
                                    "value": "en"
                                },
                                {
                                    "&title": {
                                        "default": "Ruotsi"
                                    },
                                    "value": "sv"
                                }
                            ]
                        }
                    }
                },
                data: {
                    fields: {
                        errors: {
                            type: 'CONTAINER',
                            //var results = getResults(data);
                            rows: {}
                        }
                    }
                },
                field: {
                    displayType: 'CONTAINER',
                    key: "errors",
                    columnFields: [
                        'errorscore',
                        'errordatasetpart',
                        'errorpartsection',
                        'errorlanguage',
                        'errorlabel'
                    ],
                    showSaveInfo: true,
                    onRowChange: function ($tr, transferRow) {
                        var data = require('./../../map/transferRow/object')(transferRow, fieldOptions.defaultLang);
                        data.studyId = MetkaJS.revisionId;
                        data.errortriggerdate = moment(data.errortriggerdate).format('YYYY-MM-DD');
                        delete data.savedAt;
                        delete data.savedBy;
                        require('./../../server')('/study/updateError/', {
                            data: JSON.stringify(data),
                            success: function (response) {
                                refreshData();
                            }
                        });
                    },
                    onRemove: function ($tr) {
                        $tr.find('button').prop('disabled', true);
                        require('./../../server')('/study/removeError/{id}', {
                            id: $tr.data('transferRow').fields.id.values.DEFAULT.current
                        }, {
                            method: 'GET',
                            success: function () {
                                $tr.remove();
                            }
                        });
                    }
                },
                '&title': options['&title']
            };

            require('./../../field').call(this.children().first(), fieldOptions);

            refreshData();
        }
    };
});
