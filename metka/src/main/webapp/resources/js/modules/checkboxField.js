define([
    './isFieldDisabled',
    './input',
    './label'
], function (isFieldDisabled, input, label) {
    return function (options) {
        var key = options.field.key;
        this
            .addClass('checkbox')
            .append(label(options)
                .prepend(input.call($('<input type="checkbox">'), options)
                    .prop('disabled', isFieldDisabled(options))
                    .change(function () {
                        MetkaJS.Data.set(key, $(this).prop('checked'));
                    })
                    .prop('checked', !!MetkaJS.Data.get(key))));
    };
});
