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
            var $body = $(this);

            return {
                title: togglable.call(
                    $('<li>').data('hidePageButtons', !!options.hidePageButtons)
                    , options
                ).append(
                    $('<a data-target="#' + id + '" href="javascript:void 0;" data-toggle="tab">')
                        .text(MetkaJS.L10N.localize(options, 'title'))

                        // set tab content on first open
                        .one('show.bs.tab', function (e) {
                            require('./container').call($body.find($(this).data('target')), options);
                        })
                    ),
                content: togglable.call($('<div class="tab-pane">'), options)
                    .attr('id', id)
            };
        }),
        add: function (tabs) {
            function activate($li) {
                if (!$li.hasClass('containerHidden')) {
                    $li.children('a').tab('show');
                    $tabContent.find($li.children('a').data('target')).addClass('active');
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
            // Issue #404
            var $button = require('./button')()({
                style: 'default',
                title: MetkaJS.L10N.get('+'),
                create: function () {
                    this
                        .click(function () {
                            $('div.panel-heading').attr('aria-expanded', true);
                            $('div.panel-collapse').addClass('in').attr('aria-expanded', true);
                            $('#expandbutton').hide();
                            $('#collapsebutton').show();
                        });
                }
            });
            $button.addClass('btn-sm').css('background-color', '#009ee0').css('color', 'white');
            $button.attr('id', 'expandbutton');

            var $button2 = require('./button')()({
                style: 'default',
                title: MetkaJS.L10N.get('-'),
                create: function () {
                    this
                        .click(function () {
                            $('div.panel-heading').attr('aria-expanded', false);
                            $('div.panel-collapse').removeClass('in').attr('aria-expanded', false);
                            $('div.panel').removeClass('panel-primary');
                            $('div.panel').addClass('panel-default');
                            $('#expandbutton').show();
                            $('#collapsebutton').hide();
                        });
                }
            });
            $button2.addClass('btn-sm').css('background-color', '#009ee0').css('color', 'white');
            $button2.attr('id', 'collapsebutton');
            $button2.hide();

            if(tabs.content[2] && tabs.title.length === 8) {
                tabs.content[2].append($button).append($button2);
            }
            $tabContent.append(tabs.content);

            this
                .append($navTabs)
                .append($('<div class="panel-body">')
                    .append($tabContent));

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
                if (activate($(this))) {
                    return false; // break (note: jQuery each loop)
                }
            });
        }
    };
});