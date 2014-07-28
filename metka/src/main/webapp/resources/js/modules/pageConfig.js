// page configurations
define([
    './pages/defaults',
    './pages/expertSearch'
], function (defaults, expertSearch) {
    'use strict';

    var page = MetkaJS.Globals.page;
    if (typeof page !== 'string') {
        throw 'page is not set';
    }

    return {
        expertSearch: expertSearch,
        study: defaults,
        series: defaults
    }[page];
});