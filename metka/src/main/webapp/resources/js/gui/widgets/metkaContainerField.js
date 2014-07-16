(function () {
    'use strict';

    $.widget('metka.metkaContainerField', $.metka.metkaField, {
        _create: function () {
            this.columns = [];
            var context = MetkaJS.Globals.page.toUpperCase();
            var key = this.options.field.key;
            this.$tbody = $('<tbody>');

            var that = this;

            function field2TableHead(field) {
                this.columns.push(field);
                return $('<th>')
                    .text(MetkaJS.L10N.get(context + '.field.' + field));
            }

            this.element.append($('<div class="form-group">')
                .append($('<div class="panel">')
                    .addClass('panel-' + (this.options.style || 'default'))
                    .append($('<div class="panel-heading">')
                        .text(MetkaJS.L10N.localize(this.options, 'title')))
                    .append($('<table class="table table-condensed">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .append((MetkaJS.JSConfig[context].fields[key].subfields || [])
                                    .filter(function (field) {
                                        // ui only shows summary fields
                                        return !!MetkaJS.JSConfig[context].fields[field].summaryField;
                                    })
                                    .map(field2TableHead, this))
                                .append((this.options.field.columnFields || [])
                                    .map(field2TableHead, this))
                                .append(function () {
                                    if (this.options.field.showSaveInfo) {
                                        this.columns.push('savedAt');
                                        this.columns.push('savedBy');
                                        return [$('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedAt')),
                                        $('<th>')
                                                .text(MetkaJS.L10N.get('general.saveInfo.savedBy'))];
                                    }
                                }.bind(this))
                                ))
                        .append((function () {
                            (MetkaJS.Data.get(key) || []).forEach(this.addRow, this);
                            return this.$tbody;
                        }).call(this))))
                .if(!this.isFieldDisabled(), function () {
                    this.append($('<div>') /*class="pull-right"*/
                        .append($.metka.metkaButton({
                            style: 'default',
                            create: function () {
                                $(this)
                                    .text(MetkaJS.L10N.get('general.table.add'))
                                    .click(function () {
                                        //MetkaJS.DialogHandlers.generalContainerHandler.show(key);
                                        var modal = $.metka.metkaModal({
                                            title: MetkaJS.L10N.get(['dialog', context, key, 'add'].join('.')),
                                            body: $.metka.metkaContainer({
                                                content: [{
                                                    type: 'COLUMN',
                                                    columns: 1,
                                                    rows: MetkaJS.JSConfig[context].fields[key].subfields.map(function (field) {
                                                        // clear data, so we know which fields were set when modal was open
                                                        // TODO: containers, like MetkaModal or MetkaUI, should be instantiated with pointer to some private data, not global MetkaJS.Data
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
                                            }).element,
                                            buttons: [{
                                                create: function () {
                                                    $(this)
                                                        .text(MetkaJS.L10N.get('general.buttons.add'))
                                                        .click(function () {
                                                            var dataRow = {};
                                                            MetkaJS.JSConfig[context].fields[key].subfields.forEach(function (field) {
                                                                dataRow[field] = MetkaJS.Data.get(field);
                                                            });

                                                            that.addRow(dataRow);

                                                            var data = JSON.parse(MetkaJS.Data.get(key) || '[]');
                                                            data.push(dataRow);
                                                            MetkaJS.Data.set(key, JSON.stringify(data));
                                                        });
                                                }
                                            }, {
                                                create: function () {
                                                    $(this)
                                                        .text(MetkaJS.L10N.get('general.buttons.cancel'))
                                                }
                                            }]
                                        });
                                    });
                            }
                        }).element));
                }));
        },
        addRow: function (data) {
            var context = MetkaJS.Globals.page.toUpperCase();
            this.$tbody.append($('<tr>')
                .data(data)
                .append(this.columns.map(function (column) {
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
                                return moment(value).format(this.options.dateFormats[type]);
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
                    }).call(this));
            }, this)));
        }
    });
})();