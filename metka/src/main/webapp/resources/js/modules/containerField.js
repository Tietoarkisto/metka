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
                return $('<th>')
                    .text(MetkaJS.L10N.get(prefix + '.' + field));
            };
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

            return $tr
                .data('transferRow', transferRow)
                .append(columns.map(function (column, i) {
                    var $td = $('<td>')
                        .toggleClass('hiddenByTranslationState', $thead.children('tr').children().eq(i).hasClass('hiddenByTranslationState'));

                    return $td.append((function () {
                        if (column === 'rowCommands') {
                            $td.css('text-align', 'right');
                            return $('<button type="button" class="btn btn-default btn-xs">')
                                .append('<span class="glyphicon glyphicon-remove"></span> ' + MetkaJS.L10N.get('general.buttons.remove'));
                        }

                        var type = (function () {
                            if (fieldOptions.type === 'REFERENCECONTAINER') {
                                var target = options.dataConf.references[fieldOptions.reference].target;
                                var field = require('./../metka').dataConfigurations[target].fields[column];
                                return field ? field.type : false;
                            }
                            var type = getPropertyNS(options, 'dataConf.fields', column, 'type');
                            if (type) {
                                return type;
                            }

                            return {
                                savedAt: 'DATE',
                                savedBy: 'STRING'
                            }[column];

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
                    ]
                };

                var $modal = require('./modal')({
                    title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                    body: require('./container').call($('<div>'), containerOptions),
                    buttons: [
                        {
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
                        },
                        {
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.cancel'));
                            }
                        }
                    ]
                });

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
                                var addColumn = field2TableHead('general.saveInfo');
                                return [addColumn('savedAt'), addColumn('savedBy')];
                            }
                        })
                        .append(function () {
                            if (!require('./isFieldDisabled')(options) || options.field.onRemove) {
                                columns.push('rowCommands');
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
                var items = [];
                /*if (key === 'files') {
                    //log(options);

                    var $input = $('<input type="file">');
                    var $form = $('<form>')
                    $form.append($input);

                    $input.fileinput({
                        showPreview: false,
                        showRemove: false,
                        browseLabel: 'Valitse ...',
                        uploadLabel: 'Lataa'
                        //browseLabel: MetkaJS.L10N.get('general.buttons.upload.browse') + ' ...',
                        //uploadLabel: MetkaJS.L10N.get('general.buttons.upload.dataConfiguration')
                    });

                    $form.find('.input-group').addClass('input-group-sm');

                    $form.find('.kv-fileinput-caption').click(function () {
                        $input.click();
                    });

                    $form.find('button[type="submit"]').prop('disabled', true);
                    $input.change(function () {
                        var isSelected = !!$input.val();
                        $form.find('button[type="submit"]').prop('disabled', !isSelected);
                        if (isSelected) {
                            $form.find('.kv-caption-icon').show();
                        } else {
                            $form[0].reset();
                        }
                    });

                    $form.submit(function () {
                        function success(data) {
                            if (data) {
                                data = JSON.parse(data);
                                data.temporary = true;
                                addRow(data);
                            }
                        }

                        success('{"type":"reference","key":"files","value":"4"}');
                        return false;
                        var data = new FormData();
                        data.append('file', $input[0].files[0]);
                        data.append('id', require('./../metka').id);
                        data.append('targetField', key);
                        $.ajax({
                            url: require('./url')('fileUpload'),
                            type: "POST",
                            data: data,
                            processData: false,
                            contentType: false,
                            dataType: 'json',
                            success: success
                        });

                        return false;
                    });

                    items.push($form);
                }*/

                if (!require('./isFieldDisabled')(options)/* && key !== 'files'*/) {
                    items.push(require('./button')(options)({
                        style: 'default'
                    })
                        .addClass('btn-sm')
                        .text(MetkaJS.L10N.get('general.table.add'))
                        .click(function () {
                            rowDialog('add', 'add', addRow)({});
                        }));
                }

                if (items.length) {
                    return $('<div class="panel-footer clearfix">')
                        .append($('<div class="pull-right">')
                            .append(items));
                }
            }));
    };
});
