define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans');
        return function (options, onLoad) {
            $.extend(options, {
                header: MetkaJS.L10N.get('type.STUDY.search'),
                fieldTitles: {
                    "studyid": {
                        "key": "studyid",
                        "title": "Aineiston numero"
                    },
                    "studyname": {
                        "key": "studyname",
                        "title": "Aineiston nimi"
                    },
                    "authors": {
                        "key": "authors",
                        "title": "Tekijät"
                    },
                    "seriesname": {
                        "key": "seriesname",
                        "title": "Sarja"
                    },
                    "datakind": {
                        "key": "datakind",
                        "title": "Laatu"
                    },
                    "termsofuse": {
                        "key": "termsofuse",
                        "title": "Käyttöoikeus"
                    },
                    "state": {
                        "key": "state",
                        "title": "Tila"
                    }
                },
                content: [
                    {
                        "type": "TAB",
                        "title": "Aineistohaku",
                        "content": [
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
                                                "title": "Aineiston numero",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "studyid"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Hankinta-aineistonumero",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "submissionid"
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
                                                    "key": "title"
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
                                                    "key": "author.lastname"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "Etunimi",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "author.firstname"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tekijän organisaatio",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "authororganization"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tuottajan nimi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "producername"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tuottajan rooli",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "producerrole"
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
                                                "title": "Aineiston laatu",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "datakind"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Anonymisointi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "anonymization"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tietosuoja",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "securityissues"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Julkaisu",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "publication"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Valmis-päivämäärä",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "publicationfirstsaved"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "-",
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
                                                "title": "Ehto 1: käyttöoikeus",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "termsofuse"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Käyttöehto muutospvm jälkeen",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "newtermsofuse"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Käyttöehdon muutospvm",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "termsofusechangedate"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "-",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "termsofusechangedate.until"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Arkistointisopimuksen tapa",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "agreementtype"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Luovuttajan tyyppi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "depositortype"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Käsittelijä",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "publication.savedBy"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "URN-tunniste",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "packageurn"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Abstrakti",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "abstract"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tieteenala",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "topic"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Ajallinen kattavuus",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "timeperiod"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "-",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "timeperiod.until"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aineistonkeruun ajankohta",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "colltime"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "-",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "colltime.until"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Maa",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "country"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aineiston kerääjän nimi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "collector"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Havainto/aineistoyksikkö",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "analysisunit"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aikaulottuvuus",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "timemethod"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Otantamenetelmä",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "sampproc"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Keruumenetelmä",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "collmode"
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "type": "TAB",
                        "title": "Virheelliset",
                        "content": []
                    }
                ],
                buttons: [
                    require('./../searchButton')('searchAjax', function () {
                        var requestData = commonSearchBooleans.requestData(options, {
                            type: require('./../../metka').PAGE,
                            values: {}
                        });
                        [
                            'studyid',
                            'submissionid',
                            'title',
                            'author.lastname',
                            'author.firstname',
                            'authororganization',
                            'producername',
                            'producerrole',
                            'seriesname',
                            'datakind',
                            'anonymization',
                            'securityissues',
                            'publication',
                            'publicationfirstsaved',
                            'savedAt',
                            'termsofuse',
                            'newtermsofuse',
                            'termsofusechangedate',
                            'termsofusechangedate.until',
                            'agreementtype',
                            'depositortype',
                            'publication.savedBy',
                            'packageurn',
                            'abstract',
                            'topic',
                            'timeperiod',
                            'timeperiod.until',
                            'colltime',
                            'colltime.until',
                            'country',
                            'collector',
                            'analysisunit',
                            'timemethod',
                            'sampproc',
                            'collmode'
                        ].forEach(function (field) {
                            requestData.values[field] = data(field).getByLang(options.defaultLang);
                        });
                        return requestData;
                    }, function (data) {
                        return data.rows;
                    }, function (result) {
                        return {
                            id: result.id,
                            no: result.no,
                            studyid: result.values.studyid,
                            studyname: result.values.studyname,
                            authors: result.values.authors.join(', '),
                            seriesname: result.values.seriesname,
                            datakind: result.values.datakind,
                            termsofuse: result.values.termsofuse,
                            state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                        };
                    }, {
                    }, [
                        "studyid",
                        "studyname",
                        "authors",
                        "seriesname",
                        "datakind",
                        "termsofuse",
                        "state"
                    ], function (transferRow) {
                        require('./../assignUrl')('view', {
                            id: transferRow.fields.id.value.current,
                            no: transferRow.fields.no.value.current
                        });
                    }),
                    /*{
                        "&title": {
                            "default": "Tyhjennä"
                        },
                        create: function () {
                            this.click(function () {
                                log('TODO: tyhjennä lomake')
                            });
                        }
                    },*/
                    {
                        "&title": {
                            "default": "Lisää uusi"
                        },
                        create: function () {
                            this
                                .click(function () {
                                    require('./../server')('create', {
                                        data: JSON.stringify({
                                            type: 'STUDY',
                                            parameters: {
                                                submissionid: Date.now() % 1000,
                                                dataarrivaldate: moment(Date.now()).format('YYYY-MM-DDThh:mm:ss.s')
                                            }
                                        }),
                                        success: function (response) {
                                            if (response.result === 'REVISION_CREATED') {
                                                require('./../assignUrl')('view', {
                                                    id: response.data.key.id,
                                                    no: response.data.key.no,
                                                    page: response.data.configuration.type.toLowerCase()
                                                });
                                            }
                                        }
                                    });
                                });
                        }
                    }
                ],
                data: commonSearchBooleans.initialData({}),
                dataConf: {
                    "selectionLists": {
                        "producerrole_list": {
                            "key": "producerrole_list",
                            "type": "VALUE",
                            "options": [
                                {
                                    "value": "1",
                                    "&title": {
                                        "default":"Rahoittaja"
                                    }
                                }, {
                                    "value": "2",
                                    "&title": {
                                        "default":"Projekti"
                                    }
                                }
                            ]
                        },
                        "studies": {
                            "key": "roles",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": "a"
                                    },
                                    "value": 'a'
                                },
                                {
                                    "&title": {
                                        "default": "b"
                                    },
                                    "value": 'b'
                                }
                            ]
                        }
                    },
                    fields: {
                        title: {
                            type: 'STRING'
                        },
                        studyid: {
                            type: 'STRING'
                        },
                        submissionid: {
                            "type": "INTEGER"
                        },
                        author: {
                            lastname: {
                                "type": "STRING"
                            },
                            firstname: {
                                "type": "STRING"
                            }
                        },
                        authororganization: {
                            "type": "STRING"
                        },
                        producername: {
                            "type": "STRING"
                        },
                        producerrole: {
                            "type": "SELECTION",
                            "selectionList": "producerrole_list"
                        },
                        studyname: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        datakind: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        anonymization: {
                            "type": "STRING"
                        },
                        securityissues: {
                            "type": "STRING"
                        },
                        publication: {
                            "type": "SELECTION",
                            "selectionList": "studies",

                            savedBy: {
                                "type": "STRING"
                            }
                        },
                        publicationfirstsaved: {
                            type: 'DATE'
                        },
                        savedAt: {
                            type: 'DATE'
                        },
                        termsofuse: {
                            "type": "STRING"
                        },
                        newtermsofuse: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        termsofusechangedate: {
                            "type": "DATE",
                            until: {
                                "type": "DATE"
                            }
                        },
                        agreementtype: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        depositortype: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        packageurn: {
                            "type": "STRING"
                        },
                        abstract: {
                            "type": "STRING"
                        },
                        topic: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        timeperiod: {
                            "type": "DATE",
                            until: {
                                "type": "DATE"
                            }
                        },
                        colltime: {
                            "type": "DATE",
                            until: {
                                "type": "DATE"
                            }
                        },
                        country: {
                            "type": "STRING"
                        },
                        collector: {
                            "type": "STRING"
                        },
                        analysisunit: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        timemethod: {
                            "type": "SELECTION",
                            "selectionList": "studies"
                        },
                        sampproc: {
                            "type": "STRING"
                        },
                        collmode: {
                            "type": "STRING"
                        }
                    }
                }
            });
            var data = require('./../data')(options);
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});