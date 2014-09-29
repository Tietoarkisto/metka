define(function (require) {
    'use strict';

    return function (options, type, lang) {
        var getPropertyNS = require('./utils/getPropertyNS');
        var id = require('./autoId')();
        var key = options.field.key;
        var $label = require('./label')(options, lang)
            .attr('for', id);

        var $field = (function () {
            if (options.horizontal) {

                // horizontal label should be 2 boostrap main columns wide, even when it's inside column
                var labelWidth = 2 * (getPropertyNS(options, 'parent.parent.columns') || 1) / (options.colspan || 1);

                $label.addClass('col-xs-' + labelWidth);

                var $inputWrapper = $('<div>')
                    .addClass('col-xs-' + (12 - labelWidth));

                this
                    .append($('<div class="form-horizontal">')
                        .append($('<div class="form-group">')
                            .append($label)
                            .append($inputWrapper)));

                return $inputWrapper;
            } else {
                return this.append($label);
            }
        }).call(this);

        var elemOptions = {
            'class': 'form-control',
            id: id
        };
        var isSelection = type === 'SELECTION';
        var nodeType = (function () {
            if (isSelection) {
                return 'select';
            }

            if (options.field.multiline) {
                elemOptions.rows = '4';
                return 'textarea';
            }

            // STRING, INTEGER, CONCAT

            elemOptions.type = 'text';

            return 'input';
        })();

        var $input = require('./input').call($('<' + nodeType + '>', elemOptions), options);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            require('./datetime').call($field, options, type, $input, lang);
        } else {
            $input
                .prop('disabled', require('./isFieldDisabled')(options, lang))
                .change(function () {
                    require('./data')(options).setByLang(lang, $(this).val());
                });

            $field.append($input);

            if (isSelection) {
                require('./selectInput')($input, options, lang);
            } else {
                // textarea or input elements

                require('./data')(options).onChange(function () {
                    function setValue() {
                        require('./reference').option(key, options, lang, function (value) {
                            $input.val(value);
                        })(options.data.fields, reference);
                    }
                    var dataConf = getPropertyNS(options, 'dataConf.fields', key);
                    if (dataConf && dataConf.type === 'REFERENCE') {
                        var reference = getPropertyNS(options, 'dataConf.references', dataConf.reference);
                        options.$events.on('data-change-{key}-{lang}'.supplant({
                            key: reference.target,
                            lang: lang
                        }), setValue);
                        // TODO: setValue call is not necessary, if target is select input. select input triggers change event
                        setValue();
                    } else {
                        $input.val(type === 'CONCAT'
                            ?
                            dataConf.concatenate.map(function (key) {
                                return require('./data')(options)(key).getByLang(lang);
                            }).join('')
                            :
                            require('./data')(options).getByLang(lang) || '');
                    }
                });
            }
        }
        return this;
    };
});
