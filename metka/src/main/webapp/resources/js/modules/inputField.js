define([
    './autoId',
    './datetime',
    './isFieldDisabled',
    './input',
    './selectInput',
    './label'
], function (autoId, datetime, isFieldDisabled, input, selectInput, label) {
    return function (options, type) {
        var id = autoId();
        var key = options.field.key;

        this.append(label(options)
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

        var $input = input.call($('<' + nodeType + '>', elemOptions), options);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            datetime.call(this, options, type, $input);
        } else {
            $input
                .prop('disabled', isFieldDisabled(options))
                .change(function () {
                    MetkaJS.Data.set(key, $(this).val());
                });

            if (isSelection) {
                selectInput.call($input, options);
            } else {
                // textarea or input elements

                $input.val(
                        type === 'CONCAT'
                        ?
                        MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[key].concatenate.map(MetkaJS.Data.get).join('')
                        :
                        MetkaJS.Data.get(key));
            }

            this.append($input);
        }
    };
});
