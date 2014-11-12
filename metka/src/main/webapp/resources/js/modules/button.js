define(function (require) {
    'use strict';

    function isHandler(options) {
        return options.data.state.handler === MetkaJS.User.userName;
    }

    var buttons = require('./buttons');

    return require('./inherit')(function (options) {
        function isVisible() {
            /*if(options.hide) {
                return false;
            }*/
            if (options.data && options.data.state) {
                if (options.data.state.uiState === 'DRAFT' && options.hasOwnProperty('hasHandler') && options.hasHandler !== null) {
                    if (!!options.hasHandler !== !!options.data.state.handler) {
                        return false;
                    }
                }
                if (options.data.state.uiState === 'DRAFT' && options.hasOwnProperty('isHandler') && options.isHandler !== null) {
                    if (options.isHandler !== (options.data.state.handler === MetkaJS.User.userName)) {
                        return false;
                    }
                }
                if (options.states && options.states.length) {
                    // if every state mismatch
                    if (options.states.every(function (state) {
                        return options.data.state.uiState !== state;
                    })) {
                        return false;
                    }
                }
            }

            return require('./hasEveryPermission')(options.permissions);
        }

        options = options || {};

        var $button = $('<button type="button" class="btn">');

        if (options.type && buttons[options.type]) {
            buttons[options.type].call($button, options);
        }

        $button
            .addClass('btn-' + (options.style || 'primary'))
            .toggleClass('hiddenByButtonConfiguration', !isVisible());

        $button
            .text(MetkaJS.L10N.localize(options, 'title'));

        if (options.create) {
            options.create.call($button, options);
        }

        return $button;
    });
});
