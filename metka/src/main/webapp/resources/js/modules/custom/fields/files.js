define(function (require) {
    'use strict';

    function view(requestOptions, onSaveSuccess) {

        var metka = require('./../../../metka');
        require('./../../server')('viewAjax', $.extend({
            page: 'study_attachment'
        }, requestOptions), {
            method: 'GET',
            success: function (data) {
                var options = $.extend(data.gui, {
                    title: 'Muokkaa tiedostoa',
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
                                            "title": "Tiedoston polku",
                                            "horizontal": true,
                                            "field": {
                                                "key": "file"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Virallinen selite",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filedescription",
                                                "multiline": true
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Epävirallinen selite",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filenotes",
                                                "multiline": true
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Kommentti",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filecomment",
                                                "multiline": true
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Tyyppi",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filecategory"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "PAS",
                                            "horizontal": true,
                                            "field": {
                                                "key": "fileaip"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Kieli",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filelanguage"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Alkuperäinen kieli",
                                            "horizontal": true,
                                            "field": {
                                                "key": "fileoriginal"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "WWW",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filepublication"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Ulosluovutus",
                                            "horizontal": true,
                                            "field": {
                                                "key": "filedip"
                                            }
                                        }
                                    ]
                                },
                                {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Tiedostohistoria",
                                            "field": {
                                                "key": "custom_fileHistory"
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
                                .click(require('./../../formAction')('save')(options, function (data) {
                                    require('./../../server')('/references/referenceRowRequest', {
                                        data: JSON.stringify({
                                            type: 'STUDY',
                                            id: metka.id,
                                            no: metka.no,
                                            path: 'files',
                                            reference: data.key.id
                                        }),
                                        success: function (data) {
                                            onSaveSuccess(data.row);
                                        }
                                    });
                                }));
                        }
                    }, {
                        type: 'CANCEL'
                    }]
                });
                require('./../../modal')(options);
            }
        });
    }

    return function (options) {
        if (require('./../../isFieldDisabled')(options)) {
            return {};
        } else {
            return {
                field: {
                    onClick: function (transferRow, replaceTr) {
                        view({
                            id: transferRow.value
                        }, replaceTr);
                    },
                    onAdd: function (originalEmptyData, addRow) {
                        require('./../../server')('create', {
                            data: JSON.stringify({
                                type: 'STUDY_ATTACHMENT',
                                parameters: {
                                    study: require('./../../../metka').id
                                }
                            }),
                            success: function (response) {
                                if (response.result === 'REVISION_CREATED') {
                                    view(response.data.key, addRow);
                                }
                            }
                        });
                    },
                    onRemove: function ($tr) {
                        require('./../../server')('viewAjax', $.extend({
                            page: 'study_attachment',
                            id: $tr.data('transferRow').value
                        }), {
                            method: 'GET',
                            success: function (data) {
                                require('./../../server')('/revision/ajax/remove', {
                                    data: JSON.stringify(data.transferData),
                                    success: function (response) {
                                        $tr.remove();
                                    }
                                });
                            }
                        });
                    }
                }
            };
        }
    };
});
