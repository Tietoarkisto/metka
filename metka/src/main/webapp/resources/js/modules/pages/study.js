/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var resultParser = require('./../resultParser');
        var studySearchBooleans = require('./../commonSearchBooleans')();
        var studyErrorSearchBooleans = require('./../commonSearchBooleans')('error');

        return function (options, onLoad) {
            var errorSearch = require('./../searchRequestSearch')(options, [
                {
                    key: 'key.configuration.type',
                    value: "STUDY_ERROR",
                    addParens: false
                }, {
                    key: 'findBinderId',
                    rename: 'binderid',
                    exactValue: false
                }, {
                    key: 'findstudyid',
                    rename: 'studyid',
                    exactValue: true
                }
            ], 'studyerrors', 'DEFAULT', 'error');

            function viewError(requestOptions) {
                require('./../revisionModal')(options, requestOptions, 'STUDY_ERROR', errorSearch.search, 'studyerrors');
            }

            require('./../server')('conf', {
                method: 'GET',
                success: function (response) {
                    var importFromConfiguration = [
                        'submissionid',
                        {
                            key: 'title',
                            exactValue: true,
                            addParens: true,
                            addWildcard: true
                        },
                        {
                            key: 'producerrole',
                            rename: 'producers.producerrole',
                            exactValue: true
                        }, {
                            key: 'datakind',
                            exactValue: true
                        }, {
                            key: 'anonymization',
                            exactValue: true
                        }, {
                            key: 'securityissues',
                            exactValue: true
                        }, {
                            key: 'termsofuse',
                            exactValue: true
                        }, {
                            key: 'newtermsofuse',
                            exactValue: true
                        }, {
                            key: 'agreementtype',
                            exactValue: true
                        }, {
                            key: 'depositortype',
                            exactValue: true
                        }, {
                            key: 'packageurn',
                            rename: 'packages.packageurn'
                        },
                        {
                            key: 'abstract',
                            exactValue: true,
                            addParens: true,
                            addWildcard: true
                        },
                        {
                            key: 'topictop',
                            rename: 'topics.topictop',
                            exactValue: true
                        }, {
                            key: 'topic',
                            rename: 'topics.topic',
                            exactValue: true
                        }, {
                            key: 'country',
                            rename: 'countries.country'
                        }, {
                            key: 'analysisunit',
                            rename: 'analysis.analysisunit',
                            exactValue: true
                        }, {
                            key: 'timemethod',
                            rename: 'timemethods.timemethod',
                            exactValue: true
                        }, {
                            key: 'sampproc',
                            rename: 'sampprocs.sampproc',
                            exactValue: true
                        }, {
                            key: 'collmode',
                            rename: 'collmodes.collmode',
                            exactValue: true
                        }
                    ];

                    var studySearch = require('./../searchRequestSearch')(options, [
                        {
                            key: 'key.configuration.type',
                            value: "STUDY",
                            addParens: false
                        },
                        'studyid',
                        {
                            key: 'author',
                            rename: 'authors.author',
                            exactValue: true,
                            addParens: true,
                            addWildcard: true
                        }, {
                            key: 'authororganization',
                            rename: 'authors.organisation',
                            exactValue: true,
                            addParens: true,
                            addWildcard: true
                        }, {
                            key: 'producername',
                            rename: 'producers.organisation',
                            exactValue: true,
                            addParens: true,
                            addWildcard: true
                        }, {
                            key: 'series',
                            exactValue: true
                        }, {
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
                        }, {
                            key: 'colltime',
                            rename: 'colltime.colldate'
                        }, {
                            key: 'collector',
                            rename: 'collectors.author'
                        }
                    ].concat(importFromConfiguration), 'studyresults', 'DEFAULT');
                    if (resultParser(response.result).getResult() === 'CONFIGURATION_FOUND') {
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
                                studyresultsid: {
                                    title: "Aineistonumero"
                                },
                                studyresultsdatakind: {
                                    title: "Laatu"
                                },
                                studyresultstermsofuse: {
                                    title: "Käyttöoikeus"
                                },
                                studyresultsseries: {
                                    title: "Sarja"
                                },
                                studyresultstitle: {
                                    title: "Aineiston nimi"
                                },
                                studyerrorsstudytitle: {
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
                                    "hidePageButtons": true,
                                    "content": [
                                        {
                                            "type": "COLUMN",
                                            "rows": [{
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "contentType": "BUTTON",
                                                    "button": {
                                                        "&title": {
                                                            "default": "Lisää uusi"
                                                        },
                                                        permissions: [
                                                            "canCreateRevision"
                                                        ],
                                                        create: function () {
                                                            this.click(function () {
                                                                require('./../server')('create', {
                                                                    data: JSON.stringify({
                                                                        type: 'STUDY',
                                                                        parameters: {
                                                                            submissionid: Date.now() % 1000,
                                                                            dataarrivaldate: moment(Date.now()).format('YYYY-MM-DD')
                                                                        }
                                                                    }),
                                                                    success: function (response) {
                                                                        if (resultParser(response.result).getResult() === 'REVISION_CREATED') {
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
                                                }]
                                            }]
                                        },
                                        studySearchBooleans.column,
                                        {
                                            "type": "COLUMN",
                                            "columns": 2,
                                            "rows": [{
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineiston numero",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "studyid"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Hankinta-aineistonumero",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "submissionid"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineiston nimi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "title"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tekijän nimi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "author"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tekijän organisaatio",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "authororganization"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tuottajan nimi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "producername"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tuottajan rooli",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "producerrole"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Sarjan nimi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "series"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineiston laatu",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "datakind"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Anonymisointi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "anonymization"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tietosuoja",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "securityissues"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Julkaisu",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "publication"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Valmis-päivämäärä",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "aipcomplete"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Ehto 1: käyttöoikeus",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "termsofuse"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Käyttöehto muutospvm jälkeen",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "newtermsofuse"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Käyttöehdon muutospvm",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "termsofusechangedate"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Arkistointisopimuksen tapa",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "agreementtype"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Luovuttajan tyyppi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "depositortype"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Käsittelijä",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "handler"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "URN-tunniste",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "packageurn"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Abstrakti",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "abstract"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Pääala",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "topictop"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Tieteenala",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "topic"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Ajallinen kattavuus",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "timeperiod"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineistonkeruun ajankohta",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "colltime"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Maa",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "country"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineiston kerääjän nimi",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "collector"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Havainto/aineistoyksikkö",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "analysisunit"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aikaulottuvuus",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "timemethod"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Otantamenetelmä",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "sampproc"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Keruumenetelmä",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "collmode"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "contentType": "BUTTON",
                                                        "button": {
                                                            "title": MetkaJS.L10N.get('general.buttons.search'),
                                                            "create": function () {
                                                                this.click(studySearch.search);
                                                            }
                                                        }
                                                    }
                                                ]
                                            }
                                            ]
                                        },
                                        {
                                            "type": "COLUMN",
                                            "columns": 1,
                                            "rows": [{
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineistohaun tulokset",
                                                    "colspan": 1,
                                                    "readOnly": true,
                                                    "field": {
                                                        "key": "studyresults",
                                                        "showRowAmount": true,
                                                        "allowDownload": true,
                                                        "disableRemoval": true,
                                                        "rowsPerPage": 100,
                                                        "showReferenceState": true,

                                                        // "showReferenceValue": true,
                                                        // "showReferenceType": true,
                                                        // "showSaveInfo": true,
                                                        // "showReferenceSaveInfo": true,
                                                        // "showReferenceApproveInfo": ["DEFAULT"],

                                                        "columnFields": [
                                                            "studyresultsid",
                                                            "studyresultstitle",
                                                            "studyresultsseries",
                                                            "studyresultsdatakind",
                                                            "studyresultstermsofuse"
                                                        ],
                                                        onClick: function (transferRow) {
                                                            require('./../assignUrl')('view', {
                                                                    id: transferRow.value.split('-')[0],
                                                                    no: transferRow.value.split('-')[1]
                                                                }
                                                            );
                                                        }
                                                    }
                                                }]
                                            }]
                                        }]
                                },
                                {
                                    "type": "TAB",
                                    "title": "Virheelliset",
                                    "hidePageButtons": true,
                                    "content": [
                                        studyErrorSearchBooleans.column,
                                        {
                                            "type": "COLUMN",
                                            "columns": 2,
                                            "rows": [{
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Aineistonro",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "findstudyid"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Pisteet",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "finderrorscore"
                                                    }
                                                }, {
                                                    "type": "CELL",
                                                    "contentType": "BUTTON",
                                                    "button": {
                                                        "title": MetkaJS.L10N.get('general.buttons.search'),
                                                        "create": function () {
                                                            this.click(errorSearch.search);
                                                        }
                                                    }
                                                }]
                                            }]
                                        }, {
                                            "type": "COLUMN",
                                            "columns": 1,
                                            "rows": [{
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": "Virheet",
                                                    "colspan": 1,
                                                    "field": {
                                                        "key": "studyerrors",
                                                        "showRowAmount": true,
                                                        "allowDownload": true,
                                                        "disableRemoval": true,
                                                        "showReferenceValue": true,
                                                        "showReferenceState": true,
                                                        "columnFields": [
                                                            "studyerrorsstudyid",
                                                            "studyerrorsstudytitle",
                                                            "studyerrorsscore"
                                                        ],
                                                        onClick: function (transferRow) {
                                                            viewError({
                                                                id: transferRow.value.split('-')[0],
                                                                no: transferRow.value.split('-')[1]
                                                            });
                                                        },
                                                        onAdd: function () {
                                                            require('./../server')('create', {
                                                                data: JSON.stringify({
                                                                    type: 'STUDY_ERROR'
                                                                }),
                                                                success: function (response) {
                                                                    if (resultParser(response.result).getResult() === 'REVISION_CREATED') {
                                                                        viewError({
                                                                            type: 'STUDY_ERROR',
                                                                            id: response.data.key.id,
                                                                            no: response.data.key.no
                                                                        });
                                                                    } else {
                                                                        require('./../resultViewer')(response.result);
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                }]
                                            }]
                                        }]
                                }
                            ],
                            data: studySearchBooleans.initialData(studyErrorSearchBooleans.initialData({})),
                            dataConf: {
                                key: response.configuration.key,
                                references: $.extend(true, response.configuration.references, {
                                    studyerrors_ref: {
                                        type: "REVISION",
                                        target: "STUDY_ERROR"
                                    },
                                    studyresults_ref: {
                                        type: "REVISION",
                                        target: "STUDY"
                                    },
                                    studyerrorsstudyid_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyerrors",
                                        valuePath: "study"
                                    },
                                    studyresultsid_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyresults",
                                        valuePath: "studyid"
                                    },
                                    studyresultsseries_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyresults",
                                        valuePath: "series"
                                    },
                                    studyresultsdatakind_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyresults",
                                        valuePath: "datakind"
                                    },
                                    studyresultstermsofuse_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyresults",
                                        valuePath: "termsofuse"
                                    },
                                    studyresultstitle_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyresults",
                                        valuePath: "title"
                                    },
                                    studyerrorsstudytitle_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyerrors",
                                        valuePath: "studytitle"
                                    },
                                    studyerrorsscore_ref: {
                                        type: "DEPENDENCY",
                                        target: "studyerrors",
                                        valuePath: "errorscore"
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
                                    },
                                    "finderrorscore_list": {
                                        "key": "finderrorscore_list",
                                        "type": "LITERAL",
                                        "includeEmpty": true,
                                        "options": [{
                                            "value": "1"
                                        }, {
                                            "value": "2"
                                        }, {
                                            "value": "3"
                                        }, {
                                            "value": "4"
                                        }, {
                                            "value": "5"
                                        }]
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
                                            type: "REFERENCECONTAINER",
                                            reference: "studyerrors_ref",
                                            fixedOrder: true,
                                            subfields: [
                                                "studyerrorsstudyid",
                                                "studyerrorsstudytitle",
                                                "studyerrorsscore"
                                            ]
                                        },
                                        studyresults: {
                                            type: "REFERENCECONTAINER",
                                            reference: "studyresults_ref",
                                            fixedOrder: true,
                                            subfields: [
                                                "studyresultsid",
                                                "studyresultstitle",
                                                "studyresultsseries",
                                                "studyresultsdatakind",
                                                "studyresultstermsofuse"
                                            ]
                                        },
                                        studyresultsid: {
                                            key: "studyresultsid",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyresultsid_ref"
                                        },
                                        studyresultsseries: {
                                            key: "studyresultsseries",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyresultsseries_ref"
                                        },
                                        studyresultsdatakind: {
                                            key: "studyresultsdatakind",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyresultsdatakind_ref"
                                        },
                                        studyresultstermsofuse: {
                                            key: "studyresultstermsofuse",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyresultstermsofuse_ref"
                                        },
                                        studyerrorsstudyid: {
                                            key: "studyerrorsstudyid",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorsstudyid_ref"
                                        },
                                        studyresultstitle: {
                                            key: "studyresultstitle",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyresultstitle_ref"
                                        },
                                        studyerrorsstudytitle: {
                                            key: "studyerrorsstudytitle",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorsstudytitle_ref"
                                        },
                                        studyerrorsscore: {
                                            key: "studyerrorsscore",
                                            subfield: true,
                                            type: "REFERENCE",
                                            reference: "studyerrorsscore_ref"
                                        },
                                        findstudyid: {
                                            type: "STRING"
                                        },
                                        finderrorscore: {
                                            type: "SELECTION",
                                            selectionList: "finderrorscore_list"
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