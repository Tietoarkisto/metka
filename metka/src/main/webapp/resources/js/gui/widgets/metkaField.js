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
            var type = this.options.field.displayType || MetkaJS.objectGetPropertyNS(MetkaJS, 'JSConfig', MetkaJS.Globals.page.toUpperCase(), 'fields', this.options.field.key, 'type');

            if (!type) {
                log('field type is not set', this.options);
                return;
            }

            if (type === 'CONTAINER') {
                this.element.metkaContainerField(this.options);
            } else {
                if (type === 'CHECKBOX') {
                    this.checkboxField();
                } else {
                    this.inputField(type);
                }
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

                if (type === 'CHECKBOX') {
                    options.type = 'checkbox';
                } else {
                    // STRING, INTEGER, CONCAT

                    options.type = 'text';
                }
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
        checkboxField: function () {
            var key = this.options.field.key;
            this.element
                .addClass('checkbox')
                .append($.metka.metkaLabel(this.options).element
                    .prepend($('<input type="checkbox">')
                        .metkaInput(this.options)
                        .prop('disabled', this.isFieldDisabled())
                        .change(function () {
                            MetkaJS.Data.set(key, $(this).prop('checked'));
                        })
                        .prop('checked', !!MetkaJS.Data.get(key))));
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

            var defaultDate = MetkaJS.Data.get(key);
            if (defaultDate) {
                setup.options.defaultDate = defaultDate;
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