define(function (require) {
    return function (options, type, $input) {
        var key = options.field.key;
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
        setup.options.format = require('./dateFormats')[type];
        setup.options.language = 'fi';

        var defaultDate = MetkaJS.Data.get(key);
        if (defaultDate) {
            setup.options.defaultDate = defaultDate;
        }

        this.append($('<div class="input-group date">')
            .append($input)
            .append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant(setup))
            .datetimepicker(setup.options)
            .if(require('./isFieldDisabled')(options), function () {
                this.data('DateTimePicker').disable();
            }))
            // FIXME: kun kenttä on tyhjä ja ikonia klikataan, arvo tulee heti näkyviin mutta dp.change event ei triggeroidu. mahdollisesti korjattu datetimepickerin päivityksissä?
            .on('dp.change', function (e) {
                MetkaJS.Data.set(key, moment(e.date).format('YYYY-MM-DDThh:mm:ss.s'));
            });
    }
});