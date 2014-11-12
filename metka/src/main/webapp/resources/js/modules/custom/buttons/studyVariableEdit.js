define(function (require) {
    'use strict';

    return function(options) {
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        require('./../../buttons').EDIT.call(this, options);
    };
});