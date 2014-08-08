define(function (require) {
    'use strict';

    return function (options) {
        var columns = [];

        var PAGE = require('./../metka').PAGE;
        var key = options.field.key;
        var $tbody = $('<tbody>');

        var getPropertyNS = require('./utils/getPropertyNS');

        function field2TableHead(prefix) {
            return function (field) {
                columns.push(field);
                return $('<th>')
                    .text(MetkaJS.L10N.get(prefix + '.' + field));
            };
        }

        function tr(transferRow) {
            return $('<tr>')
                .data('transferRow', transferRow)
                .append(columns.map(function (column) {
                    var $td = $('<td>');
                    return $td.append((function () {
                        if (column === 'rowCommands') {
                            $td.css('text-align', 'right');
                            return $('<button type="button" class="btn btn-default btn-xs">')
                                .append('<span class="glyphicon glyphicon-remove"></span> ' + MetkaJS.L10N.get('general.buttons.remove'));
                        }

                        var dataConf = options.dataConf.fields[key];
                        var type = (function () {
                            if (dataConf && dataConf.type === 'REFERENCECONTAINER') {
                                var target = options.dataConf.references[dataConf.reference].target;
                                var field = require('./../metka').dataConfigurations[target].fields[column];
                                return field ? field.type : false;
                            } else {
                                return getPropertyNS(options, 'dataConf.fields', column, 'type');
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

                            return getPropertyNS(transferField, 'value.current');
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
                                        text = MetkaJS.L10N.localize(option, 'title');
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
            $tbody.append(tr(transferRow));
        }

        function addRow(transferRow) {
            require('./data')(options).append(transferRow);
            appendRow(transferRow);
        }

        function addRowFromDataObject(data) {
            addRow(require('./map/object/transferRow')(data));
        }

        function rowDialog(data, title, button, onClose) {
            return function () {
                var $row = $(this);
                var containerOptions = {
                    data: data.call($row),
                    dataConf: options.dataConf,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: options.dataConf.fields[key].subfields.map(function (field) {
                                var dataConfig = options.dataConf.fields[field];

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
                require('./modal')({
                    title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                    body: require('./container').call($('<div>'), containerOptions),
                    buttons: [
                        {
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.' + button))
                                    .click(function () {
                                        var data = {};
                                        options.dataConf.fields[key].subfields.forEach(function (field) {
                                            data[field] = require('./data')(containerOptions)(field).get();
                                        });

                                        onClose.call($row, data);
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
            };
        }

        this.data('addRowFromDataObject', addRowFromDataObject);

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append($('<div class="panel-heading">')
                .text(MetkaJS.L10N.localize(options, 'title')))
            .append($('<table class="table table-condensed">')
                .if(options.field.onClick || !require('./isFieldDisabled')(options), function () {
                    this
                        .addClass('table-hover');

                    $tbody
                        .on('click', 'tr', options.field.onClick || rowDialog(function () {
                            return this.data('transferRow');
                        }, 'add', 'ok', function (data) {
                            var transferRow = require('./map/object/transferRow')(data);
                            this.data('transferRow').fields = transferRow.fields;
                            this.replaceWith(tr(transferRow));
                        }));
                })
                .append($('<thead>')
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
                        .append((getPropertyNS(options, 'dataConf.fields', key, 'subfields') || [])
                            .filter(function (field) {
                                // ui only shows summary fields
                                return !!options.dataConf.fields[field].summaryField;
                            })
                            .map(field2TableHead(PAGE + '.field')))
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
                                //return field2TableHead('general.buttons')('remove');
                            }
                        })))
                .append(function () {
                    var rows = require('./data')(options).get();
                    if (rows) {
                        rows.forEach(appendRow);
                    }
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
                        .click(rowDialog(function () {
                            // initial data is empty
                            return {};
                        }, 'add', 'add', addRowFromDataObject)));
                }

                if (items.length) {
                    return $('<div class="panel-footer clearfix">')
                        .append($('<div class="pull-right">')
                            .append(items));
                }
            }));
    };
});
