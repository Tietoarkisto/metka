define(function (require) {
    'use strict';

    return function (url) {
        return function (options, onSuccess, successConditions) {
            return function () {
                (function clearErrors(fields) {
                    $.each(fields, function (key, field) {
                        if (field.errors) {
                            field.errors.length = 0
                        }
                        if (field.values) {
                            $.each(field.values, function (lang, value) {
                                if (value && value.errors) {
                                    value.errors.length = 0
                                }
                            });
                        }
                        if (field.rows) {
                            $.each(field.rows, function (lang, rows) {
                                rows.forEach(function (row) {
                                    if (row.errors) {
                                        row.errors.length = 0
                                    }
                                    if (row.fields) {
                                        clearErrors(row.fields);
                                    }
                                });
                            });
                        }
                    });
                })(options.data.fields);

                var that = this;

                require('./server')(url, {
                    data: JSON.stringify(options.data),
                    success: function (response) {
                        require('./resultViewer')(response.result);
                        /*var isExpectedResult = successConditions ? successConditions.some(function (condition) {
                            return condition === response.result;
                        }) : true;
                        var dismiss = {
                            type: 'DISMISS'
                        };
                        if (isExpectedResult) {
                            dismiss.create = function () {
                                this.click(function () {
                                    onSuccess.call(that, response);
                                });
                            };
                        }
                        require('./modal')({
                            title: MetkaJS.L10N.get(isExpectedResult ? 'alert.notice.title' : 'alert.error.title'),
                            body: response.result *//*data.errors.map(function (error) {
                             return MetkaJS.L10N.get(error.msg);
                             })*//*,
                            buttons: [dismiss]
                        });*/
                    }
                });
            };
        };
    };
});