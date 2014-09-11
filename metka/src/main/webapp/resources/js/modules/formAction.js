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
                            $.each(field.values, function (lang) {
                                if (lang.errors) {
                                    lang.errors.length = 0
                                }
                            });
                        }
                        if (field.rows) {
                            $.each(field.rows, function (lang, rows) {
                                rows.forEach(function (row) {
                                    if (row.errors) {
                                        row.errors.length = 0
                                    }
                                    clearErrors(row.fields);
                                });
                            });
                        }
                    });
                })(options.data.fields);

                require('./server')(url, {
                    data: JSON.stringify(options.data),
                    success: function (response) {
                        require('./modal')({
                            title: function() {
                                if(!successConditions) {
                                    return MetkaJS.L10N.get('alert.error.title');
                                }
                                for(var i = 0; i<successConditions.length; i++) {
                                    if(successConditions[i] === response.result) {
                                        return MetkaJS.L10N.get('alert.notice.title');
                                    }
                                }
                                return MetkaJS.L10N.get('alert.error.title');
                            }(),
                            body: response.result /*data.errors.map(function (error) {
                             return MetkaJS.L10N.get(error.msg);
                             })*/,
                            buttons: [{
                                type: 'DISMISS'
                            }]
                        });

                        onSuccess(response);
                    }
                });
            };
        };
    };
});