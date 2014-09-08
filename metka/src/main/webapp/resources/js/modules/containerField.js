define(function (require) {
    'use strict';

    return function (options, lang) {
        var columns = [];

        var PAGE = require('./../metka').PAGE;
        var getPropertyNS = require('./utils/getPropertyNS');
        var key = options.field.key;
        var fieldOptions = getPropertyNS(options, 'dataConf.fields', key) || {};
        var $thead = $('<thead>');
        var $tbody = $('<tbody>');

        function field2TableHead(prefix) {
            return function (field) {
                columns.push(field);
                return th(prefix, field);
            };
        }

        function th(prefix, field) {
            return $('<th>')
                .text(MetkaJS.L10N.get(prefix + '.' + field));
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

                    return $td.append((function () {
                        var type = (function () {
                            var metka = require('./../metka');
                            if (fieldOptions.type === 'REFERENCECONTAINER') {
                                var $span = $('<span>');
                                require('./server')('options', {
                                    data: JSON.stringify({
                                        key: key,
                                        requests : [{
                                            key: column,
                                            container: key,
                                            confType: options.dataConf.key.type,
                                            confVersion: options.dataConf.key.version,
                                            dependencyValue: transferRow.value
                                        }]
                                    }),
                                    success: function (data) {
                                        log(data);
                                    }
                                });
                                return $span;
                                var target = options.dataConf.references[fieldOptions.reference].target;
                                var field = metka.dataConfigurations[target].fields[column];
                                return field ? field.type : false;
                            }
                            var type = getPropertyNS(options, 'dataConf.fields', column, 'type');
                            if (type) {
                                return type;
                            }
                        })();

                        if (!type) {
                            log('not implemented', column);
                            return '-';
                        }

                        var transferField = getPropertyNS(transferRow, 'fields', column);
                        var value = (function () {
                            if (!transferField) {
                                log('transferField not set (column: {column})'.supplant({
                                    column: column
                                }));
                                return;
                            }

                            tableError.call($td, (transferField.errors || []).concat(transferField[lang] ? transferField[lang].errors : []));

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

                            var columnLang = $thead.children('tr').children().eq(i).data('lang') || lang;
                            if (columnLang) {
                                return getPropertyNS(transferField, 'values', columnLang, 'current');
                            } else {
                                return getPropertyNS(transferField, 'value.current');
                            }
                        })();

                        if (type === 'STRING' || type === 'INTEGER') {
                            return value || '-';
                        }
                        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                            if (value) {
                                return moment(value).format(require('./dateFormats')[type]);
                            }
                            return '-';
                        }
                        if (type === 'SELECTION') {
                            if (typeof value !== 'undefined') {
                                var text;
                                if (getPropertyNS(options, 'dataConf.selectionLists', options.dataConf.fields[column].selectionList, 'options').some(function (option) {
                                    if (option.value === value) {
                                        text = require('./selectInput').optionText(option);
                                        return true;
                                    }
                                })) {
                                    return text;
                                }

                                log('missing translation', key, value);
                                return '-';
                            }
                            return '-';
                        }
                        log('not implemented', column, type);
                        return '-';
                    })());
                }));

            if (options.field.showSaveInfo) {
                $tr.append(
                    $('<td>')
                        .text(moment(transferRow.saved.time).format(require('./dateFormats')['DATE'])),
                    $('<td>')
                        .text(transferRow.saved.user));
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
            if (!transferRow.removed) {
                var $tr = tr(transferRow);
                $tbody.append($tr);
                $tbody.trigger('rowAppended', [$tr, columns]);
                return $tr;
            }
        }

        function addRow(transferRow) {
            if (lang) {
                require('./data')(options).appendByLang(lang, transferRow);
            } else {
                require('./data')(options).append(transferRow);
            }
            return appendRow(transferRow);
        }

        function addRowFromDataObject(data) {
            addRow(require('./map/object/transferRow')(data, lang));
        }

        function rowDialog(title, button, onClose) {
            return function (transferRow) {
                // copy data, so if dialog is dismissed, original data won't change
                transferRow = $.extend(true, {}, transferRow);

                var containerOptions = {
                    title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                    data: transferRow,
                    dataConf: options.dataConf,
                    $events: $({}),
                    defaultLang: fieldOptions.translatable ? lang : options.defaultLang,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: options.dataConf.fields[key].subfields.map(function (field) {
                                var dataConfig = $.extend(true, {}, options.dataConf.fields[field]);
                                if (fieldOptions.translatable) {
                                    dataConfig.translatable = false;
                                }

                                return {
                                    type: 'ROW',
                                    cells: [$.extend({}, dataConfig, {
                                        type: 'CELL',
                                        title: MetkaJS.L10N.get(PAGE + '.field.' + field),
                                        field: dataConfig
                                    })]
                                };
                            })
                        }
                    ],
                    buttons: [{
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.buttons.' + button))
                                .click(function () {
                                    var $tr = onClose(transferRow);
                                    if (options.field.onRowChange) {
                                        options.field.onRowChange($tr, transferRow);
                                    }
                                });
                        }
                    }, {
                        type: 'CANCEL'
                    }]
                };

                var $modal = require('./modal')(containerOptions);

                // if not translatable container and has translatable subfields, show language selector
                if (!fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    $modal.find('.modal-header').append(require('./languageRadioInputGroup')(containerOptions, 'dialog-translation-lang', $('input[name="translation-lang"]:checked').val()));
                }
            };
        }

        this.data('addRowFromDataObject', addRowFromDataObject);

        var $panelHeading = $('<div class="panel-heading">')
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (fieldOptions.translatable) {
            require('./langLabel')($panelHeading, lang);
        }

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append($panelHeading)
            .append($('<table class="table table-condensed">')
                .if(options.field.onClick || !require('./isFieldDisabled')(options), function () {
                    this
                        .addClass('table-hover');

                    $tbody
                        .on('click', 'tr', function () {
                            var $tr = $(this);
                            var onClick = options.field.onClick || rowDialog('modify', 'ok', function (transferRow) {
                                //this.data('transferRow').fields = transferRow.fields;
                                return $tr.replaceWith(tr(transferRow));
                            });

                            onClick.call(this, $tr.data('transferRow'));
                        });
                })
                .append($thead
                    .append($('<tr>')
                        .append(function () {
                            if (/*options.field.showReferenceKey*/getPropertyNS(options, 'dataConf.fields', key, 'showReferenceKey')) {
                                var target = options.dataConf.references[options.dataConf.fields[key].reference].target;

/*
                                require('./server')('/revision/ajax/view/{page}/{id}/{no}', {
                                    page: target.toLocaleLowerCase()
                                }, {
                                    method: 'GET',
                                    success: function (data) {
                                        log('data', data);
                                    }
                                });*/


                                //return field2TableHead(target + '.field')(require('./../metka').dataConfigurations[target].idField);
                            }
                        })
                        .append(function () {
                            var response = [];
                            var th = field2TableHead(PAGE + '.field');
                            (getPropertyNS(options, 'dataConf.fields', key, 'subfields') || [])
                                .filter(function (field) {
                                    // ui only shows summary fields
                                    return !!options.dataConf.fields[field].summaryField;
                                })
                                .forEach(function (field) {
                                    // if container is not translatable && subfield is translatable, add columns
                                    if (!fieldOptions.translatable && options.dataConf.fields[field].translatable) {
                                        ['DEFAULT', 'EN', 'SV'].forEach(function (lang) {
                                            response.push(require('./langLabel')(th(field).data('lang', lang), lang));
                                        });
                                    } else {
                                        response.push(th(field));
                                    }
                                });
                            return response;
                        })
                        .append((options.field.columnFields || [])
                            .map(field2TableHead(PAGE + '.field')))
                        .append(function () {
                            if (options.field.showSaveInfo) {
                                return [th('general.saveInfo', 'savedAt'), th('general.saveInfo', 'savedBy')];
                            }
                        })
                        .append(function () {
                            if (!require('./isFieldDisabled')(options) || options.field.onRemove) {
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
                        })))
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
                if (!require('./isFieldDisabled')(options)) {

                    buttons.push({
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.table.add'))
                                .click(function () {
                                    rowDialog('add', 'add', addRow)({});
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
