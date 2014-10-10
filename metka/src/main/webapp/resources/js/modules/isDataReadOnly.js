define(function (require) {
    'use strict';

    return function (data) {
        return !data.state.uiState === 'DRAFT' || !(data.state.handler === MetkaJS.User.userName);
    };
});
