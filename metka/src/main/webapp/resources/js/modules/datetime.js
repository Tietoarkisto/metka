define(function (require) {
    return function (options, type, $input, lang) {
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

        this.append($('<div class="input-group date">')
            .append($input)
            .append('<span class="input-group-addon"><span class="glyphicon glyphicon-{icon}"></span>'.supplant(setup))
            .datetimepicker(setup.options)
            .if(require('./isFieldDisabled')(options), function () {
                this.data('DateTimePicker').disable();
            })
            .me(function () {
                require('./data')(options).onChange(function () {
                    var date = require('./data')(options).getByLang(lang);
                    if (date) {
                        this.data('DateTimePicker').setDate(date);
                    }
                }.bind(this));
            }))
            // FIXME: kun kenttä on tyhjä ja ikonia klikataan, arvo tulee heti näkyviin mutta dp.change event ei triggeroidu. mahdollisesti korjattu datetimepickerin päivityksissä?
            .on('dp.change', function (e) {
                require('./data')(options).setByLang(lang, moment(e.date).format('YYYY-MM-DDThh:mm:ss.s'));
            });
    }
});