define(function (require) {
    'use strict';

    return function (options) {
        var columns = [];

        var context = MetkaJS.Globals.page.toUpperCase();
        var key = options.field.key;
        var $tbody = $('<tbody>');

        function field2TableHead(field) {
            columns.push(field);
            return $('<th>')
                .text(MetkaJS.L10N.get(context + '.field.' + field));
        }

        function addRow(data) {
            var context = MetkaJS.Globals.page.toUpperCase();
            $tbody.append($('<tr>')
                .data(data)
                .append(columns.map(function (column) {
                    return $('<td>').text((function () {
                        var type = MetkaJS.objectGetPropertyNS(MetkaJS, 'JSConfig', context, 'fields', column, 'type');
                        if (!type) {
                            log('not implemented', column);
                            return '-';
                        }

                        var value = MetkaJS.objectGetPropertyNS(data, column);
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
                                if (MetkaJS.objectGetPropertyNS(MetkaJS, 'JSConfig', context, 'selectionLists', MetkaJS.JSConfig[context].fields[column].selectionList, 'options').some(function (option) {
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

        this.append($('<div class="form-group">')
            .append($('<div class="panel">')
                .addClass('panel-' + (options.style || 'default'))
                .append($('<div class="panel-heading">')
                    .text(MetkaJS.L10N.localize(options, 'title')))
                .append($('<table class="table table-condensed">')
                    .append($('<thead>')
                        .append($('<tr>')
                            .append((MetkaJS.objectGetPropertyNS(options, 'dataConf.fields', key, 'subfields') || [])
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
                        (MetkaJS.Data.get(key) || []).forEach(addRow);
                        return $tbody;
                    })))
            .if(!require('./isFieldDisabled')(options), function () {
                this.append($('<div>') /*class="pull-right"*/
                    .append(require('./button')({
                        style: 'default'
                    })
                        .text(MetkaJS.L10N.get('general.table.add'))
                        .click(function () {
                            //MetkaJS.DialogHandlers.generalContainerHandler.show(key);
                            require('./modal')({
                                title: MetkaJS.L10N.get(['dialog', context, key, 'add'].join('.')),
                                body: require('./container').call($('<div>'), {
                                    dataConf: options.dataConf,
                                    content: [{
                                        type: 'COLUMN',
                                        columns: 1,
                                        rows: MetkaJS.JSConfig[context].fields[key].subfields.map(function (field) {
                                            // clear data, so we know which fields were set when modal was open
                                            // TODO: containers, like modal or MetkaUI, should be instantiated with pointer to some private data, not global MetkaJS.Data
                                            MetkaJS.Data.set(field, null);

                                            var dataConfig = MetkaJS.JSConfig[context].fields[field];

                                            return {
                                                type: 'ROW',
                                                cells: [$.extend({}, dataConfig, {
                                                    type: 'CELL',
                                                    title: MetkaJS.L10N.get(context + '.field.' + field),
                                                    field: dataConfig
                                                })]
                                            };
                                        })
                                    }]
                                }),
                                buttons: [{
                                    create: function () {
                                        this
                                            .text(MetkaJS.L10N.get('general.buttons.add'))
                                            .click(function () {
                                                var dataRow = {};
                                                MetkaJS.JSConfig[context].fields[key].subfields.forEach(function (field) {
                                                    dataRow[field] = MetkaJS.Data.get(field);
                                                });

                                                addRow(dataRow);

                                                var data = JSON.parse(MetkaJS.Data.get(key) || '[]');
                                                data.push(dataRow);
                                                MetkaJS.Data.set(key, JSON.stringify(data));
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
