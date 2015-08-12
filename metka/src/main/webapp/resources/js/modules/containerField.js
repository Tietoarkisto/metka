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

    var resultParser = require('./resultParser');

    /**
     * Creates table (container field or reference container) field.
     *
     * @this {jQuery} Language specific element.
     * @param {object} options UI configuration of this field, with properties for accessing data, configurations etc.
     * @param {object} lang. Language of this field.
     * @return {undefined} No return value.
     */
    return function (options, lang) {
        var getPropertyNS = require('./utils/getPropertyNS');

        function addRowCommand($container, className, html, click, rowFilter) {
            rowCommands.push({
                className: className,
                html: html,
                rowFilter: rowFilter
            });
            $container
                .on('click', 'tr button.' + className, click);
        }

        function getTitle(fieldKey) {
            return MetkaJS.L10N.localize(options.fieldTitles[fieldKey], "title");
        }

        function th(text) {
            return $('<th>')
                .text(text);
        }

        function tableError(errors) {
            if (errors && errors.length) {
                this
                    .addClass('danger')
                    .tooltip({
                        container: 'body',
                        title: require('./dataValidationErrorText')(errors),
                        html: true
                    });
            }
        }

        function tr(transferRow) {
            var key = options.field.key;
            var $tr = $('<tr>');
            var infoTDs = {
                saved: {},
                approved: {}
            };

            tableError.call($tr, transferRow.errors);

            if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                if(options.field.showReferenceValue) {
                    infoTDs.value = $('<td>').text(EMPTY);
                    $tr.append(infoTDs.value);
                }

                if(options.field.showReferenceType) {
                    infoTDs.type = $('<td>').text(EMPTY);
                    $tr.append(infoTDs.type);
                }
            }

            $tr
                .data('transferRow', transferRow)
                .append(columns.map(function (column, i) {
                    var $td = $('<td>')
                        .toggleClass('hiddenByTranslationState', $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'));

                    if (options.fieldOptions.type === 'REFERENCECONTAINER') {
                        require('./reference').optionByPath(column, options, options.defaultLang, function(option) {
                            var columnOptions = $.extend(
                                true
                                , {}
                                , getPropertyNS(options, 'dataConf.fields', column)
                                , options.subfieldConfiguration && options.subfieldConfiguration[column] ? options.subfieldConfiguration[column].field : {});

                            if(columnOptions && columnOptions.displayType === 'LINK') {
                                require('./inherit')(function(options) {
                                    require('./linkField')($td, options, "DEFAULT");
                                })(options)({
                                    fieldOptions: getPropertyNS(options, 'dataConf.fields', column),
                                    field: columnOptions,
                                    data: $.extend({}, options.data, {fields: transferRow.fields})
                                });
                            } else {
                                var value = require('./selectInputOptionText')(option);
                                $td.text(!!value && value.length ? value : EMPTY);
                            }
                        })(null, null, transferRow.value);

                        return $td;
                    }

                    // This fetches the correct display text for each table cell. It would make sense to split and generalize this better but the amount of work makes it
                    // not a priority at the moment.
                    (function ($td) {
                        var columnLang = $thead.children('tr').children().eq(i).data('lang') || lang;

                        var columnOptions = $.extend(
                            true
                            , {}
                            , getPropertyNS(options, 'dataConf.fields', column)
                            , options.subfieldConfiguration && options.subfieldConfiguration[column] ? options.subfieldConfiguration[column].field : {});

                        function setText(option) {
                            var text = require('./selectInputOptionText')(option);
                            $td.text(typeof text === 'undefined' ? EMPTY : text);
                        }

                        function setOptionText(listOptions) {
                            setText(listOptions.find(function (option) {
                                return option.value === value;
                            }));
                        }
                        var dataConf = getPropertyNS(options, 'dataConf.fields', column);
                        var type = columnOptions && columnOptions.displayType ? columnOptions.displayType : columnOptions.type;
                        //var type = getPropertyNS(options, 'dataConf.fields', column, 'type');

                        if (!type) {
                            log('not implemented', column);
                            $td.text(EMPTY);
                            return;
                        }

                        var transferField = getPropertyNS(transferRow, 'fields', column);
                        var value = (function () {
                            if (!transferField) {
                                return;
                            }

                            tableError.call($td, (transferField.errors || []).concat(getPropertyNS(transferField, 'values', columnLang, 'errors') || []));

                            if (!transferField.type) {
                                log('transferField type not set (column: {column})'.supplant({
                                    column: column
                                }));
                                return;
                            }

                            if (transferField.type !== 'VALUE') {
                                log('not implemented (type: {type}, column: {column})'.supplant({
                                    type: transferField.type,
                                    column: column
                                }));
                                return;
                            }

                            return require('./data').latestValue(transferField, columnLang);
                        })();

                        if(!value && type !== 'REFERENCE') {
                            $td.text(EMPTY);
                            return;
                        }

                        switch(type) {
                            case 'REFERENCE': {
                                var refKey = getPropertyNS(options, 'dataConf.fields', column, 'reference');
                                var reference = getPropertyNS(options, 'dataConf.references', refKey);

                                require('./reference').optionByPath(column, options, columnLang, setText)(transferRow.fields, reference);
                                return;
                            }
                            case 'SELECTION': {
                                var list = require('./selectionList')(options, column);
                                if (!list) {
                                    $td.text(EMPTY);
                                    break;
                                }
                                if (list.type === 'REFERENCE') {
                                    require('./reference').optionsByPath(column, options, lang, setOptionText)(transferRow.fields, getPropertyNS(options, 'dataConf.references', list.reference));
                                } else {
                                    setOptionText(list.options);
                                }
                                break;
                            }
                            case 'STRING': {
                                $td.text(value);
                                break;
                            }
                            case 'INTEGER':
                            case 'REAL': {
                                $td.text(value);
                                break;
                            }
                            case 'RICHTEXT': {
                                $td.html(value);
                                break;
                            }
                            case 'DATE':
                            case 'TIME':
                            case 'DATETIME': {
                                $td.text(moment(value).format(require('./dateFormats')[type]));
                                break;
                            }
                            case 'LINK': {
                                require('./inherit')(function(options) {
                                    require('./linkField')($td, options, "DEFAULT");
                                })(options)({
                                    fieldOptions: getPropertyNS(options, 'dataConf.fields', column),
                                    field: columnOptions,
                                    data: $.extend({}, options.data, {fields: transferRow.fields})
                                });
                                break;
                            }
                            default: {
                                log('not implemented', column, type);
                                $td.text(EMPTY);
                                break;
                            }
                        }
                    })($td);
                    return $td;
                }));

            if (options.field.showSaveInfo) {
                $tr.append(
                    $('<td>')
                        .text(transferRow.saved ? moment(transferRow.saved.time).format(require('./dateFormats')['DATE']) : EMPTY),
                    $('<td>')
                        .text(transferRow.saved ? transferRow.saved.user : EMPTY)
                );
            }

            if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                if(infoTDs.value) {
                    infoTDs.value.text(transferRow.value);
                }
                if(options.field.showReferenceSaveInfo) {
                    infoTDs.saved.at = $('<td>').text(EMPTY);
                    infoTDs.saved.by = $('<td>').text(EMPTY);
                    $tr.append(infoTDs.saved.at, infoTDs.saved.by);
                }
                if(options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0) {
                    $.each(options.field.showReferenceApproveInfo, function(index, lang) {
                        lang = lang.toUpperCase();
                        infoTDs.approved[lang] = {};
                        infoTDs.approved[lang].at = $('<td>').text(EMPTY);
                        infoTDs.approved[lang].by = $('<td>').text(EMPTY);
                        infoTDs.approved[lang].revision = $('<td>').text(EMPTY);
                        $tr.append(infoTDs.approved[lang].at, infoTDs.approved[lang].by, infoTDs.approved[lang].revision);
                    });
                }
                if(options.field.showReferenceState) {
                    infoTDs.state = $('<td>').text(EMPTY);
                    $tr.append(infoTDs.state);
                }
                if(options.field.showReferenceType
                        || options.field.showReferenceSaveInfo
                        || (options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0)
                        || options.field.showReferenceState) {
                    require('./server')('/references/referenceStatus/{value}', transferRow, {
                        method: 'GET',
                        success: function (response) {
                            if(resultParser(response.result).getResult() !== 'REVISION_FOUND') {
                                require('./resultViewer')(response.result);
                                return false;
                            }
                            if(infoTDs.type) {
                                if(response.type) {
                                    infoTDs.type.text(MetkaJS.L10N.get('type.'+response.type+".title"))
                                }
                            }
                            if(response.saved) {
                                if(response.saved.time && infoTDs.saved.at) {
                                    infoTDs.saved.at.text(moment(response.saved.time).format(require('./dateFormats')['DATE']))
                                }
                                if(response.saved.user && infoTDs.saved.by) {
                                    infoTDs.saved.by.text(response.saved.user);
                                }
                            }
                            $.each(response.approved, function(key, value) {
                                key = key.toUpperCase();
                                if(infoTDs.approved[key] && value) {
                                    if(infoTDs.approved[key].at && value.approved.time) {
                                        infoTDs.approved[key].at.text(moment(value.approved.time).format(require('./dateFormats')['DATE']))
                                    }
                                    if(infoTDs.approved[key].by && value.approved.user) {
                                        infoTDs.approved[key].by.text(value.approved.user);
                                    }
                                    if(infoTDs.approved[key].revision && value.revision) {
                                        infoTDs.approved[key].revision.text(value.revision);
                                    }
                                }
                            });
                            if(infoTDs.state) {
                                if(response.state) {
                                    infoTDs.state.text(MetkaJS.L10N.get('state.'+response.state));
                                } else {
                                    infoTDs.state.text(EMPTY);
                                }
                            }
                        }
                    });
                }
            }

            if (rowCommands.length) {
                $tr.append($('<td>')
                    .css('text-align', 'right')
                    .append($('<div class="btn-group btn-group-xs">')
                        .append(rowCommands.map(
                            function (button) {
                                if(button.rowFilter && !button.rowFilter(transferRow)) {
                                    return false;
                                }
                                return $('<button type="button" class="btn btn-default">')
                                    .addClass(button.className)
                                    .html(button.html);
                            })
                        )
                    )
                );
            }

            return $tr;
        }

        function redrawHeader() {
            containerHeader.redraw();
        }

        function redraw(event, page) {
            $tbody.empty();
            var rows = require('./data')(options).getByLang(lang);
            redrawHeader();
            if (rows) {
                //only if paging is enabled change current behavior
                if (options.field.rowsPerPage != null) {

                    //always trigger pager redraw with current row count
                    //if we redraw component where pager is
                    var redrawPaging = 'redraw-{key}-paging'.supplant({
                        key: options.field.key
                    });
                    options.$events.trigger(redrawPaging, [options.field.rowsPerPage, require('./data')(options).validRows(lang)]);

                    //if there is "paging-info" on $tbody.data and new page to draw is not provided
                    //we get current page from .data and just redraw that
                    //otherwise we can just go to page 1
                    var currentPage = page;
                    if (!page && $tbody.data("currentPage") != null) {
                        currentPage = $tbody.data("currentPage");
                    } else if (!page) {
                        currentPage = 1;
                    }

                    //now that we know currentPage we store it to tbody.data so
                    //we can get it in case of redraw event
                    $tbody.data("currentPage", currentPage);

                    var lastElement = currentPage * options.field.rowsPerPage;
                    if (lastElement > rows.length) {
                        lastElement = rows.length;
                    }

                    for (var i = ((currentPage - 1) * options.field.rowsPerPage); i < lastElement; i++) {
                        appendRow($tbody, rows[i], columns);
                    }

                } else {
                    rows.forEach(function (transferRow) {
                        appendRow($tbody, transferRow, columns)
                    });
                }
            }
            return $tbody;
        }

        function appendRow($container, transferRow, columnList) {
            function append() {
                var $tr = tr(transferRow);
                $container.append($tr);
                $container.trigger('rowAppended', [$tr, columnList]);
                return $tr;
            }

            if (!transferRow.removed) {
                return append();
            }
        }

        function addRow($container, transferRow, columnList) {
            require('./data')(options).appendByLang(lang, transferRow);

            //if paging is enabled and we add a new row just redraw current container page
            if(options.field.rowsPerPage) {
                var redrawKey = 'redraw-{key}'.supplant({
                    key: options.field.key
                });
                options.$events.trigger(redrawKey);
            } else {
                //otherwise we can just append new row without redrawing whole page
                var $tr = appendRow($container, transferRow, columnList);
                //redraw header because if showRowAmount is enabled we need to update that
                containerHeader.redraw();
                return $tr;
            }
        }

        function addRowFromDataObject($container, data, columnList) {
            addRow($container, require('./map/object/transferRow')(data, lang), columnList);
        }

        var columns = [];

        //var key = options.field.key;

        var rowDialog = require('./defaultContainerRowDialog')(options, lang);

        var $thead = $('<thead>');
        var $tbody = $('<tbody>')
            .on('click', 'tr', function () {
                if(options.fieldOptions.hasOwnProperty("editable") && !options.fieldOptions.editable) {
                    return;
                }
                var $tr = $(this);

                // if reference container without custom onClick
                if (options.fieldOptions.type === 'REFERENCECONTAINER' && !options.field.onClick) {
                    // TODO: visualize new window behaviour. place this in row commands: <span class="glyphicon glyphicon-new-window"></span>

                    // open reference target in new window
                    var ref = options.dataConf.references[options.fieldOptions.reference];
                    var transferRow = $tr.data('transferRow');
                    if (ref.type === 'REVISIONABLE') {
                        window.open(require('./url')('view', {
                            PAGE: ref.target,
                            id: transferRow.value,
                            no: ''
                        }));
                    } else if(ref.type === 'REVISION') {
                        window.open(require('./url')('view', {
                            PAGE: ref.target,
                            id: transferRow.value.split("-")[0],
                            no: transferRow.value.split("-")[1]
                        }));
                    }
                } else {
                    (options.field.onClick || rowDialog('MODIFY', 'ok'))
                    .call(this, $tr.data('transferRow'), function (transferRow) {
                        var $trNew = tr(transferRow);
                        $tr.replaceWith($trNew);
                        if (options.field.onRowChange) {
                            options.field.onRowChange(options, $trNew, transferRow);
                        }
                        $tbody.trigger('rowChanged', [$trNew, columns]);
                    });
                }
            });

        // TODO: Should probably be on container options instead
        this.data('addRowFromDataObject', function(data) {
            addRowFromDataObject($tbody, data, columns);
        });
        this.data('addRow', function(transferRow) {
            return addRow($tbody, transferRow, columns);
        });

        var EMPTY = '-';
        var rowCommands = [];
        // TODO: Add clear container function

        var containerHeader = require('./containerHeader')(options, lang);

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append(containerHeader.create())
            .append(require('./pagination')(options))
            .append($('<table class="table table-condensed table-hover">')
                .me(function () {
                    $thead
                        .append($('<tr>')
                            .append(function () {
                                if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                                    if(options.field.showReferenceValue) {
                                        return [th(MetkaJS.L10N.get('general.referenceValue'))];
                                    }
                                }
                            }).append(function () {
                                if (options.fieldOptions.type === 'REFERENCECONTAINER') {
                                    if(options.field.showReferenceType) {
                                        return [th(MetkaJS.L10N.get('general.referenceType'))];
                                    }
                                }
                            })
                            .append(function () {
                                var response = [];
                                (options.field.columnFields || [])
                                    .forEach(function (fieldKey) {
                                        // if container is not translatable && subfield is translatable, add columns
                                        if (!options.fieldOptions.translatable && getPropertyNS(options, 'dataConf.fields', fieldKey, 'translatable')) {
                                            MetkaJS.Languages.forEach(function (lang) {
                                                columns.push(fieldKey);
                                                response.push((require('./langLabel')(th(getTitle(fieldKey)).data('lang', lang), lang)));
                                            });
                                        } else {
                                            columns.push(fieldKey);
                                            response.push(th(getTitle(fieldKey)));
                                        }
                                    });
                                return response;
                            })
                            .append(function () {
                                if (options.field.showSaveInfo) {
                                    return [th(MetkaJS.L10N.get('general.saveInfo.savedAt')), th(MetkaJS.L10N.get('general.saveInfo.savedBy'))];
                                }
                            })
                            .append(function () {
                                if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                                    if(options.field.showReferenceSaveInfo) {
                                        return [th(MetkaJS.L10N.get('general.refSaveInfo.savedAt')), th(MetkaJS.L10N.get('general.refSaveInfo.savedBy'))];
                                    }
                                }
                            })
                            .append(function () {
                                if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                                    if(options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0) {
                                        var $appInfo = new Array();
                                        $.each(options.field.showReferenceApproveInfo, function(index, lang) {
                                            $appInfo.push((require('./langLabel')(th(MetkaJS.L10N.get('general.refApproveInfo.approvedAt')).data('lang', lang), lang)));
                                            $appInfo.push((require('./langLabel')(th(MetkaJS.L10N.get('general.refApproveInfo.approvedBy')).data('lang', lang), lang)));
                                            $appInfo.push((require('./langLabel')(th(MetkaJS.L10N.get('general.refApproveInfo.approvedRevision')).data('lang', lang), lang)));
                                        });
                                        return $appInfo;
                                    }
                                }
                            })
                            .append(function () {
                                if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                                    if(options.field.showReferenceState) {
                                        return [th(MetkaJS.L10N.get('general.refState'))];
                                    }
                                }
                            })
                            .append(function () {
                                function addMoveButton(direction, sibling, insert) {
                                    addRowCommand($tbody, direction, '<i class="glyphicon glyphicon-arrow-' + direction + '"></i>', function () {
                                        var $tr = $(this).closest('tr');
                                        var $toggleWithTr = $tr[sibling]();
                                        var toggleWithTransferRow = $toggleWithTr.data('transferRow');
                                        var rows = require('./data')(options).getByLang(lang);
                                        if (toggleWithTransferRow) {
                                            var transferRow = $tr.data('transferRow');
                                            var row = rows.indexOf(toggleWithTransferRow);
                                            rows[rows.indexOf(transferRow)] = toggleWithTransferRow;
                                            rows[row] = transferRow;
                                            $toggleWithTr[insert]($tr.detach());
                                        }
                                        return false;
                                    });
                                }
                                if (!options.fieldOptions.fixedOrder && !require('./isFieldDisabled')(options, lang)) {
                                    addMoveButton('up', 'prev', 'before');
                                    addMoveButton('down', 'next', 'after');
                                }

                                if (!options.field.disableRemoval && require('./hasEveryPermission')(options.fieldOptions.removePermissions) && (!require('./isFieldDisabled')(options, lang) || options.field.onRemove)) {
                                    addRowCommand($tbody, 'remove', '<i class="glyphicon glyphicon-remove"></i> ' + MetkaJS.L10N.get('general.buttons.remove'), function () {
                                        var $tr = $(this).closest('tr');
                                        if (options.field.onRemove) {
                                            options.field.onRemove($tr);
                                        } else {
                                            $tr.data('transferRow').removed = true;
                                            $tr.remove();
                                        }
                                        options.$events.trigger('redraw-header-{key}'.supplant({
                                            key: options.field.key
                                        }));
                                        return false;
                                    }, options.field.removeFilter);
                                }
                                if (rowCommands.length) {
                                    return th('');
                                }
                            }));
                    this.append($thead);
                    $thead.toggleClass("containerHidden", !(!options.field.hasOwnProperty('displayHeader') || options.field.displayHeader));
                }).append($tbody))
            .append(function () {
                if(!options.buttons) {
                    options.buttons = [];
                }
                var buttons = options.buttons;
                if (!require('./isFieldDisabled')(options, lang) && !buttons.some(function(button) {
                        if(button.buttonId) {
                            return button.buttonId === options.field.key+"_add";
                        }
                        return false;
                    })) {
                    buttons.push({
                        buttonId: options.field.key+"_add",
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.table.add'))
                                .click(function () {
                                    (options.field.onAdd || rowDialog('ADD', 'add'))
                                        .call(this, {
                                            removed: false,
                                            unapproved: true
                                        }, function (transferRow) {
                                            var $tr = addRow($tbody, transferRow, columns);
                                            if (options.field.onRowChange) {
                                                options.field.onRowChange(options, $tr, transferRow);
                                            }
                                        });
                                });
                        }
                    });
                }

                buttons = buttons.map(function (button) {
                    button.style = 'default';
                    return button;
                }).map(require('./button')(options));

                buttons.forEach(function ($button) {
                    $button.addClass('btn-sm');
                });

                if (buttons.length && !require('./isFieldDisabled')(options, lang)) {
                    return $('<div class="panel-footer clearfix">')
                        .append($('<div class="pull-right">')
                            .append(buttons));
                }
            }));
        var redrawKey = 'redraw-{key}'.supplant({
            key: options.field.key
        });
        var redrawHeaderKey = 'redraw-header-{key}'.supplant({
            key: options.field.key
        });
        options.$events.on(redrawKey, redraw);
        options.$events.on(redrawHeaderKey, redrawHeader);
        options.$events.trigger(redrawKey);
    };
});
