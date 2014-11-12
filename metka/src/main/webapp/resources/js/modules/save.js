define(function (require) {
    'use strict';

    return function (options, success) {
        return require('./formAction')('save')(options, function (response) {
                /*if (response.result === 'NO_CHANGES_TO_SAVE') {
                    return;
                }*/
                success.call(this, response);
            },
            [
                'SAVE_SUCCESSFUL',
                'SAVE_SUCCESSFUL_WITH_ERRORS',
                'NO_CHANGES_TO_SAVE'
            ]);
    };
});
