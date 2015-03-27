define(function (require) {
    'use strict';

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

        function addRowCommand($container, className, html, click) {
            rowCommands.push({
                className: className,
                html: html
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

            tableError.call($tr, transferRow.errors);

            $tr
                .data('transferRow', transferRow)
                .append(columns.map(function (column, i) {
                    var $td = $('<td>')
                        .toggleClass('hiddenByTranslationState', $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'));

                    if (options.fieldOptions.type === 'REFERENCECONTAINER') {
                        var fieldValues = {};
                        fieldValues[key] = transferRow.value;
                        require('./server')('options', {
                            data: JSON.stringify({
                                key: key,
                                requests: [{
                                    key: column,
                                    container: key,
                                    confType: options.dataConf.key.type,
                                    confVersion: options.dataConf.key.version,
                                    language: lang,
                                    fieldValues: fieldValues
                                }]
                            }),
                            success: function (data) {
                                $td.text(data.responses.length && data.responses[0].options.length ? data.responses[0].options[0].title.value : EMPTY);
                            }
                        });
                        return $td;
                    }
                    return $td.append((function () {
                        var columnLang = $thead.children('tr').children().eq(i).data('lang') || lang;

                        function setText(text) {
                            $td.append(typeof text === 'undefined' ? EMPTY : text);
                        }
                        function setOptionText(listOptions) {
                            setText(require('./selectInputOptionText')(listOptions.find(function (option) {
                                return option.value === value;
                            })));
                        }
                        var dataConf = getPropertyNS(options, 'dataConf.fields', column);
                        var type = getPropertyNS(options, 'dataConf.fields', column, 'type');

                        if (!type) {
                            log('not implemented', column);
                            return EMPTY;
                        }

                        if (type === 'REFERENCE') {
                            var refKey = getPropertyNS(options, 'dataConf.fields', column, 'reference');
                            var reference = getPropertyNS(options, 'dataConf.references', refKey);

                            require('./reference').optionByPath(column, options, columnLang, setText)(transferRow.fields, reference);
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

                        if (type === 'STRING' || type === 'INTEGER' || type === 'REAL' || type === 'RICHTEXT') {
                            return value || EMPTY;
                        }
                        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                            if (value) {
                                return moment(value).format(require('./dateFormats')[type]);
                            }
                            return EMPTY;
                        }
                        if (type === 'SELECTION') {
                            var list = require('./selectionList')(options, column);
                            if (!list) {
                                $td.append(EMPTY);
                                return;
                            }
                            if (list.type === 'REFERENCE') {
                                require('./reference').optionsByPath(column, options, lang, setOptionText)(transferRow.fields, getPropertyNS(options, 'dataConf.references', list.reference));
                            } else {
                                setOptionText(list.options);
                            }
                            return;
                        }
                        log('not implemented', column, type);
                        return EMPTY;
                    })());
                }));

            if (options.field.showSaveInfo) {
                $tr.append(
                    $('<td>')
                        .text(transferRow.saved ? moment(transferRow.saved.time).format(require('./dateFormats')['DATE']) : EMPTY),
                    $('<td>')
                        .text(transferRow.saved ? transferRow.saved.user : EMPTY));
            }

            if (rowCommands.length) {
                $tr.append($('<td>')
                    .css('text-align', 'right')
                    .append($('<div class="btn-group btn-group-xs">')
                    .append(rowCommands.map(function (button) {
                        return $('<button type="button" class="btn btn-default">')
                            .addClass(button.className)
                            .html(button.html);
                    }))));
            }

            return $tr;
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
            return appendRow($container, transferRow, columnList);
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
                var $tr = $(this);

                // if reference container without custom onClick
                if (options.fieldOptions.type === 'REFERENCECONTAINER' && !options.field.onClick) {
                    // TODO: visualize new window behaviour. place this in row commands: <span class="glyphicon glyphicon-new-window"></span>

                    // open reference target in new window
                    var ref = options.dataConf.references[options.fieldOptions.reference];
                    if (ref.type === 'REVISIONABLE') {
                        window.open(require('./url')('view', {
                            PAGE: ref.target,
                            id: $tr.data('transferRow').value,
                            no: ''
                        }));
                    }
                } else {
                    (options.field.onClick || rowDialog('MODIFY', 'ok'))
                    .call(this, $tr.data('transferRow'), function (transferRow) {
                        //return $tr.replaceWith(tr(transferRow));
                        var $trNew = tr(transferRow);
                        $tr.replaceWith($trNew);
                        if (options.field.onRowChange) {
                            log("first", transferRow);
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

        var $panelHeading = $('<div class="panel-heading">')
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (options.fieldOptions.translatable) {
            require('./langLabel')($panelHeading, lang);
        }

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append($panelHeading)
            .append($('<table class="table table-condensed table-hover">')
                .me(function () {
                    $thead
                        .append($('<tr>')
                            .append(function () {
                                var response = [];
                                (options.field.columnFields || [])
                                    .forEach(function (fieldKey) {
                                        // if container is not translatable && subfield is translatable, add columns
                                        if (!options.fieldOptions.translatable && getPropertyNS(options, 'dataConf.fields', fieldKey, 'translatable')) {
                                            ['DEFAULT', 'EN', 'SV'].forEach(function (lang) {
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

                                if (require('./hasEveryPermission')(options.fieldOptions.removePermissions) && (!require('./isFieldDisabled')(options, lang) || options.field.onRemove)) {
                                    addRowCommand($tbody, 'remove', '<i class="glyphicon glyphicon-remove"></i> ' + MetkaJS.L10N.get('general.buttons.remove'), function () {
                                        var $tr = $(this).closest('tr');
                                        if (options.field.onRemove) {
                                            options.field.onRemove($tr);
                                        } else {
                                            $tr.data('transferRow').removed = true;
                                            $tr.remove();
                                        }
                                        return false;
                                    });
                                }
                                if (rowCommands.length) {
                                    return th('');
                                }
                            }));
                    if (!options.field.hasOwnProperty('displayHeader') || options.field.displayHeader) {
                        this.append($thead);
                    }
                })
                .append(function () {
                    require('./data')(options).onChange(function () {
                        $tbody.empty();
                        var rows = require('./data')(options).getByLang(lang);
                        if (rows) {
                            rows.forEach(function(transferRow) {
                                appendRow($tbody, transferRow, columns)
                            });
                        }
                    });
                    return $tbody;
                }))
            .append(function () {
                var buttons = (options.buttons || []);
                if (options.field.onAdd || !require('./isFieldDisabled')(options, lang)) {
                    buttons.push({
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.table.add'))
                                .click(function () {
                                    (options.field.onAdd || rowDialog('ADD', 'add'))
                                        .call(this, {}, function (transferRow) {
                                            log('pre-second', transferRow);
                                            var $tr = addRow($tbody, transferRow, columns);
                                            if (options.field.onRowChange) {
                                                log("second", transferRow);
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

                if (buttons.length) {
                    return $('<div class="panel-footer clearfix">')
                        .append($('<div class="pull-right">')
                            .append(buttons));
                }
            }));
    };
});
