define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans');

        return function (options, onLoad) {
            require('./../server')('conf', {
                method: 'GET',
                success: function (response) {
                    var importFromConfiguration = [
                        'submissionid',
                        'title',
                        'producerrole',
                        'datakind',
                        'anonymization',
                        'securityissues',
                        'termsofuse',
                        'newtermsofuse',
                        'agreementtype',
                        'depositortype',
                        'packageurn',
                        'abstract',
                        'topic',
                        'country',
                        'analysisunit',
                        'timemethod',
                        'sampproc',
                        'collmode'
                    ];

                    if (response.result === 'CONFIGURATION_FOUND') {
                        $.extend(true, options, {
                            header: MetkaJS.L10N.get('type.STUDY.search'),
                            fieldTitles: {
                                "studyid": {
                                    "title": "Aineiston numero"
                                },
                                "studyname": {
                                    "title": "Aineiston nimi"
                                },
                                "authors": {
                                    "title": "Tekijät"
                                },
                                "series": {
                                    "title": "Sarja"
                                },
                                "datakind": {
                                    "title": "Laatu"
                                },
                                "termsofuse": {
                                    "title": "Käyttöoikeus"
                                },
                                "state": {
                                    "title": "Tila"
                                },
                                studyerrorsstudyid: {
                                    title: "Aineistonumero"
                                },
                                studyerrorstitle: {
                                    title: "Aineiston nimi"
                                },
                                studyerrorsscore: {
                                    title: "Virhepisteet"
                                }
                            },
                            content: [
                                {
                                    "type": "TAB",
                                    "title": "Aineistohaku",
                                    "content": [
                                        commonSearchBooleans.column,
                                        {
                                            "type": "COLUMN",
                                            "columns": 2,
                                            "rows": [
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aineiston numero",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "study.id"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Hankinta-aineistonumero",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "submissionid"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aineiston nimi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "title"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tekijän nimi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "author.author"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tekijän organisaatio",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "authororganization"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tuottajan nimi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "producername"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tuottajan rooli",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "producerrole"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Sarjan nimi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "seriesname"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aineiston laatu",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "datakind"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Anonymisointi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "anonymization"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tietosuoja",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "securityissues"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Julkaisu",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "publication"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Valmis-päivämäärä",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "aipcomplete"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Ehto 1: käyttöoikeus",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "termsofuse"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Käyttöehto muutospvm jälkeen",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "newtermsofuse"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Käyttöehdon muutospvm",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "termsofusechangedate"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Arkistointisopimuksen tapa",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "agreementtype"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Luovuttajan tyyppi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "depositortype"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Käsittelijä",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "handler"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "URN-tunniste",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "packageurn"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Abstrakti",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "abstract"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Tieteenala",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "topic"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Ajallinen kattavuus",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "timeperiod"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aineistonkeruun ajankohta",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "colltime"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Maa",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "country"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aineiston kerääjän nimi",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "collector"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Havainto/aineistoyksikkö",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "analysisunit"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Aikaulottuvuus",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "timemethod"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Otantamenetelmä",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "sampproc"
                                                            }
                                                        }
                                                    ]
                                                },
                                                {
                                                    "type": "ROW",
                                                    "cells": [
                                                        {
                                                            "type": "CELL",
                                                            "title": "Keruumenetelmä",
                                                            "horizontal": true,
                                                            "colspan": 2,
                                                            "field": {
                                                                "key": "collmode"
                                                            }
                                                        }
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "type": "TAB",
                                    "title": "Virheelliset",
                                    "hidePageButtons": true,
                                    "content": [{
                                        type: "COLUMN",
                                        columns: 1,
                                        rows: [{
                                            type: "ROW",
                                            cells: [{
                                                type: "CELL",
                                                title: "Virheelliset aineistot",
                                                readOnly: true,
                                                field: {
                                                    key: "studyerrors",
                                                    columnFields: [
                                                        "studyerrorsstudyid",
                                                        "studyerrorstitle",
                                                        "studyerrorsscore"
                                                    ],
                                                    onClick: function(transferRow, callback) {
                                                        require('./../assignUrl')('view', {
                                                            id: require('./../data').latestValue(transferRow.fields["studyerrorsid"], options.defaultLang),
                                                            no: ''
                                                        });

                                                    }
                                                },
                                                create: function() {
                                                    var $field = this.children().first();
                                                    require('./../server')('/study/studiesWithErrors', {
                                                        method: 'GET',
                                                        success: function (data) {
                                                            $field.find("tbody").empty();
                                                            if(data.rows && data.rows.length > 0) {
                                                                var objectToTransferRow = require('./../map/object/transferRow');
                                                                data.rows.map(function(row) {
                                                                    var studyerror = {
                                                                        studyerrorsid: row.id,
                                                                        studyerrorsscore: row.values["score"]
                                                                    };
                                                                    $field.data("addRow")(objectToTransferRow(studyerror, options.defaultLang));
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }]
                                        }]
                                    }]
                                }
                            ],
                            buttons: [
                                require('./../searchButton')('searchAjax', function () {
                                    var requestData = commonSearchBooleans.requestData(options, {
                                        type: require('./../../metka').PAGE,
                                        values: {}
                                    });
                                    [
                                        'study.id',
                                        'author.author',
                                        'authororganization',
                                        'producername',
                                        'seriesname',
                                        'publication',
                                        'publicationfirstsaved',
                                        'savedAt',
                                        'termsofusechangedate',
                                        'publication.savedBy',
                                        'timeperiod',
                                        'colltime',
                                        'collector'
                                    ].concat(importFromConfiguration).forEach(function (field) {
                                            requestData.values[field] = data(field).getByLang(options.defaultLang);
                                        });
                                    return requestData;
                                }, function (data) {
                                    return data.rows;
                                }, function (result) {
                                    return {
                                        id: result.id,
                                        no: result.no,
                                        studyid: result.values.studyid,
                                        studyname: result.values.title,
                                        authors: result.values.authors,
                                        series: result.values.series,
                                        datakind: result.values.datakind,
                                        termsofuse: result.values.termsofuse,
                                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                                    };
                                }, {
                                    studyid: {
                                        type: 'STRING'
                                    },
                                    studyname: {
                                        type: 'STRING'
                                    },
                                    authors: {
                                        type: 'STRING'
                                    },
                                    series : response.configuration.fields.series,
                                    datakind : response.configuration.fields.datakind,
                                    termsofuse : response.configuration.fields.termsofuse,
                                    state: {
                                        type: 'STRING'
                                    }
                                }, [
                                    "studyid",
                                    "studyname",
                                    "authors",
                                    "series",
                                    "datakind",
                                    "termsofuse",
                                    "state"
                                ], function () {}, options),
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
                                    create: function () {
                                        this
                                            .click(function () {
                                                require('./../server')('create', {
                                                    data: JSON.stringify({
                                                        type: 'STUDY',
                                                        parameters: {
                                                            submissionid: Date.now() % 1000,
                                                            dataarrivaldate: moment(Date.now()).format('YYYY-MM-DD')
                                                        }
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
                            data: commonSearchBooleans.initialData({}),
                            dataConf: {
                                key: response.configuration.key,
                                references: {
                                    studyerrorsid_ref: {
                                        type: "REVISIONABLE",
                                        target: "STUDY"
                                    },
                                    studyerrorsstudyid_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyerrorsid",
                                        valuePath: "studyid"
                                    },
                                    studyerrorstitle_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyerrorsid",
                                        valuePath: "title"
                                    }
                                },
                                selectionLists: $.extend(response.configuration.selectionLists, {
                                    seriesname_list: {
                                        "key": "seriesname_list",
                                        "type": "VALUE",
                                        "includeEmpty": true,
                                        "options": [
                                            {
                                                "value": "1",
                                                "&title": {
                                                    "default":"A"
                                                }
                                            }, {
                                                "value": "2",
                                                "&title": {
                                                    "default":"B"
                                                }
                                            }
                                        ]
                                    },
                                    publication_list: {
                                        "key": "publication_list",
                                        "type": "VALUE",
                                        "includeEmpty": true,
                                        "options": [
                                            {
                                                "value": "1",
                                                "&title": {
                                                    "default":"A"
                                                }
                                            }, {
                                                "value": "2",
                                                "&title": {
                                                    "default":"B"
                                                }
                                            }
                                        ]
                                    }
                                }),
                                fields: (function () {
                                    var fields = {};
                                    importFromConfiguration.forEach(function (key) {
                                        fields[key] = $.extend(response.configuration.fields[key], {
                                            editable: true,
                                            immutable: false,
                                            required: false
                                        });
                                    });
                                    return $.extend(fields, {
                                        study: {
                                            id: {
                                                type: response.configuration.fields.studyid.type
                                            }
                                        },
                                        author: {
                                            author: {
                                                type: response.configuration.fields.author.type
                                            }
                                        },
                                        // METKA_aineistohaut.docx "Organisaatiohakuun alkukatkaisu. Ei valita listasta."
                                        abstract: {
                                            "type": "STRING"
                                        },
                                        authororganization: {
                                            "type": "STRING"
                                        },
                                        producername: {
                                            "type": "STRING"
                                        },
                                        seriesname: {
                                            "type": "SELECTION",
                                            "selectionList": "seriesname_list"
                                        },
                                        publication: {
                                            "type": "SELECTION",
                                            "selectionList": "publication_list"
                                        },
                                        aipcomplete: {
                                            "type": "STRING"
                                        },
                                        termsofusechangedate: {
                                            "type": "STRING"
                                        },
                                        timeperiod: {
                                            "type": "STRING"
                                        },
                                        colltime: {
                                            "type": "STRING"
                                        },
                                        collector: {
                                            "type": "STRING"
                                        },
                                        handler: {
                                            "type": "STRING"
                                        },
                                        studyerrors: {
                                            type: "CONTAINER",
                                            subfields: [
                                                "studyerrorsid",
                                                "studyerrorsstudyid",
                                                "studyerrorstitle",
                                                "studyerrorsscore"
                                            ]
                                        },
                                        studyerrorsid: {
                                            key: "studyerrorsid",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorsid_ref"
                                        },
                                        studyerrorsstudyid: {
                                            key: "studyerrorsstudyid",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorsstudyid_ref"
                                        },
                                        studyerrorstitle: {
                                            key: "studyerrorstitle",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorstitle_ref"
                                        },
                                        studyerrorsscore: {
                                            key: "studyerrorsscore",
                                            subfield: true,
                                            type: "INTEGER"
                                        }
                                    });
                                })()
                            }
                        });
                        var data = require('./../data')(options);
                        onLoad();
                    }
                }
            });
        };
    } else {
        return require('./defaults');
    }
});