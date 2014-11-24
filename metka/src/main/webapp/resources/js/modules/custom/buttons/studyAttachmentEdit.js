define(function (require) {
    'use strict';

    return function(options) {
        $(this).toggleClass('hiddenByCustomCode', !require('./../../root')(options).isRelatedStudyDraftForCurrentUser);
        this.click(require('./../../formAction')('edit')(options, function (response) {
            $.extend(options.data, response.data);
            require('../../isRelatedStudyDraftForCurrentUser')(options, function (isDraft) {
                options.isRelatedStudyDraftForCurrentUser = isDraft;
                options.readOnly = require('./../../isDataReadOnly')(response.transferData, isDraft);
                options.type = options.readOnly ? 'VIEW' : 'MODIFY';
                $(this).trigger('refresh.metka');
            });
        }, [
            'REVISION_FOUND',
            'REVISION_CREATED'
        ]));
    };
});