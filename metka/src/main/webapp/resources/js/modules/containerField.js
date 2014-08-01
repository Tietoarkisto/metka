define(function (require) {
    'use strict';

    return function (options) {
        var columns = [];

        var PAGE = require('./../metka').PAGE;
        var key = options.field.key;
        var $tbody = $('<tbody>');

        function field2TableHead(field) {
            columns.push(field);
            return $('<th>')
                .text(MetkaJS.L10N.get(PAGE + '.field.' + field));
        }

        function addRow(data) {
            $tbody.append($('<tr>')
                .data(data)
                .append(columns.map(function (column) {
                    return $('<td>').text((function () {
                        var type = require('./utils/getPropertyNS')(options, 'dataConf.fields', column, 'type');
                        if (!type) {
                            log('not implemented', column);
                            return '-';
                        }

                        var value = require('./utils/getPropertyNS')(data, column);
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
                                if (require('./utils/getPropertyNS')(options, 'dataConf.selectionLists', options.dataConf.fields[column].selectionList, 'options').some(function (option) {
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

        this.append($('<div class="form-group">')
            .append($('<div class="panel">')
                .addClass('panel-' + (options.style || 'default'))
                .append($('<div class="panel-heading">')
                    .text(MetkaJS.L10N.localize(options, 'title')))
                .append($('<table class="table table-condensed">')
                    .append($('<thead>')
                        .append($('<tr>')
                            .append((require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'subfields') || [])
                                .filter(function (field) {
                                    // ui only shows summary fields
                                    return !!options.dataConf.fields[field].summaryField;
                                })
                                .map(field2TableHead))
                            .append((options.field.columnFields || [])
                                .map(field2TableHead))
                            .append(function () {
                                if (options.field.showSaveInfo) {
                                    columns.push('savedAt');
                                    columns.push('savedBy');
                                    return [$('<th>')
                                        .text(MetkaJS.L10N.get('general.saveInfo.savedAt')),
                                        $('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedBy'))];
                                }
                            })
                    ))
                    .append(function () {
                        (require('./data').get(options, key) || []).forEach(addRow);
                        return $tbody;
                    })))
            .if(!require('./isFieldDisabled')(options), function () {
                this.append($('<div>') /*class="pull-right"*/
                    .append(require('./button')(options)({
                        style: 'default'
                    })
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
                        })));
            }));
    };
});
