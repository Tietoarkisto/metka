define(function (require) {
    'use strict';

    return function (url) {
        return function (options, onSuccess, successConditions, operation) {
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

                        var isExpectedResult = successConditions ? successConditions.some(function (condition) {
                            return condition === response.result;
                        }) : true;

                        require('./resultViewer')(response.result, operation, function() {
                            if(isExpectedResult) {
                                onSuccess.call(that, response);
                            }
                        });
                    }
                });
            };
        };
    };
});