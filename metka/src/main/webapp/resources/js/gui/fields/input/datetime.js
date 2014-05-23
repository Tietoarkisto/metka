(function() {
    'use strict';
    GUI.Fields.input.datetime = function ($elem, type, $input, disabled) {
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

        $elem.append($('<div class="input-group date">')
            .append($input)
            //.append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant({icon: icon[type]}))
            .append('<span class="input-group-addon"><span class="glyphicon glyphicon-' + setup.icon + '"></span>')
            .datetimepicker(setup.options)
            .if(disabled, function () {
                this.data('DateTimePicker').disable();
            }));
    }
}());