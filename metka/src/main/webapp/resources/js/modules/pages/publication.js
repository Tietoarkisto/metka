define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var commonSearchBooleans = require('./../commonSearchBooleans');
        return function (options, onLoad) {
            $.extend(options, {
                header: MetkaJS.L10N.get('type.PUBLICATION.search'),
                content: [
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
                            }
                        ]
                    }
                ],
                buttons: [
                    require('./../searchButton')('/revision/ajax/search', function () {
                        var requestData = commonSearchBooleans.requestData(options, {
                            type: require('./../../metka').PAGE,
                            values: {}
                        });
                        [
                            'publicationfirstsaved',
                            'savedAt',
                            'publicationyear',
                            'studyname',
                            'seriesname',
                            'lastname',
                            'firstname',
                            'publicationtitle',
                            'publicationrelpubl',
                            'publicationlanguage',
                            'publicationpublic',
                            'savedBy'
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
                            seriesabbr: result.values.seriesabbr,
                            seriesname: result.values.seriesname,
                            state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                        };
                    }, {
                    }, [
                        "id",
                        "seriesabbr",
                        "seriesname",
                        "state"
                    ], function (transferRow) {
                        require('./../assignUrl')('view', {
                            id: transferRow.fields.id.value.current,
                            no: transferRow.fields.no.value.current
                        });
                    }),
                    {
                        "&title": {
                            "default": "Tyhjennä"
                        },
                        create: function () {
                            this.click(function () {
                                log('TODO: tyhjennä lomake')
                            });
                        }
                    },
                    {
                        "&title": {
                            "default": "Lisää uusi"
                        },
                        create: function () {
                            this
                                .click(function () {
                                    require('./../server')('create', {
                                        data: JSON.stringify({
                                            type: 'PUBLICATION'
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
                        "yes_no": {
                            "key": "yes_no",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": ""
                                    },
                                    "value": ''
                                },
                                {
                                    "&title": {
                                        "default": "Kyllä"
                                    },
                                    "value": 1
                                },
                                {
                                    "&title": {
                                        "default": "Ei"
                                    },
                                    "value": 0
                                }
                            ]
                        },
                        "langs": {
                            "key": "langs",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": ""
                                    },
                                    "value": ''
                                },
                                {
                                    "&title": {
                                        "default": "Suomi"
                                    },
                                    "value": 'fi'
                                },
                                {
                                    "&title": {
                                        "default": "Englanti"
                                    },
                                    "value": 'en'
                                },
                                {
                                    "&title": {
                                        "default": "Ruotsi"
                                    },
                                    "value": 'sv'
                                },
                                {
                                    "&title": {
                                        "default": "Muu"
                                    },
                                    "value": 'other'
                                }
                            ]
                        },
                        "publicationannouncement_list": {
                            "key": "publicationannouncement_list",
                            "type": "VALUE",
                            "options": [
                                {
                                    "&title": {
                                        "default": "Ei tietoa"
                                    },
                                    "value": 0
                                },
                                {
                                    "&title": {
                                        "default": "Alkup. tutkija ilmoit."
                                    },
                                    "value": 1
                                },
                                {
                                    "&title": {
                                        "default": "Oma paikannus"
                                    },
                                    "value": 2
                                },
                                {
                                    "&title": {
                                        "default": "käyttäjä ilmoit."
                                    },
                                    "value": 3
                                }
                            ]
                        }
                    },
                    fields: {
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
                            type: 'SELECTION'
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
        };
    } else {
        return require('./defaults');
    }
});