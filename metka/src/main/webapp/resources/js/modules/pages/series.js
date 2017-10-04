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

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans')();
        return function (options, onLoad) {

            var seriesSearch = require('./../searchRequestSearch')(options, [
                {
                    key: 'key.configuration.type',
                    value: "SERIES",
                    addParens: false
                },
                'key.id',
                {
                    key: 'seriesabbr',
                    exactValue: true
                },
                {
                    key: 'seriesname',
                    exactValue: true,
                    addWildcard: true
                }
            ], 'seriesresults', 'DEFAULT');

            $.extend(options, {
                header: MetkaJS.L10N.get('type.SERIES.search'),
                dataConf: {
                    key: {
                        version: 1,
                        type: 'SERIES'
                    },
                    selectionLists: {
                        seriesabbr_list: {
                            includeEmpty: true,
                            key: 'seriesabbr_list',
                            type: 'REFERENCE',
                            reference: 'seriesabbr_ref'
                        }
                    },
                    references: {
                        seriesabbr_ref: {
                            key: 'seriesabbr_ref',
                            type: 'REVISIONABLE',
                            target: 'SERIES',
                            valuePath: 'seriesabbr',
                            titlePath: 'seriesabbr'
                        },
                        seriesresults_ref: {
                            key: "seriesresults_ref",
                            type: "REVISION",
                            target: "SERIES"
                        },
                        seriesresults_abbr_ref: {
                            key: "seriesresults_abbr_ref",
                            type: "DEPENDENCY",
                            target: "seriesresults",
                            valuePath: "seriesabbr"
                        },
                        seriesresults_name_ref: {
                            key: "result_name_ref",
                            type: "DEPENDENCY",
                            target: "seriesresults",
                            valuePath: "seriesname"
                        }
                    },
                    fields: {
                        seriesabbr: {
                            key: 'seriesabbr',
                            type: 'SELECTION',
                            selectionList: 'seriesabbr_list'
                        },
                        seriesresults: {
                            key: 'seriesresults',
                            type: "REFERENCECONTAINER",
                            reference: "seriesresults_ref",
                            fixedOrder: true,
                            subfields: [
                                "seriesresultsabbr",
                                "seriesresultsname"
                            ]
                        },
                        seriesresultsabbr: {
                            key: "seriesresultsabbr",
                            type: "REFERENCE",
                            reference: "seriesresults_abbr_ref",
                            subfield: true
                        },
                        seriesresultsname: {
                            key: "seriesresultsname",
                            type: "REFERENCE",
                            reference: "seriesresults_name_ref",
                            subfield: true
                        }
                    }
                },
                fieldTitles: {
                    "seriesresultsabbr": {
                        "title": MetkaJS.L10N.get("search.coltitle.seriesresultsabbr")
                    },
                    "seriesresultsname": {
                        "title": MetkaJS.L10N.get("search.coltitle.seriesresultsname")
                    },
                    "state": {
                        "title": MetkaJS.L10N.get("search.coltitle.seriesresultsstate")
                    }
                },
                content: [
                    {
                        "type": "COLUMN",
                        "rows": [{
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "contentType": "BUTTON",
                                "button": {
                                    "&title": {
                                        "default": MetkaJS.L10N.get("general.buttons.addSeries")
                                    },
                                    "permissions": [
                                        "canCreateRevision"
                                    ],
                                    create: function () {
                                        this
                                            .click(function () {
                                                require('./../server')('create', {
                                                    data: JSON.stringify({
                                                        type: 'SERIES'
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
                    {
                        "type": "COLUMN",
                        "columns": 3,
                        "rows": [
                            {
                                "type": "ROW",
                                "cells": [
                                    {
                                        "type": "CELL",
                                        "title": MetkaJS.L10N.get('search.series.seriesid'),
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
                                        "title": MetkaJS.L10N.get('search.series.seriesabbr'),
                                        "colspan": 2,
                                        "field": {
                                            "key": "seriesabbr"
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
                                        "title": MetkaJS.L10N.get('search.series.seriesname'),
                                        "colspan": 2,
                                        "field": {
                                            "displayType": "STRING",
                                            "key": "seriesname"
                                        }
                                    },
                                    commonSearchBooleans.cells.removed
                                ]
                            },
                            {
                                "type": "ROW",
                                "cells": [
                                    {
                                        "type": "CELL",
                                        "contentType": "BUTTON",
                                        "button": {
                                            "title": MetkaJS.L10N.get('general.buttons.search'),
                                            "create": function () {
                                                this.click(seriesSearch.search);
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
                                "title": MetkaJS.L10N.get("search.result.seriessearch"),
                                "colspan": 1,
                                "readOnly": true,
                                "field": {
                                    "key": "seriesresults",
                                    "showRowAmount": true,
                                    "allowDownload": true,
                                    "disableRemoval": true,
                                    //"showReferenceValue": true,
                                    "showReferenceState": true,
                                    "columnFields": [
                                        "seriesresultsabbr",
                                        "seriesresultsname"
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