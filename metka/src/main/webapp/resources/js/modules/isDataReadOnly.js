define(function (require) {
    'use strict';

    return function (data) {
        return !data || !data.state || !data.state.uiState === 'DRAFT' || !(data.state.handler === MetkaJS.User.userName);
    };
});
