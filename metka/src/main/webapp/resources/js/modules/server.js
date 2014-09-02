define(function (require) {
    'use strict';

    return function (url/*[, urlOpts]*/, options) {
        /*var mockData = {
            '/binder/binderContent/{binderId}': {
                pages: [{
                    pageId: 1,
                    study: 21,
                    studyId: 'FSD123',
                    studyTitle: 'wqewqe',
                    savedBy: 'me',
                    binderId: 30,
                    description: 'asfassa'
                }, {
                    pageId: 2,
                    study: 21,
                    studyId: 'FSD123',
                    studyTitle: 'wqewqeewrerw',
                    savedBy: 'me',
                    binderId: 30,
                    description: 'asfasffdafsa'
                }]
            }
        }[url];*/

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
/*
        if (mockData) {
            setTimeout(function () {
                options.success(mockData);
            }, 0);
            return;
        }*/

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