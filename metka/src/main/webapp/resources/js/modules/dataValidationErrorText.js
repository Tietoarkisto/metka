define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (list, callback) {
        var texts = new Array();
        log(list);
        // TODO: Fetch error strings
        list.forEach(function(error) {
            require('./server')('optionsByPath', {
                data: JSON.stringify({
                    requests : [{
                        key: "fielderror",
                        container: "",
                        language: MetkaJS.L10N.locale.toUpperCase(),
                        root: {
                            reference: {
                                key: null,
                                type: "JSON",
                                target: "field_error_descriptions",
                                valuePath: "key",
                                titlePath: "text"
                            },
                            value: error/*, Deeper levels not yet supported, possibility for field specific messages is still open
                            next: (operation ? {
                                reference: {
                                    key: null,
                                    type: "DEPENDENCY",
                                    target: "result",
                                    valuePath: "operations.key",
                                    titlePath: "title"
                                },
                                value: operation
                            } : null)*/
                        }
                    }]
                }),
                success: function (data) {
                    if(callback) {
                        callback(getPropertyNS(data, 'responses.0.options.0.title.value'));
                    }
                }
            });
        });
    };
});
