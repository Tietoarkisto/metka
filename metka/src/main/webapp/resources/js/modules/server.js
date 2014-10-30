define(function (require) {
    'use strict';

    /**
     * Combines `url` module and jQuery AJAX with default options.
     *
     * @param {string} url Valid string for module `url`.
     * @param {object} (optional) urlOpts Valid object for module `url`.
     * @param {object} options jQuery AJAX options, if other then default values in this function.
     */
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
            cache: false,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: url
        }, options));
    };
});