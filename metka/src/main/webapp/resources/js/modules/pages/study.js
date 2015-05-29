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
                        {
                            key: 'producerrole',
                            rename: 'producers.producerrole',
                            exactValue: true
                        },
                        {
                            key:'datakind',
                            exactValue: true
                        },
                        {
                            key: 'anonymization',
                            exactValue: true
                        },
                        {
                            key: 'securityissues',
                            exactValue: true
                        },
                        {
                            key: 'termsofuse',
                            exactValue: true
                        },
                        {
                            key: 'newtermsofuse',
                            exactValue: true
                        },
                        {
                            key: 'agreementtype',
                            exactValue: true
                        },
                        {
                            key: 'depositortype',
                            exactValue: true
                        },
                        {
                            key: 'packageurn',
                            rename: 'packages.packageurn'
                        },
                        'abstract',
                        {
                            key: 'topic',
                            rename: 'topics.topic',
                            exactValue: true
                        },
                        {
                            key: 'country',
                            rename: 'countries.country'
                        },
                        {
                            key: 'analysisunit',
                            rename: 'analysis.analysisunit',
                            exactValue: true
                        },
                        {
                            key: 'timemethod',
                            rename: 'timemethods.timemethod',
                            exactValue: true
                        },
                        {
                            key: 'sampproc',
                            rename: 'sampprocs.sampproc',
                            exactValue: true
                        },
                        {
                            key: 'collmode',
                            rename: 'collmodes.collmode',
                            exactValue: true
                        }
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
                                    "title": "Käyttöoikeus",
                                    exactValue: true
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
                                                                "key": "studyid"
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
                                                                "key": "author"
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
                                                                "key": "series"
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
                                                preCreate: function() {
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
                                require('./../searchButton')('searchAjax', [
                                        'studyid',
                                        {
                                            key: 'author',
                                            rename: 'authors.author'
                                        },
                                        {
                                            key: 'authororganization',
                                            rename: 'authors.organisation'
                                        },
                                        {
                                            key: 'producername',
                                            rename: 'producers.organisation'
                                        },
                                        {
                                            key: 'series',
                                            exactValue: true
                                        },
                                        {
                                            key: 'publication',
                                            useSelectionText: false,
                                            rename: 'publications.value',
                                            exactValue: true
                                        },
                                        'aipcomplete',
                                        'termsofusechangedate',
                                        {
                                            key: 'timeperiod',
                                            rename: 'timeperiods.timeperiod'
                                        },
                                        {
                                            key: 'colltime',
                                            rename: 'colltime.colldate'
                                        },
                                        {
                                            key: 'collector',
                                            rename: 'collectors.author'
                                        }
                                    ].concat(importFromConfiguration)
                                , function (data) {
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
                                    permissions: [
                                        "canCreateRevision"
                                    ],
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
                                references: $.extend(true, response.configuration.references, {
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
                                    },
                                    seriesname_ref: {
                                        key: 'seriesname_ref',
                                        type: 'REVISIONABLE',
                                        target: 'SERIES',
                                        titlePath: 'seriesname'
                                    },
                                    publication_ref: {
                                        key: 'publication_ref',
                                        type: 'REVISIONABLE',
                                        target: 'PUBLICATION',
                                        titlePath: 'publicationid'
                                    },
                                    topictop_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.topicvocab_ref.target
                                    },
                                    topic_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.topicvocab_ref.target,
                                        valuePath: 'terms.term_ll.id'
                                    },
                                    analysisunit_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.analysisunitvocab_ref.target
                                    },
                                    timemethod_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.timemethodvocab_ref.target
                                    },
                                    collmode_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.collmodevocab_ref.target
                                    },
                                    sampproc_ref: {
                                        type: 'JSON',
                                        target: response.configuration.references.sampprocvocab_ref.target
                                    }
                                }),
                                selectionLists: $.extend(true, response.configuration.selectionLists, {
                                    analysisunit_list: {
                                        freeText: [],
                                        freeTextKey: null
                                    },
                                    timemethod_list: {
                                        freeText: [],
                                        freeTextKey: null
                                    },
                                    sampproc_list: {
                                        freeText: [],
                                        freeTextKey: null
                                    },
                                    collmode_list: {
                                        freeText: [],
                                        freeTextKey: null
                                    },
                                    seriesname_list: {
                                        includeEmpty: true,
                                        key: 'seriesname_list',
                                        type: 'REFERENCE',
                                        reference: 'seriesname_ref'
                                    },
                                    publication_list: {
                                        includeEmpty: true,
                                        key: 'publication_list',
                                        type: 'REFERENCE',
                                        reference: 'publication_ref'
                                    }
                                }),
                                fields: (function () {
                                    var fields = {};
                                    importFromConfiguration.map(function toKey(field) {
                                        if (typeof field === 'object') {
                                            return field.key;
                                        }
                                        return field;
                                    }).forEach(function (key) {
                                        fields[key] = $.extend(response.configuration.fields[key], {
                                            editable: true,
                                            immutable: false,
                                            required: false
                                        });
                                    });
                                    return $.extend(fields, {
                                        studyid: {
                                            type: response.configuration.fields.studyid.type
                                        },
                                        author: {
                                            type: response.configuration.fields.author.type
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
                                        series: {
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