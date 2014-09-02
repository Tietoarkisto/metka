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
                            return objectToTransferRow(result, fieldOptions.defaultLang);
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
                                'score',
                                'section',
                                'subsection',
                                'language',
                                'summary',
                                'description',
                                'triggerDate',
                                'triggerTarget'
                            ]
                        },
                        id: {
                            summaryField: false
                        },
                        score: {
                            key: 'score',
                            type: 'SELECTION',
                            summaryField: true,
                            selectionList: 'score_list'
                        },
                        section: {
                            key: 'section',
                            type: 'SELECTION',
                            summaryField: true,
                            selectionList: 'section_list'
                        },
                        subsection: {
                            key: 'subsection',
                            type: 'SELECTION',
                            summaryField: false,
                            selectionList: 'subsection_list'

                        },
                        language: {
                            key: 'language',
                            type: 'SELECTION',
                            summaryField: false,
                            selectionList: 'language_list'
                        },
                        summary: {
                            key: 'summary',
                            type: 'STRING',
                            summaryField: true
                        },
                        description: {
                            key: 'description',
                            type: 'STRING',
                            summaryField: false,
                            "multiline": true
                        },
                        triggerDate: {
                            key: 'triggerDate',
                            type: 'DATETIME',
                            summaryField: false
                        },
                        triggerTarget: {
                            key: 'triggerTarget',
                            type: 'SELECTION',
                            summaryField: false
                        }
                    },
                    "selectionLists": {
                        "score_list": {
                            "key": "score_list",
                            "type": "VALUE",
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
                        "section_list": {
                            "key": "section_list",
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
                        "subsection_list": {
                            "key": "subsection_list",
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
                        "language_list": {
                            "key": "language_list",
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
                    columnFields: [],
                    showSaveInfo: true,
                    onRowChange: function ($tr, transferRow) {
                        require('./../../server')('/study/updateError/', {
                            data: JSON.stringify(require('./../../map/transferRow/object')(transferRow, fieldOptions.defaultLang)),
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
