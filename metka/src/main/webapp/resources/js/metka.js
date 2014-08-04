define(function (require) {
    'use strict';

    // initialize utilities
    require('./modules/utils/init');

    return {
        contextPath: MetkaJS.Globals.contextPath,
        id: MetkaJS.SingleObject.id,
        page: MetkaJS.Globals.page,
        PAGE: MetkaJS.Globals.page.toUpperCase(),
        revision: MetkaJS.SingleObject.revision,
        state: MetkaJS.SingleObject.state,
        dataConfigurations: MetkaJS.JSConfig
    };
});
