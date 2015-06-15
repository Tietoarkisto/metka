define(function (require) {
    'use strict';

    var filesContainerCreated = false;
    return function (options) {
        if (filesContainerCreated) {
            return;
        }
        filesContainerCreated = true;

        return {
            postCreate: function (options) {

                function partialRefresh() {
                    require('./../../server')('viewAjax', {
                        method: 'GET',
                        success: function (response) {
                            if (response.result === 'VIEW_SUCCESSFUL') {
                                // on browser, overwrite these fields only, since there might be other unsaved fields on page
                                ['files', 'studyvariables'].forEach(function (field) {
                                    options.data.fields[field] = options.data.fields[field] || {};
                                    $.extend(options.data.fields[field], response.data.fields[field]);
                                });
                                options.$events.trigger('refresh.metka');
                            }
                        }
                    });
                }

                function view(requestOptions) {
                    require('./../../server')('viewAjax', $.extend({
                        PAGE: 'STUDY_ATTACHMENT'
                    }, requestOptions), {
                        method: 'GET',
                        success: function (response) {

                            // TODO: check status
                            if (response.result === 'VIEW_SUCCESSFUL') {
                            }
                            var modalOptions = $.extend({}, response.gui, {
                                //title: 'Muokkaa tiedostoa',
                                data: response.data,
                                dataConf: response.configuration,
                                //$events: options.$events,
                                // TODO: Events need better management so that some events can be inherited and others can be overwritten
                                $events: $({}),
                                defaultLang: 'DEFAULT',
                                large: true,
                                dialogTitle: options.field.dialogTitle,
                                dialogTitles: options.dialogTitles
                            });
                            modalOptions.$events.on('attachment.refresh', partialRefresh);
                            // We need the isReadOnly function at this point so we need to add it before calling modal
                            modalOptions = $.extend(true, require('./../../optionsBase')(), modalOptions);
                            modalOptions.type = modalOptions.isReadOnly(modalOptions) ? 'VIEW' : 'MODIFY';
                            require('./../../modal')(modalOptions);
                        }
                    });
                }

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
                    "field": $.extend(true, {}, options.field, {
                        "displayType": null,
                        "key": "files",
                        "columnFields": [
                            "filepath",
                            "filelanguage"
                        ],
                        onClick: function (transferRow, replaceTr) {
                            view({
                                id: transferRow.value,
                                no: ''
                            }, replaceTr);
                        }
                    })
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
                                    view(response.data.key, addRow);
                                    partialRefresh();
                                }
                            }
                        });
                    };
                }
                addFileContainer($filesContainer, filesOptions);
                addFileContainer($removedFilesContainer, {
                    "type": "CELL",
                    "title": "Poistetut tiedostot",
                    "readOnly": true,
                    "field": $.extend(true, {}, options.field, {
                        "key": "files",
                        "displayType": "REFERENCECONTAINER",
                        "columnFields": [
                            "filepath",
                            "filedescription",
                            "filecomment"
                        ],
                        onClick: function (transferRow, replaceTr) {
                            view({
                                id: transferRow.value,
                                no: ''
                            }, replaceTr);
                        }
                    })
                });

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
            }
        };
    };
});
