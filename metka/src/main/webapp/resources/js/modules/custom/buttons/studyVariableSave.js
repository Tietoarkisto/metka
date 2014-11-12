define(function (require) {
    'use strict';

    return function(options) {
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        this.click(require('./../../save')(options, function (response) {
            log(response);
            if(response.result === "SAVE_SUCCESSFUL_WITH_ERRORS") {
                $.extend(options.data, response.data);
                $(this).trigger('refresh.metka');
            } else {
                log("trying to dismiss", options);
                //$(options.modalTarget).modal('hide');
                $('#'+options.modalTarget).modal('hide');
            }
        }));
    };
});