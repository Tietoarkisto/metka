/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function (require) {
    'use strict';

    var buttons = require('./buttons');

    function finalizeButton(options, $button) {
        if(buttons[options.type]) {
            buttons[options.type].call($button, options);
        }

        if (options.create) {
            options.create.call($button, options);
        }

        $button.addClass('btn-' + (options.style || 'primary'));

        if(!!options.isHandledByUser && options.isHandledByUser.length > 0) {
            require('./isHandledByUser')(options, options.isHandledByUser, function(isHandledByUser) {
                if(isHandledByUser) {
                    $button.toggleClass('hiddenByButtonConfiguration', !isVisible(options));
                    // Enable allowed buttons
                    $button.prop("disabled", "")
                } else {
                    $button.toggleClass('hiddenByButtonConfiguration', true);
                }
            });
        } else {
            $button.toggleClass('hiddenByButtonConfiguration', !isVisible(options));
            // Enable allowed buttons
            $button.prop('disabled', isVisible(options) ? '' : 'disabled')
        }
        if(options.html) {
            $button.html(options.html);
        } else {
            $button.text(MetkaJS.L10N.localize(options, 'title'));
        }
    }

    function isVisible(options) {
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

        if(!require('./hasEveryPermission')(options.permissions)) {
            return false;
        }

        return !options.forceHide;
    }

    function isHandler(options) {
        return options.data.state.handler === MetkaJS.User.userName;
    }

    return require('./inherit')(function (options) {

        options = options || {};

        var $button = $('<button type="button" class="btn">');
        // Disabled buttons in case something takes too long
        $button.prop('disabled', 'disabled');

        if(options.type && options.type === 'CUSTOM' && options.customHandler) {
            // If there is a custom handler then prevent dismiss. Each custom handler should decide for itself if it needs to close a dialog or not.
            $.extend(true, options, {
                preventDismiss: true
            });
            require(['./custom/buttons/'+options.customHandler], function(customHandler) {
                switch (typeof customHandler) {
                    case 'object':
                        $.extend(true, options, customHandler);
                        break;
                    case 'function':
                        customHandler.call(this, options);
                        break;
                }
                finalizeButton(options, this);
            }.bind($button));
        } else {
            finalizeButton(options, $button);
        }
        return $button;
    });
});
