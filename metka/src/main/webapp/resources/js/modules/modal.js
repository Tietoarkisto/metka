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

    return function (options) {
        function getTitle(options) {
            if(options.dialogTitle && MetkaJS.L10N.containsText(options.dialogTitle, options.type)) {
                return MetkaJS.L10N.localize(options.dialogTitle, options.type);
            } else if(options.dialogTitles
                    && options.dialogTitles[options.containerKey]
                    && MetkaJS.L10N.containsText(options.dialogTitles[options.containerKey], options.type)) {
                return MetkaJS.L10N.localize(options.dialogTitles[options.containerKey], options.type);
            } else if(options.title) {
                return MetkaJS.L10N.get(options.title);
            } else {
                // Issue #769 workaround
                var newTitle = MetkaJS.L10N.localize(options, 'title');
                if (newTitle === '[title]') {
                    newTitle = '';
                }
                return newTitle;
            }
        }

        function refreshMetka() {
            if(!content) {
                return true;
            }
            if (!options.buttons) {
                options.buttons = [];
            } else {
                if (typeof options.buttons === 'string') {
                    options.buttons = [options.buttons];
                }
            }

            options.buttons.forEach(function(button, i, buttons) {
                if(typeof button === 'string') {
                    buttons[i] = {
                        type: button
                    }
                }
            });

            var $body = $('<div class="modal-body">');
            if (options.content && options.content.length > 0) {
                require('./container').call($body, options);
            } else if(options.body) {
                $body.append(options.body);
            }

            var $header = $('<div class="modal-header">')
                .append((options.disableClose ? null : '<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>'))
                .append($('<h4 class="modal-title">')
                    .text(getTitle(options)));

            // Issue #452
            var txtBox = $body.find('.form-control:first');
            setTimeout(function() {
                $(txtBox).focus();
            }, 600);

            content
                .empty()
                .toggleClass('modal-lg', !!options.large)
                .append($('<div class="modal-content">')
                    .append($header)
                    .append($body)
                    .append((options.disableFooter ? null : $('<div class="modal-footer">')
                        .append((options.buttons || []).map(function (buttonOptions) {
                            $.extend(true, buttonOptions, {
                                modalTarget: options.modalTarget
                            });
                            return require('./button')(options)(buttonOptions)
                                .if(!buttonOptions.preventDismiss, function () {
                                    // although some bootstrap features are accessible via .data method, this wont work
                                    // this.data('dismiss', 'modal');

                                    // default behaviour dismisses modal
                                    this.attr('data-dismiss', 'modal');
                                    this.addClass('focused');  // Issue #425
                                });
                        })))));

            if (options.translatableCurrentLang) {
                $header.append(require('./languageRadioInputGroup')(options, 'dialog-translation-lang', options.translatableCurrentLang));
            }
            return false;
        }

        var content = null;
        if(!options.$events) {
            options.$events = require('./events')();
        }

        options.modalTarget = require('./autoId')("M");
        options.$events.register('refresh.metka', refreshMetka);
        var $modal = $('<div class="modal fade" tabindex="-1" role="dialog" id="'+options.modalTarget+'">');
        content = $('<div class="modal-dialog">');
        options.$events.trigger('refresh.metka');
        $modal.append(content);
        // Issue #425
        var focusedButton = $modal.find('.focused');
        setTimeout(function() {
            $(focusedButton).focus();
        }, 500);
        /*$modal.on('show.bs.modal', function() {
            options.$events.trigger('refresh.metka');
        });*/
        var modalSettings = {
            backdrop: 'static',
            keyboard: options.keyboard
        };
        $modal.modal(modalSettings).on('hidden.bs.modal', function () {
            $(this).remove();

            // workaround to keep scrolling enabled for nested modals

            var $body = $('body');
            var $otherModal = $body.children('.modal');

            // if there are other modals on screen
            if ($otherModal.length) {
                // get instance of Bootstrap Modal object
                var bsModal = $otherModal.data('bs.modal');

                // do same things that Bootstrap does internally, when dialog is opened
                $body.addClass('modal-open');
                bsModal.setScrollbar();
            }
        });

        return $modal;
    };
});
