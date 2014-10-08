define(function (require) {
    'use strict';

    // initialize utilities
    require('./modules/utils/init');

    return {
        contextPath: MetkaJS.contextPath,
        id: MetkaJS.revisionId,
        page: MetkaJS.configurationType.toLowerCase(),
        PAGE: MetkaJS.configurationType.toUpperCase(),
        no: MetkaJS.revisionNo
    };
});
