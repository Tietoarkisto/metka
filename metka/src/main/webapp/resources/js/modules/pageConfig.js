// page configurations
define(function (require) {
    'use strict';

    var page = require('./../metka').page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }

    var pages = {
        expertsearch: require('./pages/expertSearch'),
        study: require('./pages/study'),
        series: require('./pages/series'),
        publication: require('./pages/publication')
    };

    if (!pages[page]) {
        throw 'page not found (page: {page})'.supplant({
            page: page
        });
    }

    return pages[page];
});