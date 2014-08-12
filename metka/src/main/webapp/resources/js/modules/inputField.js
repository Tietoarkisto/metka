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

            // STRING, INTEGER, CONCAT

            elemOptions.type = 'text';

            return 'input';
        })();

        var $input = require('./input').call($('<' + nodeType + '>', elemOptions), options);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            require('./datetime').call(this, options, type, $input);
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

            this.append($input);
        }
    };
});
