define(function (require) {
    'use strict';

    return function (url/*[, urlOpts]*/, options) {
        switch (arguments.length) {
            case 3:
                url = require('./url')(url, options);
                options = arguments[2];
                break;
            case 2:
                url = require('./url')(url);
                break;
            default:
                throw 'illegal number of arguments';
        }

        $.ajax($.extend({
            type: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: url
        }, options));
    };
});