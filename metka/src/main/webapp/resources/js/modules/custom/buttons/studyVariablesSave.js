define(function (require) {
    'use strict';

    return function(options) {
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        require('./../../buttons').SAVE.call(this, options);
    };
});