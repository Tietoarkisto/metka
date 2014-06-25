$.widget('metka.metkaField', $.metka.metkaField, {
    datetime: function (type, $input, key) {
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
            //.append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant({icon: icon[type]}))
            .append('<span class="input-group-addon"><span class="glyphicon glyphicon-' + setup.icon + '"></span>')
            .datetimepicker(setup.options)
            .if(this.isDisabled(), function () {
                this.data('DateTimePicker').disable();
            }));
    }
});