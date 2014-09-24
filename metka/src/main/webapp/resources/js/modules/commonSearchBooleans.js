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
            return $.extend(data, {
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