define(function (require) {
    'use strict';

    return function(options) {
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        this.click(require('./../../save')(options, function (response) {
            if(response.result === "SAVE_SUCCESSFUL_WITH_ERRORS") {
                $.extend(options.data, response.data);
                $(this).trigger('refresh.metka');
            } else {
                $('#'+options.modalTarget).modal('hide');
            }
        }));
    };
});