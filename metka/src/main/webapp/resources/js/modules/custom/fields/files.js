define(function (require) {
    'use strict';

    var filesContainerCreated = false;
    return function (options) {
        if (filesContainerCreated) {
            return;
        }
        filesContainerCreated = true;

        return {
            create: function (options) {

                function view(requestOptions) {
                    require('./../../server')('viewAjax', $.extend({
                        PAGE: 'STUDY_ATTACHMENT'
                    }, requestOptions), {
                        method: 'GET',
                        success: function (response) {
                            function refreshPage() {
                                    require('./../../server')('viewAjax', {
                                        method: 'GET',
                                        success: function (response) {
                                            if (response.result === 'VIEW_SUCCESSFUL') {
                                                // on browser, overwrite these fields only, since there might be other unsaved fields on page
                                                ['files', 'variables'].forEach(function (field) {
                                                    options.data.fields[field] = options.data.fields[field] || {};
                                                    $.extend(options.data.fields[field], response.transferData.fields[field]);
                                                });
                                                $elem.trigger('refresh.metka');
                                            }
                                        }
                                    });
                            }
                            // TODO: check status
                            if (response.result === 'VIEW_SUCCESSFUL') {
                            }
                            var modalOptions = $.extend(response.gui, {
                                //title: 'Muokkaa tiedostoa',
                                data: response.transferData,
                                dataConf: $.extend(true, response.configuration, {
                                    "fields": {
                                        "no": {
                                            "key": "no",
                                            "type": "STRING",
                                            "subfield": true,
                                            "editable": false,
                                            "writable": false
                                        },
                                        "date": {
                                            "key": "date",
                                            "type": "STRING",
                                            "subfield": true,
                                            "editable": false,
                                            "writable": false
                                        },
                                        "user": {
                                            "key": "user",
                                            "type": "STRING",
                                            "subfield": true,
                                            "editable": false,
                                            "writable": false
                                        },
                                        "state": {
                                            "key": "state",
                                            "type": "STRING",
                                            "subfield": true,
                                            "editable": false,
                                            "writable": false
                                        }
                                    }
                                }),
                                $events: options.$events,
                                defaultLang: 'DEFAULT',
                                large: true,
                                fieldTitles: {
                                    "date": {
                                        "key": "date",
                                        "title": "Tallennettu"
                                    },
                                    "user": {
                                        "key": "user",
                                        "title": "Tallentaja"
                                    },
                                    "no": {
                                        "key": "no",
                                        "title": "Revisio"
                                    },
                                    "state": {
                                        "key": "user",
                                        "title": "Tila"
                                    },
                                    "filecomment": {
                                        "key": "filecomment",
                                        "title": "Kommentti"
                                    }
                                },
                                dialogTitle: options.field.dialogTitle,
                                dialogTitles: options.dialogTitles,
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
                                                        // Hide this field, if there's no value. Variables will be parsed on next save
                                                        hidden: typeof require('./../../utils/getPropertyNS')(response.transferData, 'fields.parsed.values.DEFAULT.current') === 'undefined',
                                                        "title": "Parsi muuttujat uudelleen tallennuksen yhteydessä",
                                                        "field": {
                                                            "reverseBoolean": true,
                                                            "key": "parsed"
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
                                                            "columnFields": [
                                                                "no",
                                                                "state",
                                                                "filecomment",
                                                                "date",
                                                                "user"
                                                            ],

                                                            onClick: function (transferRow) {
                                                                var $row = $(this);
                                                                require('./../../server')('viewAjax', {
                                                                    PAGE: 'STUDY_ATTACHMENT',
                                                                    no: transferRow.fields.no.values.DEFAULT.current,
                                                                    id: requestOptions.id
                                                                }, {
                                                                    method: 'GET',
                                                                    success: function (response) {
                                                                        if (response.result === 'VIEW_SUCCESSFUL') {
                                                                            $.extend(modalOptions.data, response.transferData);
                                                                            modalOptions.type = modalOptions.isReadOnly(modalOptions) ? 'VIEW' : 'MODIFY';
                                                                            $row.trigger('refresh.metka');
                                                                        }
                                                                    }
                                                                })
                                                            }
                                                        },
                                                        create: function () {
                                                            var $containerField = $(this).children();
                                                            require('./../../server')('/study/attachmentHistory/', {
                                                                data: JSON.stringify(modalOptions.data),
                                                                success: function (data) {
                                                                    var objectToTransferRow = require('./../../map/object/transferRow');
                                                                    var revisions = data.rows.map(function (result) {

                                                                        return {
                                                                            no: result.no,
                                                                            state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result)),
                                                                            filecomment: result.values["filecomment"],
                                                                            date: result.values["date"],
                                                                            user: result.values["user"]
                                                                        };
                                                                    }).map(function (result) {
                                                                        return objectToTransferRow(result, "DEFAULT");
                                                                    });

                                                                    revisions && revisions.forEach(function (row) {
                                                                        $containerField.data('addRow')(row);
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
                                    "title": "Tallenna",
                                    "states": [
                                        "DRAFT"
                                    ],
                                    "isHandler": true,
                                    "permissions": [
                                        "canEditRevision"
                                    ],
                                    create: function (options) {
                                        options.preventDismiss = true;
                                        var $this = $(this);
                                        this.click(require('./../../save')(modalOptions, function(response) {
                                            // TODO: Check that if result is SAVE_SUCCESSFUL_WITH_ERRORS then don't close the dialog and instead reload data from TransferData
                                            if(response.result === 'SAVE_SUCCESSFUL_WITH_ERRORS') {
                                                $.extend(true, options.data, response.data);
                                                //options.$events.trigger('refresh.metka');
                                                $this.trigger('refresh.metka');
                                            } else {
                                                $('#'+options.modalTarget).modal('hide');
                                            }
                                            refreshPage();
                                        }));
                                    }
                                }, {
                                    "type": "EDIT",
                                    "title": "Tee luonnos",
                                    "isHandledByUser": "study",
                                    "permissions": [
                                        "canEditRevision"
                                    ],
                                    "states": [
                                        "APPROVED"
                                    ]
                                }, {
                                    "title": "Poista",
                                    "states": [
                                        "DRAFT",
                                        "APPROVED"
                                    ],
                                    "isHandler": true,
                                    "isHandledByUser": "study",
                                    "permissions": [
                                        "canRemoveRevision"
                                    ],
                                    create: function (options) {
                                        this.click(require('./../../remove')($.extend({
                                            success: {
                                                SUCCESS_LOGICAL: refreshPage,
                                                SUCCESS_DRAFT: refreshPage,
                                                FINAL_REVISION: refreshPage
                                            }
                                        }, modalOptions)));
                                    }
                                }, {
                                    "title": "Palauta",
                                    "type": "RESTORE",
                                    "states": [
                                        "REMOVED"
                                    ],
                                    "permissions": [
                                        "canRestoreRevision"
                                    ],
                                    "isHandledByUser": "study",
                                    request: {
                                        success: refreshPage
                                    }
                                }, {
                                    type: 'CANCEL'
                                }]
                            });
                            // We need the isReadOnly function at this point so we need to add it before calling modal
                            modalOptions = $.extend(true, require('./../../optionsBase')(), modalOptions);
                            modalOptions.type = modalOptions.isReadOnly(modalOptions) ? 'VIEW' : 'MODIFY';
                            require('./../../modal')(modalOptions);
                        }
                    });
                }
                var $elem = this;
                var $filesContainer = $('<div>').appendTo(this);
                var $removedFilesContainer = $('<div>').appendTo(this);

                function addFileContainer($mount, cell) {
                    require('./../../inherit')(function (options) {
                        require('./../../container').call($mount, options);
                    })(options)({
                        data: {},
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
                var filesOptions = {
                    "type": "CELL",
                    "title": "Liitetyt tiedostot",
                    "readOnly": true,
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
                        }
                    }
                };
                if (!require('./../../isFieldDisabled')(options, 'DEFAULT')) {
                    filesOptions.field.onAdd = function (originalEmptyData, addRow) {
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
                    };
                }
                addFileContainer($filesContainer, filesOptions);
                addFileContainer($removedFilesContainer, {
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
                        ],
                        onClick: function (transferRow, replaceTr) {
                            view({
                                id: transferRow.value,
                                no: ''
                            }, replaceTr);
                        }
                    }
                });

                require('./../../data')(options).onChange(function () {
                    $filesContainer.find('tbody').empty();
                    $removedFilesContainer.find('tbody').empty();

                    var rows = require('./../../data')(options).getByLang(options.defaultLang);
                    if (rows) {
                        var i = 0;
                        (function processNextRow() {
                            if (i < rows.length) {
                                var transferRow = rows[i++];
                                require('./../../server')('/references/referenceStatus/{value}', transferRow, {
                                    method: 'GET',
                                    success: function (response) {
                                        if (response.exists) {
                                            (!response.removed ? $filesContainer : $removedFilesContainer).find('.panel').parent().data('addRow')(transferRow);
                                        }
                                        processNextRow();
                                    }
                                });
                            }
                        })(0);
                    }
                });
            }
        };
    };
});
