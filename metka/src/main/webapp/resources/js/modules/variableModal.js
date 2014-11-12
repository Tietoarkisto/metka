define(function (require) {
    'use strict';

    return function (key, requestOptions, onSaveSuccess) {
        require('./server')('viewAjax', $.extend({
            PAGE: 'STUDY_VARIABLE'
        }, requestOptions), {
            method: 'GET',
            success: function (data) {
                var modalOptions = $.extend(data.gui, {
                    title: 'Muokkaa muuttujaa',
                    data: data.transferData,
                    dataConf: data.configuration,
                    $events: $({}),
                    defaultLang: 'DEFAULT',
                    translatableCurrentLang: MetkaJS.User.role.defaultLanguage.toUpperCase(),
                    large: true,
                    "fieldTitles": {
                        "ivuinstr": {
                            "key" : "ivuinstr",
                            "title" : "Haastattelijan ohje"
                        },
                        "postqtxt": {
                            "key" : "postqtxt",
                            "title" : "Jälkiteksti"
                        },
                        "preqtxt": {
                            "key" : "preqtxt",
                            "title" : "Esiteksti"
                        },
                        "qstnlit": {
                            "key" : "qstnlit",
                            "title" : "Kysymysteksti"
                        },
                        "varnote": {
                            "key" : "varnote",
                            "title" : "Huomiot"
                        },
                        "varsecurity": {
                            "key" : "varsecurity",
                            "title" : "Tietosuoja-asiat"
                        },
                        "vartext": {
                            "key" : "vartext",
                            "title" : "Lisätiedot"
                        }
                    },
                    content: [
                        {
                            "type": "COLUMN",
                            "columns": 1,
                            "rows": [
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Nimi",
                                            "horizontal": true,
                                            "field": {
                                                "key": "varname"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Selite",
                                            "horizontal": true,
                                            "field": {
                                                "key": "varlabel"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Kysymystekstit",
                                            "field": {
                                                "key": "qstnlits",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "qstnlit"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Esitekstit",
                                            "field": {
                                                "key": "preqtxts",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "preqtxt"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Jälkitekstit",
                                            "field": {
                                                "key": "postqtxts",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "postqtxt"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Haastattelijan ohjeet",
                                            "field": {
                                                "key": "ivuinstrs",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "ivuinstr"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Huomiot",
                                            "field": {
                                                "key": "varnotes",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "varnote"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Lisätiedot",
                                            "field": {
                                                "key": "vartexts",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "vartext"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Tietosuoja-asiat",
                                            "field": {
                                                "key": "varsecurities",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "varsecurity"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Arvojen selitteet",
                                            "readOnly": true,
                                            "field": {
                                                "displayHeader": false,
                                                "displayType": "CONTAINER",
                                                "key": "categories",
                                                "columnFields": [
                                                    "value",
                                                    "label",
                                                    "stat",
                                                    "missing"
                                                ]
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Statistiikka",
                                            "readOnly": true,
                                            "field": {
                                                "key": "statistics",
                                                "displayHeader": false,
                                                "columnFields": [
                                                    "statisticstype",
                                                    "statisticsvalue"
                                                ]
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    buttons: [
                        {
                            "&title": {
                                "default": "Tallenna"
                            },
                            "type": "CUSTOM",
                            "customHandler": "studyVariableSave",
                            "isHandler": true,
                            "states": [
                                "DRAFT"
                            ],
                            "permissions": [
                                "canEditRevision"
                            ]
                        },
                        {
                            "&title": {
                                "default": "Tee luonnos"
                            },
                            "type": "CUSTOM",
                            "customHandler": "studyVariableEdit",
                            "states": [
                                "APPROVED"
                            ],
                                "permissions": [
                                "canEditRevision"
                            ]
                        },
                        {
                            "title": "Palauta",
                            "type": "CUSTOM",
                            "customHandler": "studyVariableRestore",
                            "states": [
                                "REMOVED"
                            ],
                            "permissions": [
                                "canRestoreRevision"
                            ]
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                });
                require('./isRelatedStudyDraftForCurrentUser')(modalOptions, function (isDraft) {
                    modalOptions.isRelatedStudyDraftForCurrentUser = isDraft;
                    var $modal = require('./modal')(modalOptions);
                });
            }
        });
    };
});