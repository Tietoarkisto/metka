define(function (require) {
    'use strict';

    return function (options, lang) {
        var columns = [];

        var getPropertyNS = require('./utils/getPropertyNS');
        var key = options.field.key;
        var fieldOptions = getPropertyNS(options, 'dataConf.fields', key) || {};
        var $thead = $('<thead>');
        var $tbody = $('<tbody>');
        var EMPTY = '-';

        function fieldTitle(field) {
            return 'fieldTitles.{field}.title'.supplant({
                field: field
            });
        }

        function th(key) {
            return $('<th>')
                .text(MetkaJS.L10N.get(key));
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
            var $tr = $('<tr>');

            tableError.call($tr, transferRow.errors);

            $tr
                .data('transferRow', transferRow)
                .append(columns.map(function (column, i) {
                    var $td = $('<td>')
                        .toggleClass('hiddenByTranslationState', $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'));

                    if (fieldOptions.type === 'REFERENCECONTAINER') {
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

                            require('./reference').option(column, options, columnLang, setText)(transferRow.fields, reference);
                            return;
                        }

                        var transferField = getPropertyNS(transferRow, 'fields', column);
                        var value = (function () {
                            if (!transferField) {
                                return;
                            }

                            tableError.call($td, (transferField.errors || []).concat(transferField[columnLang] ? transferField[columnLang].errors : []));

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

                        if (type === 'STRING' || type === 'INTEGER' || type === 'REAL') {
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
                                require('./reference').options(column, options, lang, setOptionText)(transferRow.fields, {
                                    target: column
                                });
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

            if (options.field.hasRowCommands) {
                $tr.append($('<td>')
                    .css('text-align', 'right')
                    .html($('<button type="button" class="btn btn-default btn-xs">')
                        .append('<span class="glyphicon glyphicon-remove"></span> ' + MetkaJS.L10N.get('general.buttons.remove'))));
            }

            return $tr;
        }

        function appendRow(transferRow) {
            function append() {
                var $tr = tr(transferRow);
                $tbody.append($tr);
                $tbody.trigger('rowAppended', [$tr, columns]);
                return $tr;
            }

            if (!transferRow.removed) {
                return append();
            }
        }

        function addRow(transferRow) {
            require('./data')(options).appendByLang(lang, transferRow);
            return appendRow(transferRow);
        }

        function addRowFromDataObject(data) {
            addRow(require('./map/object/transferRow')(data, lang));
        }

        // list field keys, which are configured as freeTextKeys
        var subfields = fieldOptions.subfields || [];
        var freeTextKeys = [];
        if (options.dataConf && options.dataConf.selectionLists) {
            $.each(options.dataConf.selectionLists, function (key, list) {
                if (list.freeTextKey) {
                    freeTextKeys.push(list.freeTextKey);
                }
            });
        }

        if (fieldOptions.type === 'REFERENCECONTAINER' && key !== 'files') {
            // FIXME: Merge shared code with containerRowDialog.
            var refDialog = function (options, lang, key) {
                var PAGE = require('./../metka').PAGE;
                var fieldOptions = getPropertyNS(options, 'dataConf.fields', key) || {};
                return function (title, button) {
                    return function (transferRow, onClose) {
                        // copy data, so if dialog is dismissed, original data won't change
                        var transferRowCopy = $.extend(true, {}, transferRow);

                        var containerOptions = {
                            title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                            data: transferRowCopy,
                            dataConf: options.dataConf,
                            $events: $({}),
                            defaultLang: fieldOptions.translatable ? lang : options.defaultLang,
                            content: [
                                {
                                    type: 'COLUMN',
                                    columns: 1,
                                    rows: (function () {
                                        var field = key;
                                        var dataConfig = {
                                            "key": key,
                                            "translatable": false,
                                            "type": "SELECTION",
                                            "selectionList": 'referenceContainerRowDialog_list'
                                        };
                                        return [{
                                            type: 'ROW',
                                            cells: [$.extend({}, dataConfig, {
                                                type: 'CELL',
                                                title: MetkaJS.L10N.get(fieldTitle(field)),
                                                field: dataConfig
                                            })]
                                        }];
                                    })()
                                }
                            ],
                            buttons: [
                                {
                                    create: function () {
                                        this
                                            .text(MetkaJS.L10N.get('general.buttons.' + button))
                                            .click(function () {
                                                transferRow.value = transferRowCopy.fields[key].values[lang].current;
                                                onClose(transferRow);
                                            });
                                    }
                                },
                                {
                                    type: 'CANCEL'
                                }
                            ]
                        };

                        // if not translatable container and has translatable subfields, show language selector
                        if (!fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                            containerOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || options.defaultLang;
                        }

                        var $modal = require('./modal')(containerOptions);

                    };
                }
            };
            var fields = {};
            fields[key] = {
                "key": key,
                "translatable": false,
                "type": "SELECTION",
                "selectionList": "referenceContainerRowDialog_list"
            };
            var references = {};
            references[fieldOptions.reference] = options.dataConf.references[fieldOptions.reference];
            var rowDialog = refDialog({
                defaultLang: options.defaultLang,
                dataConf: {
                    key: options.dataConf.key,
                    selectionLists: {
                        referenceContainerRowDialog_list: {
                            "type": "REFERENCE",
                            "reference": fieldOptions.reference
                        }
                    },
                    references: references,
                    fields: fields
                },
                field: {
                    key: key
                }
            }, lang, key);
        } else {
            var rowDialog = require('./containerRowDialog')(options, lang, key, function () {
                return subfields.filter(function (field) {
                    // filter free text fields
                    return freeTextKeys.indexOf(field) === -1;
                }).map(function (field) {
                    var dataConfig = $.extend(true, {}, options.dataConf.fields[field]);

                    return {
                        type: 'ROW',
                        cells: [$.extend(
                            true,
                            {},
                            options.extraDialogConfiguration ? options.extraDialogConfiguration[field] : {},
                            {
                                type: 'CELL',
                                translatable: fieldOptions.translatable ? false : dataConfig.translatable,
                                title: MetkaJS.L10N.get(fieldTitle(field)),
                                field: {
                                    key: field
                                }
                            }
                        )]
                    };
                });
            });
        }

        this.data('addRowFromDataObject', addRowFromDataObject);
        this.data('addRow', addRow);

        var $panelHeading = $('<div class="panel-heading">')
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (fieldOptions.translatable) {
            require('./langLabel')($panelHeading, lang);
        }

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append($panelHeading)
            .append($('<table class="table table-condensed">')
                .if(options.field.onClick || !require('./isFieldDisabled')(options, lang), function () {
                    this
                        .addClass('table-hover');

                    $tbody
                        .on('click', 'tr', function () {
                            var $tr = $(this);
                            (options.field.onClick || rowDialog('modify', 'ok'))
                                .call(this, $tr.data('transferRow'), function (transferRow) {
                                    //return $tr.replaceWith(tr(transferRow));
                                    var $tr2 = $tr.replaceWith(tr(transferRow));
                                    if (options.field.onRowChange) {
                                        options.field.onRowChange($tr2, transferRow);
                                    }
                                });
                        });
                })
                .me(function () {
                    $thead
                        .append($('<tr>')
                            /*.append(function () {
                             if (options.field.showReferenceKey ??? getPropertyNS(options, 'dataConf.fields', key, 'showReferenceKey')) {
                             var target = options.dataConf.references[options.dataConf.fields[key].reference].target;
                             }
                             })*/
                            .append(function () {
                                var response = [];
                                (options.field.columnFields || [])
                                    .forEach(function (field) {
                                        // if container is not translatable && subfield is translatable, add columns
                                        if (!fieldOptions.translatable && getPropertyNS(options, 'dataConf.fields', field, 'translatable')) {
                                            ['DEFAULT', 'EN', 'SV'].forEach(function (lang) {
                                                columns.push(field);
                                                response.push((require('./langLabel')(th(fieldTitle(field)).data('lang', lang), lang)));
                                            });
                                        } else {
                                            columns.push(field);
                                            response.push(th(fieldTitle(field)));
                                        }
                                    });
                                return response;
                            })
                            .append(function () {
                                if (options.field.showSaveInfo) {
                                    return [th('general.saveInfo.savedAt'), th('general.saveInfo.savedBy')];
                                }
                            })
                            .append(function () {
                                if (!require('./isFieldDisabled')(options, lang) || options.field.onRemove) {
                                    options.field.hasRowCommands = true;
                                    $tbody
                                        .on('click', 'tr button', function () {
                                            var $tr = $(this).closest('tr');
                                            if (options.field.onRemove) {
                                                options.field.onRemove($tr);
                                            } else {
                                                $tr.data('transferRow').removed = true;
                                                $tr.remove();
                                            }
                                            return false;
                                        });
                                    return $('<th>');
                                }
                            }));
                    if (!options.field.hasOwnProperty('displayHeader') || options.field.displayHeader) {
                        this.append($thead);
                    }
                })
                .append(function () {
                    require('./data')(options).onChange(function () {
                        $tbody.empty();
                        var rows = (function () {
                            if (lang) {
                                return require('./data')(options).getByLang(lang);
                            } else {
                                return require('./data')(options).get();
                            }
                        })();
                        if (rows) {
                            rows.forEach(appendRow);
                        }
                    });
                    return $tbody;
                }))
            .append(function () {
                var buttons = (options.buttons || []);
                if (!require('./isFieldDisabled')(options, lang)) {
                    buttons.push({
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.table.add'))
                                .click(function () {
                                    (options.field.onAdd || rowDialog('add', 'add'))
                                        .call(this, {}, function (transferRow) {
                                            var $tr = addRow(transferRow);
                                            if (options.field.onRowChange) {
                                                options.field.onRowChange($tr, transferRow);
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
