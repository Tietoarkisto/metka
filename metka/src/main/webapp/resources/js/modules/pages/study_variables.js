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
    var variablesBooleans = require('./../commonSearchBooleans')('variables');
    var variableBooleans = require('./../commonSearchBooleans')('variable');

    var variablesSearchOptions = [{
            key: 'key.configuration.type',
            value: "STUDY_VARIABLES",
            addParens: false
        }, {
            key: 'findvariablesstudyid',
            rename: 'study',
            exactValue: true
        }, {
            key: 'findvariableslanguage',
            rename: 'language.value',
            exactValue: true,
            useSelectionText: false
        }];
    var variableSearchOptions = [{
            key: 'key.configuration.type',
            value: "STUDY_VARIABLE",
            addParens: false
        }, {
            key: 'findvariablestudyid',
            rename: 'study',
            exactValue: true
        }, {
            key: 'findvariablevarlabel',
            rename: 'varlabel',
            exactValue: true,
            addWildcard: true
        }, {
            key: 'findvariableqstnlit',
            rename: 'qstnlits.qstnlit',
            exactValue: false
        }, {
            key: 'findvariablevaluelabel',
            rename: 'valuelabels.label',
            exactValue: true,
            addWildcard: true
        }, {
            key: 'findvariablelanguage',
            rename: 'language.value',
            exactValue: true,
            useSelectionText: false
        }];

    if (location.pathname.split('/').indexOf('search') === -1) {
        return function (options, onLoad) {
            require('./../pages/defaults')(options, onLoad);
        };
    } else {
        return function (options, onLoad) {
            $.extend(options, {
                header: MetkaJS.L10N.get('type.STUDY_VARIABLES.search'),
                fieldTitles: {
                    variablesstudyid: {
                        "title": "Aineistonumero"
                    },
                    variablesstudytitle: {
                        "title": "Aineisto"
                    },
                    variableslanguage: {
                        "title": "Kieli"
                    },
                    variablestudyid: {
                        "title": "Aineistonumero"
                    },
                    variablestudytitle: {
                        "title": "Aineisto"
                    },
                    variablelanguage: {
                        "title": "Kieli"
                    },
                    variablevarlabel: {
                        "title": "Muuttuja"
                    }
                },
                dataConf: {
                    selectionLists: {
                        language_list: {
                            key: "language_list",
                            type: "REFERENCE",
                            reference: "language_list_ref",
                            includeEmpty: true
                        }
                    },
                    references: {
                        // Variales list references
                        variables_ref: {
                            key: "variables_ref",
                            type: "REVISION",
                            target: "STUDY_VARIABLES"
                        },
                        variables_language_ref: {
                            key: "variables_language_ref",
                            type: "DEPENDENCY",
                            target: "variableslist",
                            valuePath: "language"
                        },
                        variables_studyid_ref: {
                            key: "variables_studyid_ref",
                            type: "DEPENDENCY",
                            target: "variableslist",
                            valuePath: "study"
                        },
                        variables_title_ref: {
                            key: "variables_title_ref",
                            type: "DEPENDENCY",
                            target: "variableslist",
                            valuePath: "studytitle"
                        },
                        // Variable list references
                        variable_ref: {
                            key: "variable_ref",
                            type: "REVISION",
                            target: "STUDY_VARIABLE"
                        },
                        variable_language_ref: {
                            key: "variable_language_ref",
                            type: "DEPENDENCY",
                            target: "variablelist",
                            valuePath: "language"
                        },
                        variable_varlabel_ref: {
                            key: "variable_varlabel_ref",
                            type: "DEPENDENCY",
                            target: "variablelist",
                            valuePath: "varlabel"
                        },
                        variable_studyid_ref: {
                            key: "variable_studyid_ref",
                            type: "DEPENDENCY",
                            target: "variablelist",
                            valuePath: "study"
                        },
                        variable_title_ref: {
                            key: "variable_title_ref",
                            type: "DEPENDENCY",
                            target: "variablelist",
                            valuePath: "studytitle"
                        },

                        // Other references
                        language_list_ref: {
                            key: "language_list_ref",
                            type: "JSON",
                            target: "language_descriptions",
                            valuePath: "language",
                            titlePath: "text"
                        }
                    },
                    fields: {
                        // Variables list fields
                        variableslist: {
                            key: "variableslist",
                            type: "REFERENCECONTAINER",
                            reference: "variables_ref",
                            subfields: [
                                "variablesstudyid",
                                "variablesstudytitle",
                                "variableslanguage"
                            ]
                        },
                        variablesstudyid: {
                            key: "variablesstudyid",
                            type: "REFERENCE",
                            reference: "variables_studyid_ref",
                            subfield: true
                        },
                        variablesstudytitle: {
                            key: "variablesstudytitle",
                            type: "REFERENCE",
                            reference: "variables_title_ref",
                            subfield: true
                        },
                        variableslanguage: {
                            key: "variableslanguage",
                            type: "REFERENCE",
                            reference: "variables_language_ref",
                            subfield: true
                        },

                        // Find variables fields
                        findvariablesstudyid: {
                            key: "findvariablesstudyid",
                            type: "STRING"
                        },
                        findvariableslanguage: {
                            key: "findvariableslanguage",
                            type: "SELECTION",
                            selectionList: "language_list"
                        },

                        // Variable list fields
                        variablelist: {
                            key: "variablelist",
                            type: "REFERENCECONTAINER",
                            reference: "variable_ref",
                            subfields: [
                                "variablestudyid",
                                "variablestudytitle",
                                "variablevarlabel",
                                "variablelanguage"
                            ]
                        },
                        variablestudyid: {
                            key: "variablestudyid",
                            type: "REFERENCE",
                            reference: "variable_studyid_ref",
                            subfield: true
                        },
                        variablestudytitle: {
                            key: "variablestudytitle",
                            type: "REFERENCE",
                            reference: "variable_title_ref",
                            subfield: true
                        },
                        variablevarlabel: {
                            key: "variablevarlabel",
                            type: "REFERENCE",
                            reference: "variable_varlabel_ref",
                            subfield: true
                        },
                        variablelanguage: {
                            key: "variablelanguage",
                            type: "REFERENCE",
                            reference: "variable_language_ref",
                            subfield: true
                        },

                        // Find variable fields
                        findvariablestudyid: {
                            key: "findvariablestudyid",
                            type: "STRING"
                        },
                        findvariablevarlabel: {
                            key: "findvariablevarlabel",
                            type: "STRING"
                        },
                        findvariableqstnlit: {
                            key: "findvariableqstnlit",
                            type: "STRING"
                        },
                        findvariablevaluelabel: {
                            key: "findvariablevaluelabel",
                            type: "STRING"
                        },
                        findvariablelanguage: {
                            key: "findvariablelanguage",
                            type: "SELECTION",
                            selectionList: "language_list"
                        }
                    }
                },
                content: [{
                    type: "TAB",
                    title: "Muuttujajoukot",
                    content: [
                        variablesBooleans.column,
                    {
                        "type": "COLUMN",
                        "columns": 2,
                        "rows": [{
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Aineistonumero",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariablesstudyid"
                                }
                            }]
                        }, {
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Muuttujajoukon kieli",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariableslanguage"
                                }
                            }, {
                                "type": "CELL",
                                "contentType": "BUTTON",
                                "button": {
                                    "title": MetkaJS.L10N.get('general.buttons.search'),
                                    "create": function() {
                                        this.click(function() {
                                            require('./../searchRequestSearch')(options, variablesSearchOptions, 'variableslist', 'variables').search();
                                        });
                                    }
                                }
                            }]
                        }]
                    }, {
                        type: "COLUMN",
                        columns: 1,
                        rows: [{
                            type: "ROW",
                            cells: [{
                                type: "CELL",
                                title: "Muuttujajoukot",
                                readOnly: true,
                                field: {
                                    showRowAmount: true,
                                    allowDownload: true,
                                    key: "variableslist",
                                    showReferenceState: true,
                                    columnFields: [
                                        "variablesstudyid",
                                        "variablesstudytitle",
                                        "variableslanguage"
                                    ],
                                    onClick: function (transferRow) {
                                        require('./../assignUrl')('view', {
                                            id: transferRow.value.split('-')[0],
                                            no: transferRow.value.split('-')[1]}
                                        );
                                    }
                                }
                            }]
                        }]
                    }]
                }, {
                    type: "TAB",
                    title: "Muuttujat",
                    content: [
                        variableBooleans.column,
                    {
                        "type": "COLUMN",
                        "columns": 2,
                        "rows": [{
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Aineistonumero",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariablestudyid"
                                }
                            }]
                        }, {
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Muuttujan selite",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariablevarlabel"
                                }
                            }]
                        }, {
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Kysymysteksti",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariableqstnlit"
                                }
                            }]
                        }, {
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Arvon selite",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariablevaluelabel"
                                }
                            }]
                        }, {
                            "type": "ROW",
                            "cells": [{
                                "type": "CELL",
                                "title": "Muuttujan kieli",
                                "horizontal": true,
                                "field": {
                                    "key": "findvariablelanguage"
                                }
                            }, {
                                "type": "CELL",
                                "contentType": "BUTTON",
                                "button": {
                                    "title": MetkaJS.L10N.get('general.buttons.search'),
                                    "create": function() {
                                        this.click(function() {
                                            require('./../searchRequestSearch')(options, variableSearchOptions, 'variablelist', 'variable').search();
                                        });
                                    }
                                }
                            }]
                        }]
                    }, {
                        type: "COLUMN",
                        columns: 1,
                        rows: [{
                            type: "ROW",
                            cells: [{
                                type: "CELL",
                                title: "Muuttujat",
                                readOnly: true,
                                field: {
                                    showRowAmount: true,
                                    allowDownload: true,
                                    key: "variablelist",
                                    showReferenceState: true,
                                    columnFields: [
                                        "variablestudyid",
                                        "variablestudytitle",
                                        "variablevarlabel",
                                        "variablelanguage"
                                    ],
                                    onClick: function (transferRow) {
                                        require('./../revisionModal')(options, {
                                            id: transferRow.value.split('-')[0],
                                            no: transferRow.value.split('-')[1]}, 'STUDY_VARIABLE', null, null, true);
                                    }
                                }
                            }]
                        }]
                    }]
                }],
                buttons: [],
                data: $.extend(true, variablesBooleans.initialData({}), variableBooleans.initialData({}))
            });
            onLoad();
        }
    }
});
