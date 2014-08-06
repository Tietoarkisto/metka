// page configurations
define(function (require) {
    'use strict';

    var page = require('./../metka').page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }



    return {
        expertsearch: require('./pages/expertSearch'),
        study: require('./pages/study'),
        series: require('./pages/series')
    }[page];
});