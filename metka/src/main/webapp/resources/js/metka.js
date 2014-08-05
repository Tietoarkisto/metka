define(function (require) {
    'use strict';

    // initialize utilities
    require('./modules/utils/init');

    return {
        contextPath: MetkaJS.Globals.contextPath,
        id: MetkaJS.revisionId,
        page: MetkaJS.configurationType.toLowerCase(),
        PAGE: MetkaJS.configurationType.toUpperCase(),
        revision: MetkaJS.revisionNo,
        no: MetkaJS.revisionNo,
        dataConfigurations: MetkaJS.JSConfig
    };
});
