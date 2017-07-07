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

            var publicationSearch = require('./../searchRequestSearch')(options, [
                {
                    key: 'key.configuration.type',
                    value: "PUBLICATION",
                    addParens: false
                },
                'publicationid',
                {
                    key: 'studies',
                    rename: 'studies.studyid'
                },
                {
                    key: 'publicationfirstsaved',
                    exactValue: true
                },
                {
                    key: 'savedAt',
                    rename: 'state.saved.time',
                    exactValue: true,
                    addWildcard: true
                },
                'publicationyear',
                {
                    key: 'studyname',
                    rename: 'studies.studytitle',
                    exactValue: true,
                    addParens: true,
                    addWildcard: true
                },
                {
                    key: 'seriesname',
                    rename: 'series.seriesname',
                    exactValue: true,
                    addParens: true,
                    addWildcard: true
                },
                {
                    key: 'lastname',
                    rename: 'publicationauthors.lastname'
                },
                {
                    key: 'firstname',
                    rename: 'publicationauthors.firstname'
                },
                {
                    key: 'publicationtitle',
                    exactValue: true,
                    addParens: true,
                    addWildcard: true
                },{
                    key: 'publicationrelpubl',
                    exactValue: true,
                    addParens: true,
                    addWildcard: true
                },
                {
                    key: 'publicationlanguage',
                    exactValue: true
                },
                {
                    key: 'publicationpublic',
                    exactValue: true
                },
                {
                    key: 'savedBy',
                    rename: 'state.saved.user'
                }
            ], 'publicationresults', 'DEFAULT');


            require('./../server')('conf', {
                method: 'GET',
                success: function (response) {
                    if (resultParser(response.result).getResult() === 'CONFIGURATION_FOUND') {
                        $.extend(options, {
                            header: MetkaJS.L10N.get('type.PUBLICATION.search'),
                            fieldTitles: {
                                "publicationresultspublicationid": {
                                    "title": "Numero"
                                },
                                "publicationresultspublicationtitle": {
                                    "title": "Otsikko"
                                },
                                "state": {
                                    "title": "Tila"
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
                                                                    type: 'PUBLICATION'
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
                                                    "title": "Julkaisu id-nro",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "publicationid"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "/ Aineiston numerot",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "studies"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun lisäyspvm",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "publicationfirstsaved"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Viimeisin muutospvm",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "savedAt"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisuvuosi",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "publicationyear"
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
                                                        "key": "studyname"
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
                                                        "key": "seriesname"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Tekijän sukunimi",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "lastname"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Etunimi",
                                                    "horizontal": true,
                                                    "field": {
                                                        "key": "firstname"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun otsikko",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "publicationtitle"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "relPubl",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "publicationrelpubl"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun kieli",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "publicationlanguage"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Voiko julkaista",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "publicationpublic"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Käsitteljä",
                                                    "horizontal": true,
                                                    "colspan": 2,
                                                    "field": {
                                                        "key": "savedBy"
                                                    }
                                                }
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
                                                            this.click(publicationSearch.search);
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
                                            "title": "Julkaisuhaun tulokset",
                                            "colspan": 1,
                                            "readOnly": true,
                                            "field": {
                                                "key": "publicationresults",
                                                "showRowAmount": true,
                                                "allowDownload": true,
                                                "disableRemoval": true,
                                                //"showReferenceValue": true,
                                                "showReferenceState": true,
                                                "columnFields": [
                                                    "publicationresultspublicationid",
                                                    "publicationresultspublicationtitle"
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
                            data: commonSearchBooleans.initialData({}),
                            dataConf: {
                                "selectionLists": $.extend({}, response.configuration.selectionLists, {
                                    seriesname_list: {
                                        includeEmpty: true,
                                        key: 'seriesname_list',
                                        type: 'REFERENCE',
                                        reference: 'seriesname_ref'
                                    }
                                }),
                                references: {
                                    publicationresults_ref: {
                                        type: "REVISION",
                                        target: "PUBLICATION"
                                    },
                                    publicationresultspublicationid_ref: {
                                        type: "DEPENDENCY",
                                        target: "publicationresults",
                                        valuePath: "publicationid"
                                    },
                                    publicationresultspublicationtitle_ref: {
                                        type: "DEPENDENCY",
                                        target: "publicationresults",
                                        valuePath: "publicationtitle"
                                    },
                                    seriesname_ref: {
                                        key: 'seriesname_ref',
                                        type: 'REVISIONABLE',
                                        target: 'SERIES',
                                        valuePath: 'seriesname',
                                        titlePath: 'seriesname'
                                    }
                                },
                                fields: {
                                    publicationresults: {
                                        type: "REFERENCECONTAINER",
                                        reference: "publicationresults_ref",
                                        fixedOrder: true,
                                        subfields: [
                                            "publicationresultspublicationid",
                                            "publicationresultspublicationtitle"
                                        ]
                                    },
                                    publicationresultspublicationid: {
                                        key: "publicationresultspublicationid",
                                        subfield: true,
                                        type: "REFERENCE",
                                        reference: "publicationresultspublicationid_ref"
                                    },
                                    publicationresultspublicationtitle: {
                                        key: "publicationresultspublicationtitle",
                                        subfield: true,
                                        type: "REFERENCE",
                                        reference: "publicationresultspublicationtitle_ref"
                                    },
                                    publicationid: {
                                        type: 'STRING'
                                    },
                                    studies: {
                                        type: 'STRING'
                                    },
                                    publicationfirstsaved: {
                                        type: 'DATE'
                                    },
                                    savedAt: {
                                        type: 'DATE'
                                    },
                                    publicationyear: {
                                        type: 'INTEGER'
                                    },
                                    studyname: {
                                        type: 'STRING'
                                    },
                                    seriesname: {
                                        key: 'seriesname',
                                        type: 'SELECTION',
                                        selectionList: 'seriesname_list'
                                    },
                                    lastname: {
                                        type: 'STRING'
                                    },
                                    firstname: {
                                        type: 'STRING'
                                    },
                                    publicationtitle: {
                                        type: 'STRING'
                                    },
                                    publicationrelpubl: {
                                        type: 'STRING'
                                    },
                                    "publicationlanguage": {
                                        "type": "SELECTION",
                                        "selectionList": "langs"
                                    },
                                    "publicationpublic": {
                                        "type": "SELECTION",
                                        "selectionList": "yes_no"
                                    },
                                    savedBy: {
                                        type: 'STRING'
                                    }
                                }
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