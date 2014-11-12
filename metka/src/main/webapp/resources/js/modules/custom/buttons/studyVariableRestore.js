define(function (require) {
    'use strict';

    return function(options) {
        // TODO: For restore to work it has to be tied to the "removed" status of target study, not its draft status or handler
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        require('./../../buttons').RESTORE.call(this, options);
    };
});