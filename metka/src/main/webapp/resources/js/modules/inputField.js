define(function (require) {
    'use strict';

    return function (options, type) {
        var id = require('./autoId')();
        var key = options.field.key;

        this.append(require('./label')(options)
            .attr('for', id));

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

            if (type === 'CHECKBOX') {
                elemOptions.type = 'checkbox';
            } else {
                // STRING, INTEGER, CONCAT

                elemOptions.type = 'text';
            }
            return 'input';
        })();

        var $input = require('./input').call($('<' + nodeType + '>', elemOptions), options);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            require('./datetime').call(this, options, type, $input);
        } else {
            $input
                .prop('disabled', require('./isFieldDisabled')(options))
                .change(function () {
                    options.transferField.value = options.transferField.value || {};
                    options.transferField.type = options.transferField.type || 'VALUE';
                    options.transferField.value.current = $(this).val();
                });

            if (isSelection) {
                require('./selectInput').call($input, options);
            } else {
                // textarea or input elements

                $input.val(
                        type === 'CONCAT'
                        ?
                        options.dataConf.fields[key].concatenate.map(function (key) {
                            return require('./data').get(options, key);
                        }).join('')
                        :
                        require('./utils/getPropertyNS')(options, 'transferField.value.current') || '');
            }

            this.append($input);
        }
    };
});
