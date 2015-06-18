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

    return {
        create: require('./inherit')(function (options) {
            var togglable = require('./togglable');
            var id = require('./autoId')();

            return {
                title: togglable.call($('<li>')
                    .data('hidePageButtons', !!options.hidePageButtons), options)
                    .append($('<a data-target="#' + id + '" href="javascript:void 0;" data-toggle="tab">')
                        .text(MetkaJS.L10N.localize(options, 'title'))

                        // set tab content on first open
                        .one('shown.bs.tab', function (e) {

                            // allow browser to activate and display tab, before starting to set content
                            setTimeout(function () {

                                // set content
                                require('./container').call($($(this).data('target')), options);
                            }.bind(this), 0);
                        })),
                content: togglable.call($('<div class="tab-pane">'), options)
                    .attr('id', id)
            };
        }),
        add: function (tabs) {
            function activate($li) {
                if (!$li.hasClass('containerHidden')) {
                    $li.children('a').tab('show');
                    return true;
                }
                return false;
            }
            var $tabContent = $('<div class="tab-content">');

            // TODO: add Array.prototype.transform or something
            // Tabs is an array [{title: <title>, content: <content>},...].
            // Make it an object {title: [<titles>], content: [<content>]}.
            tabs = tabs.reduce(function (o, v) {
                $.each(o, function (k) {
                    o[k].push(v[k]);
                });
                return o;
            }, {
                title: [],
                content: []
            });

            var $navTabs = $('<ul class="nav nav-tabs">')
                .append(tabs.title)
                .on('shown.bs.tab', 'a', function (e) {
                    var $li = $(this).parent();
                    $('body > .wrapper > .content > .modal-footer').children().toggleClass('hiddenByTab', $li.data('hidePageButtons'));
                    sessionStorage.setItem('currentTab', $li.index());
                });

            $tabContent.append(tabs.content);
            this
                .append($navTabs)
                .append($('<div class="panel-body">')
                    .append($tabContent));

            setTimeout(function () {
                // try to activate last tab from session
                var currentTab = sessionStorage.getItem('currentTab');
                if (currentTab) {
                    var $li = $navTabs.children().eq(currentTab);
                    if ($li.length) {
                        if (activate($li)) {
                            // tab found, break
                            return;
                        }
                    }
                }

                // activate first visible tab
                $navTabs.find('li').each(function () {
                    var $li = $(this);
                    if (activate($li)) {
                        return false; // break (note: jQuery each loop)
                    }
                });
            }, 0);
        }
    };
});