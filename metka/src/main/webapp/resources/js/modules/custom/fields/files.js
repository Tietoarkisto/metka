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
                                            options.data = response.transferData;
                                            $elem.trigger('refresh.metka');
                                        }
                                    }
                                });
                            }
                            // TODO: check status
                            if (response.result === 'VIEW_SUCCESSFUL') {
                            }

                            var modalOptions = $.extend(response.gui, {
                                title: 'Muokkaa tiedostoa',
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
                                //readOnly: require('./../../isDataReadOnly')(response.transferData),
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
                                    "isHandler": true,
                                    "states": [
                                        "DRAFT"
                                    ],
                                    "permissions": [
                                        "canEditRevision"
                                    ],
                                    create: function () {
                                        this.click(require('./../../save')(modalOptions, refreshPage));
                                    }
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
                                    "title": "Poista",
                                    "states": [
                                        "DRAFT",
                                        "APPROVED"
                                    ],
                                    "isHandler": true,
                                    "permissions": [
                                        "canRemoveRevision"
                                    ],
                                    create: function () {
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
                                    request: {
                                        success: refreshPage
                                    }
                                }, {
                                    type: 'CANCEL'
                                }]
                            });

                            require('./../../modal')(modalOptions);
                        }
                    });
                }
                var $elem = this;

                require('./../../data')(options).onChange(function () {
                    function addFileContainers() {
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
                                ],
                                onClick: function (transferRow, replaceTr) {
                                    view({
                                        id: transferRow.value,
                                        no: ''
                                    }, replaceTr);
                                }
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
                        var i = 0;
                        (function processNextRow() {
                            if (i < rows.length) {
                                var transferRow = rows[i++];
                                require('./../../server')('/references/referenceStatus/{value}', transferRow, {
                                    method: 'GET',
                                    success: function (response) {
                                        if (response.exists) {
                                            (!response.removed ? files : removedFiles).rows[options.defaultLang].push(transferRow);
                                        }
                                        processNextRow();
                                    }
                                });
                            } else {
                                addFileContainers();
                            }
                        })(0);
                    } else {
                        addFileContainers();
                    }
                });
            }
        };
    };
});
