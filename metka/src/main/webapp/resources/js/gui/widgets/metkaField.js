(function () {
    'use strict';

    $.widget('metka.metkaField', $.metka.metka, {
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
            this.element.append($('<div class="form-group">')
                .append($('<div class="panel panel-default">')
                    .append($('<div class="panel-heading">')
                        .text(MetkaJS.L10N.localize(this.options, 'title')))
                    .append($('<table class="table table-condensed">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .eachTo(MetkaJS.JSConfig[context].fields[key].subfields, function (i, field) {
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
                        .append($('<tbody>')
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
                                        MetkaJS.DialogHandlers.generalContainerHandler.show(key);
                                    });
                            }
                        })));
                }));
        },
        inputField: function (type) {
            var id = this.autoId();
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

                    var key = this.options.field.key;
                    $input
                        .val(MetkaJS.Data.get(key));
                }

                this.element.append($input);
            }
        },
        datetime: function (type, $input) {
            'use strict';
            var setup = {
                DATE: {
                    options: {
                        pickTime: false,
                        format: 'YYYY-MM-DD'
                    },
                    icon: 'calendar'
                },
                TIME: {
                    options: {
                        pickDate: false,
                        format: 'hh.mm'
                    },
                    icon: 'time'
                },
                DATETIME: {
                    options: {
                        format: 'YYYY-MM-DD hh.mm'
                    },
                    icon: 'calendar'
                }
            }[type];
            setup.options.language = 'fi';

            try {
                var defaultDate = MetkaJS.Data.get(this.options.field.key);
                if (defaultDate) {
                    setup.options.defaultDate = defaultDate;
                }
            } catch (e) {}

            this.element.append($('<div class="input-group date">')
                .append($input)
                .append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant(setup))
                .datetimepicker(setup.options)
                .if(this.isFieldDisabled(), function () {
                    this.data('DateTimePicker').disable();
                }));
        }
    });
})();