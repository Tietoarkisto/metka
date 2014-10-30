/**
 * Get page specific configuration
 */
define(function (require) {
    'use strict';

    var page = require('./../metka').page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }

    // TODO: would be better to use async require here, since there's no caching or minifying in use currently.
    // Still, caching and minifying everything in one script would be better then async load.

    var pages = {
        expert: require('./pages/expert'),
        binder: require('./pages/binder'),
        publication: require('./pages/publication'),
        series: require('./pages/series'),
        study: require('./pages/study'),
        study_variables: require('./pages/study_variables'),
        settings: require('./pages/settings')
    };

    if (!pages[page]) {
        throw 'page not found (page: {page})'.supplant({
            page: page
        });
    }

    return pages[page];
});