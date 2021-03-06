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

define(function(require) {
    'use strict';

    var waitDialog = $(''
        + '<div class="modal hide" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false">'
            + '<div class="modal-header">'
                + '<h1>'+MetkaJS.L10N.get("dialog.waitDialog.title")+'</h1>'
            + '</div>'
            + '<div class="modal-body">'
                + '<div class="progress progress-striped active">'
                    + '<div class="bar" style="width: 100%;"></div>'
                + '</div>'
            + '</div>'
        + '</div>');

    var options = function() {
        return {
            disableClose: true,
            disableFooter: true,
            keyboard: false,
            title: "dialog.waitDialog.title",
            body: $('<div class="progress progress-striped active"><div class="progress-bar" role="progressbar" style="width: 100%;"></div></div>')
        }
    };

    return function () {
        var curOptions = options();
        return {
            showWait: function() {
                require('./modal')(curOptions);
                //waitDialog.modal();
            },
            hideWait: function () {
                setTimeout(function() {
                    $('#'+curOptions.modalTarget).modal('hide');
                }, 250);
                //waitDialog.modal('hide');
            }
        }
    };
});