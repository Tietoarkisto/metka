define(function (require) {
    'use strict';

    var filesContainerCreated = false;
    return function (options) {
        function view(requestOptions, onSaveSuccess) {
            require('./../../server')('viewAjax', $.extend({
                PAGE: 'STUDY_ATTACHMENT'
            }, requestOptions), {
                method: 'GET',
                success: function (response) {
                    // TODO: check status
                    if (response.result === 'VIEW_SUCCESSFUL') {
                    }

                    var modalOptions = $.extend(response.gui, {
                        title: 'Muokkaa tiedostoa',
                        data: response.transferData,
                        dataConf: response.configuration,
                        readOnly: require('./../../isDataReadOnly')(response.transferData),
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
                                                // TODO: näytä huomautus, jos valinnan muutos ja tallennus aiheuttavat muutoksia muuttujiin
                                                /*,
                                                create: function () {
                                                    var $select = this.find('select');
                                                    var prev = $select.val();
                                                    $select.change(function () {
                                                        switch ($(this).val()) {
                                                            case '1':
                                                                var parsed = require('./../../data')(modalOptions)('parsed').getByLang(options.defaultLang);
                                                                if (parsed && parsed.bool()) {
                                                                    require('./../../modal')({
                                                                        title: 'Poistetaanko muuttujat?'
                                                                    });
                                                                    return;
                                                                }
                                                            case '2':
                                                            case '3':
                                                                require('./../../modal')({
                                                                    title: 'Muuttujat parsitaan'
                                                                });

                                                        }
                                                        var prev = $select.val();
                                                    });
                                                }*/
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
                                                "readOnly": true,
                                                "field": {
                                                    "displayType": "CONTAINER",
                                                    "key": "custom_fileHistory",
                                                    "showSaveInfo": true,
                                                    "columnFields": [
                                                        //"date",
                                                        //"user",
                                                        "filecomment"
                                                    ]
                                                },
                                                create: function () {
                                                    var $containerField = $(this).children();
                                                    require('./../../server')('/study/attachmentHistory/', {
                                                        data: JSON.stringify(modalOptions.data),
                                                        success: function (data) {
                                                            data.rows && data.rows.forEach(function (row) {
                                                                $containerField.data('addRowFromDataObject')(row.values);
                                                            });
                                                        }
                                                    });
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
                    function addFileConteiners() {
                        function addFileContainer(files, cell) {
                            require('./../../inherit')(function (options) {
                                require('./../../container').call($elem, options);
                            })(options)({
                                data: {
                                    fields: {
                                        files: files
                                    }
                                },
                                content: [{
                                    "type": "COLUMN",
                                    "columns": 1,
                                    "rows": [{
                                        "type": "ROW",
                                        "cells": [cell]
                                    }]
                                }]
                            });
                        }
                        addFileContainer(files, {
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
                        });
                        addFileContainer(removedFiles, {
                            "type": "CELL",
                            "title": "Poistetut tiedostot",
                            readOnly: true,
                            "field": {
                                "key": "files",
                                "showReferenceKey": true,
                                "columnFields": [
                                    "filespath",
                                    "filedescription",
                                    "filecomment"
                                ]
                            }
                        });
                    }
                    function dataField(key) {
                        var rows = {};
                        rows[options.defaultLang] = [];
                        return {
                            key: key,
                            rows: rows
                        };
                    }
                    $elem.empty();
                    var files = dataField('files');
                    var removedFiles = dataField('removedFiles');

                    var rows = require('./../../data')(options).getByLang(options.defaultLang);
                    if (rows) {
                        var pendingResponseCount = rows.length;
                        rows.forEach(function (transferRow) {
                            require('./../../server')('/references/referenceStatus/{value}', transferRow, {
                                method: 'GET',
                                success: function (response) {
                                    if (response.exists) {
                                        (!response.removed ? files : removedFiles).rows[options.defaultLang].push(transferRow);
                                    }

                                    // TODO: to maintain row order, trigger new ajax request here
                                    if (!--pendingResponseCount) {
                                        addFileConteiners();
                                    }
                                }
                            });
                        });
                    } else {
                        addFileConteiners();
                    }
                });
            }
        };
    };
});
