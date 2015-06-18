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

    function show($section) {
        return $section
            .removeClass($section.data('options').important ? 'panel-warning' : 'panel-default')
            .addClass('panel-primary');
    }

    function hide($section) {
        return $section
            .removeClass('panel-primary')
            .addClass($section.data('options').important ? 'panel-warning' : 'panel-default');
    }

    return {
        create: require('./inherit')(function (options) {
            var id = require('./autoId')();

            var $section = $('<div class="panel">')
                .data('options', options)
                .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading">')
                    .toggleClass('collapsed', options.defaultState !== 'OPEN')
                    .append($('<h4 class="panel-title">')
                        .append($('<a href="javascript:void 0;">')
                            .text(MetkaJS.L10N.localize(options, 'title')))))
                .append($('<div id="' + id + '" class="panel-collapse collapse">')
                    .toggleClass('bg-warning', !!options.important)
                    .toggleClass('in', options.defaultState === 'OPEN')
                    .append(require('./container').call($('<div class="panel-body">'), options)));

            require('./togglable').call($section, options);

            return hide($section);
        }),
        add: function ($sections) {
            this.append($('<div class="panel-group">')
                .append($sections)
                .on('hide.bs.collapse', '.panel', function () {
                    var $this = $(this);
                    if (!$this.is(':hover')) {
                        hide($this);
                    }
                })
                .on('show.bs.collapse', '.panel', function () {
                    show($(this));
                })
                .on('mouseenter', '.panel>.collapsed', function () {
                    show($(this).parent());
                })
                .on('mouseleave', '.panel>.collapsed', function () {
                    hide($(this).parent());
                }));
        }
    };
});
