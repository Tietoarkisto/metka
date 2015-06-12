define(function (require) {
    'use strict';

    return function (options, success) {
        return require('./formAction')('save')(options, function (response) {
                success.call(this, response);
            },
            [
                'OPERATION_SUCCESSFUL',
                'OPERATION_SUCCESSFUL_WITH_ERRORS',
                'NO_CHANGES'
            ], "save");
    };
});
