define(function (require) {
    'use strict';

    var filesContainerCreated = false;
    return function (options) {
        function view(requestOptions, onSaveSuccess) {
            require('./../../server')('viewAjax', $.extend({
                PAGE: 'STUDY_ATTACHMENT'
            }, requestOptions), {
                method: 'GET',
                success: function (data) {
                    var modalOptions = $.extend(data.gui, {
                        title: 'Muokkaa tiedostoa',
                        data: data.transferData,
                        dataConf: data.configuration,
                        $events: options.$events,
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
                                                "title": "Alkuperäinen",
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
                            "&title": {
                                "default": "Tallenna"
                            },
                            "type": "SAVE",
                            "isHandler": true,
                            "states": [
                                "DRAFT"
                            ],
                            "permissions": [
                                "canEditRevision"
                            ]
                        }, {
                            "type": "CUSTOM",
                            "title": "Tee luonnos",
                            "customHandler": "studyAttachmentEdit",
                            "permissions": [
                                "canEditRevision"
                            ],
                            "states": [
                                "APPROVED"
                            ]
                        }, {
                            "&title": {
                                "default": "Poista"
                            },
                            "type": "REMOVE",
                            "states": [
                                "DRAFT",
                                "APPROVED"
                            ],
                            "isHandler": true,
                            "permissions": [
                                "canRemoveRevision"
                            ]
                        }, {
                            type: 'CANCEL'
                        }]
                    });
                    require('./../../modal')(modalOptions);
                }
            });
        }

        if (filesContainerCreated) {
            return;
        }
        filesContainerCreated = true;

        return {
            create: function (options) {
                var $elem = this;

                require('./../../data')(options).onChange(function () {
                    function addFileContainers() {
                        require('./../../inherit')(function (options) {
                            require('./../../container').call($elem, options);
                        })(options)({
                            data: {
                                fields: fields
                            },
                            dataConf: $.extend(true, {
                                fields: {
                                    removedFiles: {
                                        "key": "removedFiles",
                                        "translatable": false,
                                        "type": "REFERENCECONTAINER",
                                        "reference": "attachment_ref",
                                        "subfields": [
                                            "filespath",
                                            "fileslang"
                                        ]}
                                }
                            }, options.dataConf),
                            content: [{
                                "type": "COLUMN",
                                "columns": 1,
                                "rows": [{
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Liitetyt tiedostot",
                                            "field": {
                                                "key": "files",
                                                "showSaveInfo": true,
                                                "showReferenceKey": true,
                                                "columnFields": [
                                                    "filespath",
                                                    "fileslang"
                                                ],
                                                onClick: function (transferRow, replaceTr) {
                                                    view({
                                                        id: transferRow.value,
                                                        no: ''
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
                                                                // FIXME: row was immediately created, but if dialog is dismissed, row won't be shown until page refresh
                                                                view(response.data.key, addRow);
                                                            }
                                                        }
                                                    });
                                                },
                                                onRemove: function ($tr) {
                                                    // TODO: don't show remove button
                                                    log($tr, $tr.data('transferRow'));
                                                }
                                            }
                                        }
                                    ]
                                }, {
                                    "type": "ROW",
                                    "cells": [
                                        {
                                            "type": "CELL",
                                            "title": "Poistetut tiedostot",
                                            readOnly: true,
                                            "field": {
                                                "key": "removedFiles",
                                                "showReferenceKey": true,
                                                "columnFields": [
                                                    "filespath",
                                                    "filedescription",
                                                    "filecomment"
                                                ]
                                            }
                                        }
                                    ]
                                }]
                            }]
                        });
                    }
                    function langRow() {
                        var o = {};
                        o[options.defaultLang] = [];
                        return o;
                    }
                    $elem.empty();
                    var fields = {
                        files: {
                            key: 'files',
                            rows: langRow()
                        },
                        removedFiles: {
                            key: 'removedFiles',
                            rows: langRow()
                        }
                    };
                    var rows = require('./../../data')(options).getByLang(options.defaultLang);
                    if (rows) {
                        var pendingResponseCount = rows.length;
                        rows.forEach(function (transferRow) {
                            require('./../../server')('/references/referenceStatus/{value}', transferRow, {
                                method: 'GET',
                                success: function (response) {
                                    fields[response.exists ? 'files' : 'removedFiles'].rows[options.defaultLang].push(transferRow);
                                    if (!--pendingResponseCount) {
                                        addFileContainers();
                                    }
                                }
                            });
                        });
                    } else {
                        addFileContainers();
                    }
                });
            }
        };
    };
});
