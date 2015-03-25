define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function(result, operation, callback) {
        var dismiss = {
            type: 'DISMISS'
        };

        require('./server')('optionsByPath', {
            data: JSON.stringify({
                requests : [{
                    key: "resulttitle",
                    container: "",
                    language: MetkaJS.L10N.locale.toUpperCase(),
                    root: {
                        reference: {
                            key: null,
                            type: "JSON",
                            target: "result_code_descriptions",
                            valuePath: "key",
                            titlePath: "title"
                        },
                        value: result,
                        next: (operation ? {
                            reference: {
                                key: null,
                                type: "DEPENDENCY",
                                target: "result",
                                valuePath: "operations.key",
                                titlePath: "title"
                            },
                            value: operation
                        } : null)
                    }
                }, {
                    key: "resulttext",
                    container: "",
                    language: MetkaJS.L10N.locale.toUpperCase(),
                    root: {
                        reference: {
                            key: null,
                            type: "JSON",
                            target: "result_code_descriptions",
                            valuePath: "key",
                            titlePath: "text"
                        },
                        value: result,
                        next: (operation ? {
                            reference: {
                                key: null,
                                type: "DEPENDENCY",
                                target: "result",
                                valuePath: "operations.key",
                                titlePath: "text"
                            },
                            value: operation
                        } : null)
                    }
                }]
            }),
            success: function (data) {
                if(callback) {
                    dismiss.create = function () {
                        this.click(function () {
                            callback();
                        });
                    };
                }
                require('./modal')({
                    title: MetkaJS.L10N.get('alert.'+(getPropertyNS(data, 'responses.0.options.0.title.value') || "NOTICE").toLowerCase()+'.title'),
                    body: (getPropertyNS(data, 'responses.1.options.0.title.value') || result+(operation ? "."+operation : "")),
                    buttons: [dismiss]
                });
            },
            error: function(jqXHR, status, thrown) {
                // TODO: Form exception dialog
            }
        });
    }
});