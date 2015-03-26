define(function(require) {
    'use strict';

    return function(options) {
        // No options, never in read only state by default
        if(!options) {
            return false;
        }

        // If we have data and data has state and state has uiState check state
        // If need be we can move this back to it's own function so it's usable by other read only code as well
        if(options.data && options.data.state && options.data.state.uiState) {
            // If data state is not DRAFT or if data state handler is someone else than current user we're in ReadOnly state
            if(!options.data.state.uiState === 'DRAFT' || !(options.data.state.handler === MetkaJS.User.userName)) {
                return true;
            }
        }

        // If options readOnly has been set to true we're in read only state
        if(!!options.readOnly) {
            return true;
        }

        // At this point, if the hierarchy stops i.e. there's no parent then we're not in read only state
        if(!options.parent) {
            return false;
        }

        // If we have parent then return the read only state of the parent
        return options.isReadOnly(options.parent);
    }
});