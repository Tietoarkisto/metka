define(function (require) {
    'use strict';

    return function (url/*[, urlOpts]*/, options) {
        var mockData = {
            /*'/study/studiesWithVariables': {
                studies: [{
                    id: 1,
                    title: 'mooasdfasdf'
                }, {
                    id: 2,
                    title: 'mooaasdf'
                }, {
                    id: 3,
                    title: 'mooaadfsdfasdf'
                }]
            },*/
            '/studyAttachments': {

            }
        }[url];

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

        if (mockData) {
            setTimeout(function () {
                options.success(mockData);
            }, 0);
            return;
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