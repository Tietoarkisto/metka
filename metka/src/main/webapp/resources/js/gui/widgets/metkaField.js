(function () {
    'use strict';

    $.widget('metka.metkaField', $.metka.metka, {
        options: {
            dateFormats: {
                DATE: 'YYYY-MM-DD',
                TIME: 'hh.mm',
                DATETIME: 'YYYY-MM-DD hh.mm'
            }
        },
        _create: function () {
            //this.options.field.multichoice
            //this.options.field.showReferenceKey
            //this.options.field.showReferenceValue
            //this.options.field.handlerName
            var type = this.options.field.displayType || MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[this.options.field.key].type;

            if (type === 'CONTAINER') {
                this.containerField();
            } else {
                this.inputField(type);
            }
        },
        isFieldDisabled: function () {
            // TODO: disabled: (field.type === MetkaJS.E.Field.REFERENCE)

            var key = this.options.field.key;
            var dataConf = MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[key];

            // if data should be immutable and original value is set, field is disabled
            if (dataConf.immutable && MetkaJS.objectGetPropertyNS(MetkaJS.data.fields, key, 'originalValue')) {
                return true;
            }

            return this.options.readOnly || this.options.field.readOnly || !dataConf.editable;
        },
        containerField: function () {
            var columns = [];
            var context = MetkaJS.Globals.page.toUpperCase();
            var key = this.options.field.key;
            var $tbody = $('<tbody>');
            var options = this.options;
            this.element.append($('<div class="form-group">')
                .append($('<div class="panel panel-default">')
                    .append($('<div class="panel-heading">')
                        .text(MetkaJS.L10N.localize(this.options, 'title')))
                    .append($('<table class="table table-condensed">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .eachTo(MetkaJS.JSConfig[context].fields[key].subfields, function (i, field) {
                                    // ui only shows summary fields
                                    if (MetkaJS.JSConfig[context].fields[field].summaryField) {
                                        columns.push(field);
                                        this
                                            .append($('<th>')
                                                .text(MetkaJS.L10N.get(context + '.field.' + field)));
                                    }
                                })
                                .eachTo(this.options.field.columnFields, function (i, field) {
                                    columns.push(field);
                                    this
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get(context + '.field.' + field)));
                                })
                                .if(this.options.field.showSaveInfo, function () {
                                    columns.push('savedAt');
                                    columns.push('savedBy');
                                    this
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedAt')))
                                        .append($('<th>')
                                            .text(MetkaJS.L10N.get('general.saveInfo.savedBy')));
                                })))
                        .append($tbody
                            // TODO: onclick open and edit
                            .eachTo(['moo1', 'moo2'], function (i, data) {
                                this.append($('<tr>').eachTo(columns, function (i, column) {
                                    this.append($('<td>' + column + '-' + data + '</td>'));
                                }));
                            }))))
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

                                                            $tbody.append($('<tr>').eachTo(columns, function (i, field) {
                                                                this.append($('<td>').text((function () {
                                                                    var type = MetkaJS.JSConfig[context].fields[field].type;
                                                                    var value = MetkaJS.Data.get(field);
                                                                    if (type === 'STRING') {
                                                                        return value || '-';
                                                                    }
                                                                    if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                                                                        if (value) {
                                                                            return moment(value).format(options.dateFormats[type]);
                                                                        }
                                                                        return '-';
                                                                    }
                                                                    if (type === 'SELECTION') {
                                                                        if (typeof value !== 'undefined') {
                                                                            var text;
                                                                            if (MetkaJS.objectGetPropertyNS(MetkaJS, 'JSConfig', context, 'selectionLists', MetkaJS.JSConfig[context].fields[field].selectionList, 'options').some(function (option) {
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
                                                                    log('not implemented', type);
                                                                })()));
                                                            }));

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
        inputField: function (type) {
            var id = this.autoId();
            var key = this.options.field.key;
            this.element.append($.metka.metkaLabel(this.options).element
                .attr('for', id));

            var options = {
                'class': 'form-control',
                id: id
            };
            var isSelection = type === 'SELECTION';
            var nodeType = (function () {
                if (isSelection) {
                    return 'select';
                }

                if (this.options.field.multiline) {
                    return 'textarea';
                }

                options.type = 'text';
                return 'input';
            }).apply(this);

            var $input = $('<' + nodeType + '>', options)
                .metkaInput(this.options);

            if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                this.datetime(type, $input);
            } else {
                $input
                    .prop('disabled', this.isFieldDisabled())
                    .change(function () {
                        MetkaJS.Data.set(key, $(this).val());
                    });

                if (isSelection) {
                    $input.metkaInput('select');
                } else {
                    // textarea or input elements

                    $input.val(
                            type === 'CONCAT'
                            ?
                            MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[key].concatenate.map(MetkaJS.Data.get).join('')
                            :
                            MetkaJS.Data.get(key));
                }

                this.element.append($input);
            }
        },
        datetime: function (type, $input) {
            var key = this.options.field.key;
            var setup = {
                DATE: {
                    options: {
                        pickTime: false
                    },
                    icon: 'calendar'
                },
                TIME: {
                    options: {
                        pickDate: false
                    },
                    icon: 'time'
                },
                DATETIME: {
                    options: {},
                    icon: 'calendar'
                }
            }[type];
            setup.options.format = this.options.dateFormats[type];
            setup.options.language = 'fi';

            try {
                var defaultDate = MetkaJS.Data.get(key);
                if (defaultDate) {
                    setup.options.defaultDate = defaultDate;
                }
            } catch (e) {
            }

            this.element.append($('<div class="input-group date">')
                .append($input)
                .append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant(setup))
                .datetimepicker(setup.options)
                .if(this.isFieldDisabled(), function () {
                    this.data('DateTimePicker').disable();
                }))
                // FIXME: kun kenttä on tyhjä ja ikonia klikataan, arvo tulee heti näkyviin mutta dp.change event ei triggeroidu. mahdollisesti korjattu datetimepickerin päivityksissä?
                .on('dp.change', function (e) {
                    MetkaJS.Data.set(key, moment(e.date).format('YYYY-MM-DDThh:mm:ss.s'));
                });
        }
    });
})();