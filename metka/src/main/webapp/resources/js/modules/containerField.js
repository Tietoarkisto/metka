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

    var getPropertyNS = require('./utils/getPropertyNS');
    var resultParser = require('./resultParser');

    var EMPTY = '-';

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

    function formTR(transferRow, options, $thead, lang) {
        var $tr = $('<tr>');

        tableError.call($tr, transferRow.errors);

        $tr.data('transferRow', transferRow).append(options.columns.map(function(column, i) {
            return mapTransferRowColumn(options, transferRow, column,
                $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'), ($thead.children('tr').children().eq(i).data('lang') || lang));
        }));

        if (options.field.showSaveInfo) {
            $tr.append(
                $('<td>')
                    .text(transferRow.saved ? moment(transferRow.saved.time).format(require('./dateFormats')['DATE']) : EMPTY),
                $('<td>')
                    .text(transferRow.saved ? transferRow.saved.user : EMPTY)
            );
        }

        if (options.rowCommands.length) {
            $tr.append($('<td>')
                    .css('text-align', 'right')
                    .append($('<div class="btn-group btn-group-xs">')
                        .append(options.rowCommands.map(
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
        // Issue #475
        var authorType = $tr.find('td:eq(0)').text();
        if (authorType === 'Organisaatio') {
            $tr.find('td:eq(1)').empty();
        }
        return $tr;
    }

    function formReferenceTR(transferRow, options, $thead, lang) {
        var $tr = $('<tr>');
        var infoTDs = {
            saved: {},
            approved: {}
        };

        tableError.call($tr, transferRow.errors);

        if(options.field.showReferenceValue) {
                infoTDs.value = $('<td>').text(EMPTY);
                $tr.append(infoTDs.value);
            }

            if(options.field.showReferenceType) {
                infoTDs.type = $('<td>').text(EMPTY);
                $tr.append(infoTDs.type);
            }

        var columns = options.columns.map(function(column, i) {
            return mapReferenceTransferRowColumn(options, transferRow, column,
                $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'));
        });

        $tr.data('transferRow', transferRow).append(columns.map(function(column) {
            return column.$td;
        }));

        if (options.field.showSaveInfo) {
            $tr.append(
                $('<td>')
                    .text(transferRow.saved ? moment(transferRow.saved.time).format(require('./dateFormats')['DATE']) : EMPTY),
                $('<td>')
                    .text(transferRow.saved ? transferRow.saved.user : EMPTY)
            );
        }

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

        if (options.rowCommands.length) {
            $tr.append($('<td>')
                    .css('text-align', 'right')
                    .append($('<div class="btn-group btn-group-xs">')
                        .append(options.rowCommands.map(
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

        require('./server')('/references/referenceStatus/{value}', transferRow, {
            method: 'GET',
            success: function (response) {
                transferRow.info = {};
                if(resultParser(response.result).getResult() !== 'REVISION_FOUND') {
                    require('./resultViewer')(response.result);
                    return;
                }
                if(response.type) {
                    transferRow.info.type = response.type;
                    if(infoTDs.type) {
                        infoTDs.type.text(MetkaJS.L10N.get('type.'+response.type+".title"))
                    }
                }
                if(response.saved) {
                    transferRow.info.saved = response.saved;
                    if(response.saved.time && infoTDs.saved.at) {
                        infoTDs.saved.at.text(moment(response.saved.time).format(require('./dateFormats')['DATE']))
                    }
                    if(response.saved.user && infoTDs.saved.by) {
                        infoTDs.saved.by.text(response.saved.user);
                    }
                }
                if(response.approved) {
                    transferRow.info.approved = response.approved;
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

                if(response.state) {
                    transferRow.info.state = response.state;
                    if(infoTDs.state) {
                        infoTDs.state.text(MetkaJS.L10N.get('state.'+response.state));
                    }
                }

                columns.map(function(column) {
                    fillReferenceTransferRowColumn(options, transferRow, column);
                })
            }
        });

        return $tr;
    }

    function mapTransferRowColumn(options, transferRow, column, hidden, columnLang) {
        var $td = $('<td>').toggleClass('hiddenByTranslationState', hidden);
        if(options.fieldOptions.type === 'CONTAINER') {
            // This fetches the correct display text for each table cell. It would make sense to split and generalize this better but the amount of work makes it
            // not a priority at the moment.
            formTD(options, column, $td, columnLang, transferRow, getPropertyNS(transferRow, 'fields', column));
        } else {
            log('Tried to handle TransferRow from unsupported container type: '+options.fieldOptions.type);
        }
        return $td;
    }

    function mapReferenceTransferRowColumn(options, transferRow, column, hidden) {
        if (options.fieldOptions.type === 'REFERENCECONTAINER') {
            var columnHolder = {};
            columnHolder.key = column;
            columnHolder.$td = $('<td>').toggleClass('hiddenByTranslationState', hidden);
            columnHolder.$td.text(EMPTY);
            return columnHolder;
        } else {
            log('Tried to handle Reference TransferRow from unsupported container type: '+options.fieldOptions.type);
        }
        return;
    }

    function fillReferenceTransferRowColumn(options, transferRow, column) {
        if (options.fieldOptions.type === 'REFERENCECONTAINER') {
            require('./reference').optionByPath(column.key, options, options.defaultLang, function(option) {
                var columnOptions = $.extend(
                    true
                    , {}
                    , getPropertyNS(options, 'dataConf.fields', column.key)
                    , options.subfieldConfiguration && options.subfieldConfiguration[column.key] ? options.subfieldConfiguration[column.key].field : {});

                if(columnOptions && columnOptions.displayType === 'LINK') {
                    require('./inherit')(function(options) {
                        require('./linkField')(column.$td, options, "DEFAULT");
                    })(options)({
                        fieldOptions: getPropertyNS(options, 'dataConf.fields', column.key),
                        field: columnOptions,
                        data: $.extend({}, options.data, {fields: transferRow.fields})
                    });
                } else {
                    var value = require('./selectInputOptionText')(option);
                    column.$td.text(!!value && value.length ? value : EMPTY);
                }
            })(null, null, transferRow.value, transferRow);
        } else {
            log('Tried to handle Reference TransferRow from unsupported container type: '+options.fieldOptions.type);
        }
    }

    function formTD(options, column, $td, columnLang, transferRow, transferField) {
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

        if(type === 'CUSTOM_JS') {
            type = columnOptions.type;
        }

        if (!type) {
            log('not implemented', column);
            $td.text(EMPTY);
            return;
        }

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

                require('./reference').optionByPath(column, options, columnLang, setText)(transferRow.fields, reference, null, transferRow);
                return;
            }
            case 'SELECTION': {
                var list = require('./selectionList')(options, column);
                if (!list) {
                    $td.text(EMPTY);
                    break;
                }
                if (list.type === 'REFERENCE') {
                    require('./reference').optionsByPath(column, options, columnLang, setOptionText)(transferRow.fields, getPropertyNS(options, 'dataConf.references', list.reference), null, transferRow);
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
    }

    function appendRow(options, $container, transferRow, $thead, lang) {
        function append() {
            var $tr;
            if(options.fieldOptions.type === 'REFERENCECONTAINER') {
                $tr = formReferenceTR(transferRow, options, $thead, lang);
            } else {
                $tr = formTR(transferRow, options, $thead, lang);
                // Issue #475
                var authorType = $tr.find('td:eq(0)').text();
                if (authorType === 'Organisaatio') {
                    $tr.find('td:eq(1)').empty();
                }
            }
            $container.append($tr);
            $container.trigger('rowAppended', [$tr, options.columns]);
            return $tr;
        }

        if (!transferRow.removed) {
            return append();
        }
    }

    function redraw($tbody, options, lang, $thead) {
        $tbody.empty();
        options.$events.trigger('redraw-header-{key}'.supplant({
            key: options.field.key
        }));

        if(options.onRedraw && !options.onRedraw($tbody, options, lang, $thead)) {
            // This should differ hugely from the onRedraw function since recursive calls to redraw don't really work with pagination
            return;
        }

        if(options.field.rowsPerPage) {
            var page = $tbody.data('currentPage');
            var redrawPaging = 'redraw-{key}-paging'.supplant({
                key: options.field.key
            });
            options.$events.trigger(redrawPaging, [options.field.rowsPerPage, require('./data')(options).validRows(lang), page]);
            redrawPage(page, $tbody, options, lang, $thead);
            return $tbody;
        }

        var rows = require('./data')(options).getByLang(lang);
        if(rows) {
            rows.forEach(function (transferRow) {
                appendRow(options, $tbody, transferRow, $thead, lang)
            });
        }

        return $tbody;
    }

    function redrawPage(page, $tbody, options, lang, $thead) {
        $tbody.empty();
        if(!options.field.rowsPerPage || options.field.rowsPerPage < 1) {
            // Sanity check, if rowsPerPage is not defined then fall back on normal redraw instead
            redraw($tbody, options, lang, $thead);
            return;
        }

        if(options.onRedrawPage && !options.onRedrawPage(page, $tbody, options, lang, $thead)) {
            // This should differ hugely from the onRedraw function since recursive calls to redraw don't really work with pagination
            return;
        }

        var rows = require('./data')(options).getByLang(lang);
        if (rows) {
            var added = 0;
            var processed = 0;
            var first = ((page - 1) * options.field.rowsPerPage);
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if(!row.removed) {
                    if(processed++ >= first && added < options.field.rowsPerPage) {
                        appendRow(options, $tbody, row, $thead, lang);
                        added++;
                    }
                    if(added >= options.field.rowsPerPage) {
                        break;
                    }
                }
            }
        }
        return $tbody;
    }

    /**
     * Creates table (container field or reference container) field.
     *
     * @this {jQuery} Language specific element.
     * @param {object} options UI configuration of this field, with properties for accessing data, configurations etc.
     * @param {object} lang. Language of this field.
     * @return {undefined} No return value.
     */
    return function (options, lang) {
        function addRowCommand($container, className, html, click, rowFilter) {
            options.rowCommands.push({
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

        function addRow($container, transferRow) {
            require('./data')(options).appendByLang(lang, transferRow);

            //if paging is enabled and we add a new row just redraw current container page
            if(options.field.rowsPerPage) {
                var currentPage = $tbody.data("currentPage");
                var redrawPaging = 'redraw-{key}-paging'.supplant({
                    key: options.field.key
                });
                var validRows = require('./data')(options).validRows(lang)
                options.$events.trigger(redrawPaging, [options.field.rowsPerPage, validRows, currentPage]);
                if(currentPage == Math.ceil(validRows / options.field.rowsPerPage)) {
                    appendRow(options, $container, transferRow, $thead, lang);
                }
                containerHeader.redraw();
                /*var redrawKey = 'redraw-{key}'.supplant({
                    key: options.field.key
                });
                options.$events.trigger(redrawKey);*/
            } else {
                //otherwise we can just append new row without redrawing whole page
                appendRow(options, $container, transferRow, $thead, lang);
                //redraw header because if showRowAmount is enabled we need to update that
                containerHeader.redraw();
            }

            options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                key: transferRow.key,
                lang: lang
            }));
        }

        function addRowFromDataObject($container, data) {
            addRow($container, require('./map/object/transferRow')(data, lang));
        }

        options.columns = [];
        options.rowCommands = [];

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
                        var $trNew = formTR(transferRow, options, $thead, lang);
                        $tr.replaceWith($trNew);
                        options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                            key: transferRow.key,
                            lang: lang
                        }));
                    });
                }
            });

        // TODO: Should probably be on container options instead
        this.data('addRowFromDataObject', function(data) {
            addRowFromDataObject($tbody, data);
        });
        options.$events.off('container-{key}-{lang}-push'.supplant({
            key: options.field.key,
            lang: lang
        }));
        options.$events.on('container-{key}-{lang}-push'.supplant({
            key: options.field.key,
            lang: lang
        }), function(event, transferRow) {
            return addRow($tbody, transferRow);
        });
        this.data('lang', lang);

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
                                                options.columns.push(fieldKey);
                                                response.push((require('./langLabel')(th(getTitle(fieldKey)).data('lang', lang), lang)));
                                            });
                                        } else {
                                            options.columns.push(fieldKey);
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
                                        var $appInfo = [];
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
                                        options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                                            key: options.field.key,
                                            lang: lang
                                        }));
                                        if(options.field.rowsPerPage) {
                                            options.$events.trigger('redraw-{key}'.supplant({key: options.field.key}));
                                        } else {
                                            options.$events.trigger('redraw-header-{key}'.supplant({
                                                key: options.field.key
                                            }));
                                        }
                                        return false;
                                    }, options.field.removeFilter);
                                }
                                if (options.rowCommands.length) {
                                    return th('');
                                }
                            }));
                    this.append($thead);
                    $thead.toggleClass("containerHidden", !(!options.field.hasOwnProperty('displayHeader') || options.field.displayHeader));
                }).append($tbody))
            .append(function () {
                var buttons = (options.buttons || []).map(function(button) {
                        if(!(!!button.buttonId && button.buttonId === options.field.key+"_add")) {
                            return button;
                        }
                    });

                if (!require('./isFieldDisabled')(options, lang)) {
                    buttons.push({
                        buttonId: options.field.key+"_add",
                        title: MetkaJS.L10N.get('general.table.add'),
                        onClick: function () {
                            (options.field.onAdd || rowDialog('ADD', 'add'))
                                .call(this, {
                                    removed: false,
                                    unapproved: true
                                }, function (transferRow) {
                                    addRow($tbody, transferRow);
                                });
                        }
                    });
                }

                if (buttons.length && !require('./isFieldDisabled')(options, lang)) {
                    return $('<div class="panel-footer clearfix">')
                        .append($('<div class="pull-right">')
                            .append(buttons.map(function (button) {
                                var $button = {
                                    style: 'default',
                                    lang: lang,
                                    title: button.title,
                                    create: function (options) {
                                        this
                                            //.text(button.title)
                                            .click(function () {
                                                button.onClick(options)
                                            });
                                    }
                                };
                                $button = require('./button')(options)($button);
                                $button.addClass('btn-sm');
                                return $button;
                            })));
                }
            }));
        var redrawKey = 'redraw-{key}'.supplant({
            key: options.field.key
        });
        var redrawPageKey = 'redraw-{key}-page'.supplant({
            key: options.field.key
        });
        var redrawHeaderKey = 'redraw-header-{key}'.supplant({
            key: options.field.key
        });

        options.$events.register(redrawKey, function(event, page) {
            if(options.field.rowsPerPage) {
                //if there is "paging-info" on $tbody.data and new page to draw is not provided
                //we get current page from .data and just redraw that
                //otherwise we can just go to page 1
                var currentPage = page;
                if (!page && $tbody.data("currentPage") != null) {
                    currentPage = $tbody.data("currentPage");
                } else if (!page) {
                    currentPage = 1;
                }
                $tbody.data("currentPage", currentPage);
            }

            redraw($tbody, options, lang, $thead);
        });
        options.$events.register(redrawHeaderKey, function(){
            containerHeader.redraw();
        });
        options.$events.register(redrawPageKey, function(event, page) {
            if(options.field.rowsPerPage) {
                //if there is "paging-info" on $tbody.data and new page to draw is not provided
                //we get current page from .data and just redraw that
                //otherwise we can just go to page 1
                var currentPage = page;
                if (!page && $tbody.data("currentPage") != null) {
                    currentPage = $tbody.data("currentPage");
                } else if (!page) {
                    currentPage = 1;
                }
                $tbody.data("currentPage", currentPage);
                redrawPage(currentPage, $tbody, options, lang, $thead);
            } else {
                options.events.trigger(redrawKey);
            }
        });
        options.$events.trigger(redrawKey);
    };
});
