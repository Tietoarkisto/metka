define(function (require) {
    'use strict';

    return function (options, callback) {
        require('./server')('viewAjax', {
            PAGE: 'STUDY',
            id: require('./data')(options)('study').getByLang(options.defaultLang),
            no: ''
        }, {
            method: 'GET',
            success: function (response) {
                callback(response.result === 'VIEW_SUCCESSFUL' && response.transferData.state.uiState === 'DRAFT' && MetkaJS.User.userName === response.transferData.state.handler);
            },
            error: function () {
                callback(false);
            }
        });
    };
});
