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

    var cells = {
        approved: {
            "type": "CELL",
            "title": "Hyväksyttyjä",
            "field": {
                "displayType": "BOOLEAN",
                "key": "searchApproved"
            }
        },
        draft: {
            "type": "CELL",
            "title": "Luonnoksia",
            "field": {
                "displayType": "BOOLEAN",
                "key": "searchDraft"
            }
        },
        removed: {
            "type": "CELL",
            "title": "Poistettuja",
            "field": {
                "displayType": "BOOLEAN",
                "key": "searchRemoved"
            }
        }
    };
    return {
        column: {
            "type": "COLUMN",
            "columns": 6,
            "rows": [
                {
                    "type": "ROW",
                    "cells": [{
                        "type": "EMPTYCELL"
                    }, cells.approved, cells.draft, cells.removed]
                }
            ]
        },
        cells: cells,
        requestData: function (options, requestData) {
            var data = require('./data')(options);
            return $.extend(requestData, {
                searchApproved: data('searchApproved').getByLang(options.defaultLang),
                searchDraft: data('searchDraft').getByLang(options.defaultLang),
                searchRemoved: data('searchRemoved').getByLang(options.defaultLang)
            });
        },
        initialData: function (data) {
            return $.extend(true, data, {
                key: {
                    id: "",
                    no: ""
                },/*
                state: {
                    uiState: "DRAFT",
                    handler: MetkaJS.User.userName
                },*/
                fields: {
                    searchApproved: {
                        type: 'VALUE',
                            values: {
                            DEFAULT: {
                                current: true
                            }
                        }
                    },
                    searchDraft: {
                        type: 'VALUE',
                            values: {
                            DEFAULT: {
                                current: true
                            }
                        }
                    }
                }
            });
        }
    };
});