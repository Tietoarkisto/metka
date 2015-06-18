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

    var metka = require('./../metka');
    require('./topMenu');

    require('./uiLocalization');
    document.title = MetkaJS.L10N.get('page.title');

    var options = require('./optionsBase')();

    var getPropertyNS = require('./utils/getPropertyNS');

    function refreshMetka() {
        if(!content) {
            return true;
        }
        metka.id = getPropertyNS(options, 'data.key.id');
        metka.no = getPropertyNS(options, 'data.key.no');

        // (re-)render page
        content.empty();
        content.append(require('./header')(options.header));
        require('./container').call(content, options);
        require('./buttonContainer').call(content, options);

        //$(window).off('beforeunload', require('./onBeforeUnload'));
        return false;
    }
    var content = null;
    require('./pageConfig')(options, function () {
        options.$events.on('refresh.metka', refreshMetka);
        $('body')
            .append($('<div class="wrapper">')
                .append($('<div class="content container">')));

        content = $('.content.container');
        options.$events.trigger('refresh.metka');
                    // Inner elements may refresh page by triggering 'refresh.metka' event.
                    // If event is not captured before it propagates here, page will be re-rendered.
                    //.on('refresh.metka', refreshMetka)
                    /*.on('refresh.metka', function () {
                        log('refresh.metka');
                        metka.id = getPropertyNS(options, 'data.key.id');
                        metka.no = getPropertyNS(options, 'data.key.no');

                        // (re-)render page
                        var $this = $(this)
                            .empty()
                            .append(require('./header')(options.header));
                        require('./container').call($this, options);
                        require('./buttonContainer').call($this, options);

                        //$(window).off('beforeunload', require('./onBeforeUnload'));
                        return false;
                    })*/
                    // trigger once here immediately
                    //.trigger('refresh.metka')));
    });
});
