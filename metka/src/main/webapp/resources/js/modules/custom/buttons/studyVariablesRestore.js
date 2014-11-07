define(function (require) {
    'use strict';

    return function(options) {
        options.preventDismiss = true;
        options.hide = !require('./../../root')(options).isRelatedStudyDraftForCurrentUser;
        require('./../../buttons').RESTORE.call(this, options);
    };
});