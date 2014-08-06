define(function (require) {
    'use strict';

    return function (options) {
        var columns = [];

        var PAGE = require('./../metka').PAGE;
        var key = options.field.key;
        var $tbody = $('<tbody>');


        function field2TableHead(prefix) {
            return function (field) {
                columns.push(field);
                return $('<th>')
                    .text(MetkaJS.L10N.get(prefix + '.' + field));
            };
        }

        var getPropertyNS = require('./utils/getPropertyNS');

        function addRow(data) {
            $tbody.append($('<tr>')
                .data(data)
                .append(columns.map(function (column) {
                    return $('<td>').append((function () {
                        if (column === 'remove') {
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

                        var value = getPropertyNS(data, column);
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
                })));
        }

        options.addRow = addRow;

        this.append($('<div class="panel">')
            .addClass('panel-' + (options.style || 'default'))
            .append($('<div class="panel-heading">')
                .text(MetkaJS.L10N.localize(options, 'title')))
            .append($('<table class="table table-condensed">')
                .append($('<thead>')
                    .append($('<tr>')
                        .append(function () {
                            if (/*options.field.showReferenceKey*/getPropertyNS(options, 'dataConf.fields', key, 'showReferenceKey')) {
                                var target = options.dataConf.references[options.dataConf.fields[key].reference].target;
                                return field2TableHead(target + '.field')(require('./../metka').dataConfigurations[target].idField);
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
                            if (options.field.onRemove) {
                                $tbody
                                    .on('click', 'tr button', function () {
                                        options.field.onRemove($(this).closest('tr'));
                                        return false;
                                    });
                                return field2TableHead('general.buttons')('remove');
                            }
                        })))
                .append(function () {
                    (require('./data').get(options, key) || []).forEach(addRow);
                    return $tbody;
                }))
            .append(function () {
                var items = [];
                if (key === 'files') {
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
                }

                if (!require('./isFieldDisabled')(options) && key !== 'files') {
                    items.push(require('./button')(options)({
                        style: 'default'
                    })
                        .addClass('btn-sm')
                        .text(MetkaJS.L10N.get('general.table.add'))
                        .click(function () {
                            var containerOptions = {
                                data: {},
                                dataConf: options.dataConf,
                                content: [{
                                    type: 'COLUMN',
                                    columns: 1,
                                    rows: options.dataConf.fields[key].subfields.map(function (field) {
                                        // clear data, so we know which fields were set when modal was open
                                        //require('./data').set(options, field, null);

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
                                }]
                            };
                            require('./modal')({
                                title: MetkaJS.L10N.get(['dialog', PAGE, key, 'add'].join('.')),
                                body: require('./container').call($('<div>'), containerOptions),
                                buttons: [{
                                    create: function () {
                                        this
                                            .text(MetkaJS.L10N.get('general.buttons.add'))
                                            .click(function () {
                                                var dataRow = {};
                                                options.dataConf.fields[key].subfields.forEach(function (field) {
                                                    dataRow[field] = require('./data').get(containerOptions, field);
                                                });

                                                addRow(dataRow);

                                                var data = JSON.parse(require('./data').get(options, key) || '[]');
                                                data.push(dataRow);
                                                require('./data').set(options, key, JSON.stringify(data));
                                            });
                                    }
                                }, {
                                    create: function () {
                                        this
                                            .text(MetkaJS.L10N.get('general.buttons.cancel'))
                                    }
                                }]
                            });
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
