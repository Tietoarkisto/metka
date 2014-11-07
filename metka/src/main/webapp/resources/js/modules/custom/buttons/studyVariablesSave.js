define(function (require) {
    'use strict';

    return function(options) {
        options.hide = !require('./../../root')(options).isRelatedStudyDraftForCurrentUser;
        require('./../../buttons').SAVE.call(this, options);
    };
});