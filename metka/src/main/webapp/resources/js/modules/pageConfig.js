// page configurations
define(function (require) {
    'use strict';

    var page = require('./../metka').page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }

    return {
        expertSearch: require('./pages/expertSearch'),
        study: require('./pages/defaults'),
        series: require('./pages/series')
    }[page];
});