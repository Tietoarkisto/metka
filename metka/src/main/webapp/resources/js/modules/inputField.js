define(function (require) {
    'use strict';

    return function (options, type) {
        var id = require('./autoId')();
        var key = options.field.key;
        var $label = require('./label')(options)
            .attr('for', id);

        var $field = (function () {
            if (options.horizontal) {

                // horizontal label should be 2 boostrap main columns wide, even when it's inside column
                var labelWidth = 2 * (options.parent.parent.columns || 1) / (options.colspan || 1);

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
                return 'textarea';
            }

            // STRING, INTEGER, CONCAT

            elemOptions.type = 'text';

            return 'input';
        })();

        var $input = require('./input').call($('<' + nodeType + '>', elemOptions), options);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            require('./datetime').call($field, options, type, $input);
        } else {
            $input
                .prop('disabled', require('./isFieldDisabled')(options))
                .change(function () {
                    require('./data')(options).set($(this).val());
                });

            if (isSelection) {
                require('./selectInput').call($input, options);
            } else {
                // textarea or input elements

                require('./data')(options).onChange(function () {
                    $input.val(type === 'CONCAT'
                        ?
                        options.dataConf.fields[key].concatenate.map(function (key) {
                            return require('./data')(options)(key).get();
                        }).join('')
                        :
                        require('./data')(options).get() || '');
                });
            }

            $field.append($input);
        }
    };
});
