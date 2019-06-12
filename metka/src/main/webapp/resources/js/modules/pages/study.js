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
                    rename: 'study',
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
                            key: 'handler',
                            addParens: true,
                            exactValue: true,
                            addWildcard: true,
                            rename: 'state.draft.handler'

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
                                    "title": MetkaJS.L10N.get("search.coltitle.studyid")
                                },
                                "studyname": {
                                    "title": MetkaJS.L10N.get("search.coltitle.studyname")
                                },
                                "authors": {
                                    "title": MetkaJS.L10N.get("search.coltitle.authors")
                                },
                                "series": {
                                    "title": MetkaJS.L10N.get("search.coltitle.series")
                                },
                                "datakind": {
                                    "title": MetkaJS.L10N.get("search.coltitle.datakind")
                                },
                                "termsofuse": {
                                    "title": MetkaJS.L10N.get("search.coltitle.datakind"),
                                    exactValue: true
                                },
                                "state": {
                                    "title": MetkaJS.L10N.get("search.coltitle.state")
                                },
                                studyerrorsstudyid: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyid")
                                },
                                studyresultsid: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyid")
                                },
                                studyresultsdatakind: {
                                    title: MetkaJS.L10N.get("search.coltitle.studytype")
                                },
                                studyresultstermsofuse: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyright")
                                },
                                studyresultsseries: {
                                    title: MetkaJS.L10N.get("type.SERIES.title")
                                },
                                studyresultstitle: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyname")
                                },
                                studyerrorsstudytitle: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyname")
                                },
                                studyerrorsscore: {
                                    title: MetkaJS.L10N.get("search.coltitle.studyerrorsscore")
                                }
                            },
                            content: [
                                {
                                    "type": "TAB",
                                    "title": MetkaJS.L10N.get("search.study.studysearch"),
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
                                                            "default": MetkaJS.L10N.get("general.buttons.addStudy")
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
                                                    "title": MetkaJS.L10N.get("search.study.studyid"),
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
                                                    "title": MetkaJS.L10N.get("search.study.submissionid"),
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
                                                    "title": MetkaJS.L10N.get("search.study.title"),
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
                                                    "title": MetkaJS.L10N.get("search.study.author"),
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
                                                    "title": MetkaJS.L10N.get("search.study.authororganization"),
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
                                                    "title": MetkaJS.L10N.get("search.study.producername"),
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
                                                    "title": MetkaJS.L10N.get("search.study.producerrole"),
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
                                                    "title": MetkaJS.L10N.get("search.study.series"),
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
                                                    "title": MetkaJS.L10N.get("search.study.datakind"),
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
                                                    "title": MetkaJS.L10N.get("search.study.anonymization"),
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
                                                    "title": MetkaJS.L10N.get("search.study.securityissues"),
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
                                                    "title": MetkaJS.L10N.get("search.study.publication"),
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
                                                    "title": MetkaJS.L10N.get("search.study.aipcomplete"),
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
                                                    "title": MetkaJS.L10N.get("search.study.termsofuse"),
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
                                                    "title": MetkaJS.L10N.get("search.study.newtermsofuse"),
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
                                                    "title": MetkaJS.L10N.get("search.study.termsofusechangedate"),
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
                                                    "title": MetkaJS.L10N.get("search.study.agreementtype"),
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
                                                    "title": MetkaJS.L10N.get("search.study.depositortype"),
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
                                                    "title": MetkaJS.L10N.get("search.study.handler"),
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
                                                    "title": MetkaJS.L10N.get("search.study.packageurn"),
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
                                                    "title": MetkaJS.L10N.get("search.study.abstract"),
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
                                                    "title": MetkaJS.L10N.get("search.study.topictop"),
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
                                                    "title": MetkaJS.L10N.get("search.study.topic"),
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
                                                    "title": MetkaJS.L10N.get("search.study.timeperiod"),
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
                                                    "title": MetkaJS.L10N.get("search.study.colltime"),
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
                                                    "title": MetkaJS.L10N.get("search.study.country"),
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
                                                    "title": MetkaJS.L10N.get("search.study.collector"),
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
                                                    "title": MetkaJS.L10N.get("search.study.analysisunit"),
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
                                                    "title": MetkaJS.L10N.get("search.study.timemethod"),
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
                                                    "title": MetkaJS.L10N.get("search.study.sampproc"),
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
                                                    "title": MetkaJS.L10N.get("search.study.collmode"),
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
                                                    "title": MetkaJS.L10N.get('search.result.studysearch'),
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
                                                            require('./../assignUrlNewTab')('view', {
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
                                    "title": MetkaJS.L10N.get("search.study.errorsearch"),
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
                                                    "title": MetkaJS.L10N.get("search.study.studyid"),
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "findstudyid"
                                                    }
                                                }]
                                            }, {
                                                "type": "ROW",
                                                "cells": [{
                                                    "type": "CELL",
                                                    "title": MetkaJS.L10N.get("search.study.errorscore"),
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
                                                    "title": MetkaJS.L10N.get("search.study.errors"),
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