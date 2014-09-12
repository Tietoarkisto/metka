define(function (require) {
    'use strict';

    return function (options) {
        function view(requestOptions, onSaveSuccess) {
            var metka = require('./../../../metka');
            require('./../../server')('viewAjax', $.extend({
                page: 'study_variable'
            }, requestOptions), {
                method: 'GET',
                success: function (data) {
                    var modalOptions = $.extend(data.gui, {
                        title: 'Muokkaa muuttujaa',
                        data: data.transferData,
                        dataConf: data.configuration,
                        $events: $({}),
                        defaultLang: 'DEFAULT',
                        large: true,
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
                                                    "key": "varsecuritys",
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
                                                        "categorystat",
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
                        buttons: [{
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.save'))
                                    .click(require('./../../formAction')('save')(modalOptions, function (response) {
                                        require('./../../server')('/references/referenceRowRequest', {
                                            data: JSON.stringify({
                                                type: metka.PAGE,
                                                id: metka.id,
                                                no: metka.no,
                                                path: options.field.key,
                                                reference: response.data.key.id
                                            }),
                                            success: function (data) {
                                                onSaveSuccess(data.row);
                                            }
                                        });
                                    },
                                    [
                                        'SAVE_SUCCESSFUL',
                                        'SAVE_SUCCESSFUL_WITH_ERRORS',
                                        'NO_CHANGES_TO_SAVE'
                                    ]));
                            }
                        }, {
                            type: 'CANCEL'
                        }]
                    });
                    require('./../../modal')(modalOptions);
                }
            });
        }

        return {
            field: {
                onClick: function (transferRow, replaceTr) {
                    view({
                        id: transferRow.value
                    }, replaceTr);
                }
            }
        };
    };
});
