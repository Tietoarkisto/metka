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
                        }/*,
                        result_ref: {
                            key: "result_ref",
                            type: "REVISION",
                            target: "SERIES"
                        },
                        result_abbr_ref: {
                            key: "result_abbr_ref",
                            type: "DEPENDENCY",
                            target: "searchresults",
                            valuePath: "seriesabbr"
                        },
                        result_name_ref: {
                            key: "result_name_ref",
                            type: "DEPENDENCY",
                            target: "searchresults",
                            valuePath: "seriesname"
                        }*/
                    },
                    fields: {
                        seriesabbr: {
                            key: 'seriesabbr',
                            type: 'SELECTION',
                            selectionList: 'seriesabbr_list'
                        }/*,
                        searchresults: {
                            key: 'searchresults',
                            type: "REFERENCECONTAINER",
                            reference: "result_ref",
                            fixedOrder: true,
                            subfields: [
                                "resultabbr",
                                "resultname"
                            ]
                        },
                        resultabbr: {
                            key: "resultabbr",
                            type: "REFERENCE",
                            reference: "result_abbr_ref",
                            subfield: true
                        },
                        resultname: {
                            key: "resultname",
                            type: "REFERENCE",
                            reference: "result_name_ref",
                            subfield: true
                        }*/
                    }
                },
                fieldTitles: {
                    "id": {
                        "title" : "ID"
                    },
                    "seriesabbr": {
                        "title" : "Lyhenne"
                    },
                    "seriesname": {
                        "title" : "Nimi"
                    },
                    "state": {
                        "title" : "Tila"
                    }
                },
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
                                        "title": "Lyhenne",
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
                                        "title": "Nimi",
                                        "colspan": 2,
                                        "field": {
                                            "displayType": "STRING",
                                            "key": "seriesname"
                                        }
                                    },
                                    commonSearchBooleans.cells.removed
                                ]
                            }/*,
                            {
                                "type": "ROW",
                                "cells": [
                                    {
                                        "type": "CELL",
                                        "title": "Sarjat",
                                        "colspan": 3,
                                        "readOnly": true,
                                        "field": {
                                            "displayType": "REFERENCECONTAINER",
                                            "key": "searchresults",
                                            "disableRemoval": true,
                                            //"displayType": "CONTAINER",
                                            "showReferenceValue": true,
                                            "showReferenceState": true,
                                            "columnFields": [
                                                "resultabbr",
                                                "resultname"
                                            ]
                                        }
                                    }
                                ]
                            }*/
                        ]
                    }
                ],
                buttons: [ /*{
                    title: "Hae",
                    create: function(options) {
                        this.click(function() {
                            require('./../searchRequestSearch')(options, [
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
                                'seriesname'
                            ], 'searchresults').search();
                        });
                    }
                },*/
                    require('./../searchButton')('searchAjax', [
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
                        'seriesname'
                    ], function (data) {
                        return data.rows;
                    }, function (result) {
                        return {
                            id: result.id,
                            no: result.no,
                            TYPE: result.type,
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
                    ], options),
                    {
                        "&title": {
                            "default": "Lisää uusi"
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