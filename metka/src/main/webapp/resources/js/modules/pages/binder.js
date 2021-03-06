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

    var resultParser = require('./../resultParser');
    var commonSearchBooleans = require('./../commonSearchBooleans')();

    return function (options, onLoad) {
        var pagesSearch = require('./../searchRequestSearch')(options, [{
                key: 'key.configuration.type',
                value: "BINDER_PAGE",
                addParens: false
            }, {
                key: 'findBinderId',
                rename: 'binderid',
                exactValue: false
            }, {
                key: 'findStudyId',
                rename: 'studyid',
                exactValue: true
            }, {
                key: 'findStudyTitle',
                rename: 'studyid.value',
                addParens: false,
                exactValue:true,
                subQuery: 'ID{+key.configuration.type:STUDY +title:{input}}ID'
            }, {
                key: 'findBinderDescription',
                rename: 'description',
                exactValue: true,
                addWildcard: true,
                addParens: true

            }], 'pages', 'DEFAULT');

        function view(requestOptions) {
            require('./../revisionModal')(options, requestOptions, 'BINDER_PAGE', pagesSearch.search, 'pages');
        }

        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            fieldTitles: {
                "studyId": {
                    "title": MetkaJS.L10N.get("search.coltitle.studyid")
                },
                "studyTitle": {
                    "title": MetkaJS.L10N.get("search.coltitle.studyname")
                },
                "binderId": {
                    "title": MetkaJS.L10N.get("search.coltitle.binderid")
                },
                "description": {
                    "title": MetkaJS.L10N.get("search.coltitle.binderdescription")
                }
            },
            dialogTitles: {
                "pages": {
                    "key": "pages",
                    "ADD": "Mapitus",
                    "MODIFY": "Muokkaa mapitusta",
                    "VIEW": "Mapitus"
                }
            },
            dataConf: {
                references: {
                    binder_page_ref: {
                        key: "binder_page_ref",
                        type: "REVISION",
                        target: "BINDER_PAGE"
                    },
                    study_id_ref: {
                        key: "study_id_ref",
                        type: "DEPENDENCY",
                        target: "pages",
                        valuePath: "studyid"
                    },
                    study_title_ref: {
                        key: "study_title_ref",
                        type: "DEPENDENCY",
                        target: "pages",
                        valuePath: "studytitle"
                    },
                    binder_id_ref: {
                        key: "study_id_ref",
                        type: "DEPENDENCY",
                        target: "pages",
                        valuePath: "binderid"
                    },
                    description_ref: {
                        key: "description_ref",
                        type: "DEPENDENCY",
                        target: "pages",
                        valuePath: "description"
                    }
                },
                fields: {
                    findBinderId: {
                        key: "findBinderId",
                        type: "INTEGER"
                    },
                    findStudyId: {
                        key: "findStudyId",
                        type: "STRING"
                    },
                    findStudyTitle: {
                        key: "findStudyTitle",
                        type: "STRING"
                    },
                    findBinderDescription: {
                        key: "findBinderDescription",
                        type: "STRING"
                    },
                    pages: {
                        key: "pages",
                        type: "REFERENCECONTAINER",
                        reference: "binder_page_ref",
                        fixedOrder: true,
                        subfields: [
                            "studyId",
                            "studyTitle",
                            "binderId",
                            "description"
                        ]
                    },
                    studyId: {
                        key: "studyId",
                        type: 'REFERENCE',
                        subfield: true,
                        reference: "study_id_ref"
                    },
                    studyTitle: {
                        key: "studyTitle",
                        type: 'REFERENCE',
                        subfield: true,
                        reference: "study_title_ref"
                    },
                    binderId: {
                        key: "binderId",
                        type: 'REFERENCE',
                        subfield: true,
                        reference: "binder_id_ref"
                    },
                    description: {
                        key: "description",
                        type: 'REFERENCE',
                        subfield: true,
                        reference: "description_ref"
                    }
                }
            },
            content: [
                commonSearchBooleans.column,
            {
                "type": "COLUMN",
                "columns": 2,
                "rows": [{
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": MetkaJS.L10N.get("search.binder.studyid"),
                        "horizontal": true,
                        "field": {
                            "key": "findStudyId"
                        }
                    }]
                }, {
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": MetkaJS.L10N.get("search.binder.binderid"),
                        "horizontal": true,
                        "field": {
                            "key": "findBinderId"
                        }
                    }]
                }, {
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": MetkaJS.L10N.get("search.binder.studytitle"),
                        "horizontal": true,
                        "field": {
                            "key": "findStudyTitle"
                        }
                    }]
                }, {
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": MetkaJS.L10N.get("search.binder.binderdescription"),
                        "horizontal": true,
                        "field": {
                            "key": "findBinderDescription"
                        }
                    }, {
                        "type": "CELL",
                        "contentType": "BUTTON",
                        "button": {
                            "title": MetkaJS.L10N.get('general.buttons.search'),
                            "create": function() {
                                this.click(pagesSearch.search);
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
                        "title": MetkaJS.L10N.get("search.binder.title"),
                        "colspan": 1,
                        "readOnly": true,
                        "field": {
                            "allowDownload": true,
                            "showRowAmount": true,
                            "key": "pages",
                            "disableRemoval": true,
                            "showReferenceState": true,
                            "columnFields": [
                                "binderId",
                                "studyId",
                                "studyTitle",
                                "description"
                            ],
                            onClick: function(transferRow) {
                                view({
                                    id: transferRow.value.split('-')[0],
                                    no: transferRow.value.split('-')[1]});
                            }
                        }
                    }]
                }]
            }],
            data: commonSearchBooleans.initialData({}),
            buttons: [{
                "&title": {
                    "default": MetkaJS.L10N.get("general.buttons.addBinder")
                },
                permissions: [
                    'canEditBinderPages'
                ],
                create: function () {
                    this.click(function () {
                        require('./../server')('create', {
                            data: JSON.stringify({
                                type: 'BINDER_PAGE'
                            }),
                            success: function(response) {
                                if(resultParser(response.result).getResult() === 'REVISION_CREATED') {
                                    view({
                                        type: 'BINDER_PAGE',
                                        id: response.data.key.id,
                                        no: response.data.key.no});
                                } else {
                                    require('./../resultViewer')(response.result);
                                }
                            }
                        });
                    });
                }
            }]
        });

        pagesSearch.search();

        onLoad();
    };
});