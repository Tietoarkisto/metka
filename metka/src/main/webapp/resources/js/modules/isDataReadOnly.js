define(function (require) {
    'use strict';

    return function (data, isStudyDraft) {
        if(!data || !data.state) {
            return false;
        }

        if(!data.state.uiState === 'DRAFT' || !(data.state.handler === MetkaJS.User.userName)) {
            return true;
        }

        if(typeof isStudyDraft !== 'undefined') {
            return !isStudyDraft
        }

        return false;
    };
});
