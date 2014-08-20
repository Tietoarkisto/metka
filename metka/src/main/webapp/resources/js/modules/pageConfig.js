// page configurations
define(function (require) {
    'use strict';

    var page = require('./../metka').page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }

    var pages = {
        binders: require('./pages/binders'),
        expertsearch: require('./pages/expertSearch'),
        publication: require('./pages/publication'),
        series: require('./pages/series'),
        study: require('./pages/study'),
        study_variables: require('./pages/study_variables')
    };

    if (!pages[page]) {
        throw 'page not found (page: {page})'.supplant({
            page: page
        });
    }

    return pages[page];
});