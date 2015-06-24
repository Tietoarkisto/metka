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

    var resultParser = require('./resultParser');

    var metka = require('./../metka');

    var items = [
        /*{
            ct: 'DESKTOP',
            href: 'desktop',
            text: 'desktop'
        }, {
            ct: 'REPORT',
            href: 'report/all',
            text: 'report'
        }*/
    ];
    if (MetkaJS.User.role.permissions.hasMinimumPermission) {
        items.push({
            ct: 'EXPERT',
            href: 'expert',
            text: 'expert'
        });
    }
    if (MetkaJS.User.role.permissions.hasMinimumPermission) {
        items.push({
            ct: 'STUDY',
            href: 'revision/search/STUDY',
            text: 'study'
        });
    }
    if (MetkaJS.User.role.permissions.hasMinimumPermission) {
        items.push({
            ct: 'STUDY_VARIABLES',
            href: 'revision/search/STUDY_VARIABLES',
            text: 'variables'
        });
    }
    if (MetkaJS.User.role.permissions.hasMinimumPermission) {
        items.push({
            ct: 'PUBLICATION',
            href: 'revision/search/PUBLICATION',
            text: 'publication'
        });
    }
    if (MetkaJS.User.role.permissions.hasMinimumPermission) {
        items.push({
            ct: 'SERIES',
            href: 'revision/search/SERIES',
            text: 'series'
        });
    }
    if (MetkaJS.User.role.permissions.canViewBinderPages) {
        items.push({
            ct: 'BINDER',
            href: 'revision/search/BINDER_PAGE',
            //href: 'binder',
            text: 'binder'
        });
    }
    if (MetkaJS.User.role.permissions.canViewSettingsPage) {
        items.push({
            ct: 'SETTINGS',
            href: 'settings',
            text: 'settings'
        });
    }

    $('body')
        .prepend($('<header class="navbar navbar-default navbar-static-top">')
            .append($('<div class="container">')
                .append($('<nav class="row">')
                    .append($('<ul class="col-sm-8 nav navbar-nav nav navbar-nav navbar-left">')
                        .append(items.map(function (li) {
                            var $li = $('<li>')
                                .append($('<a>', {
                                    href: require('./url')('/' + li.href)
                                })
                                    .text(MetkaJS.L10N.get('topmenu.' + li.text)));

                            if (metka.PAGE === li.ct) {
                                $li.addClass('active');
                            }
                            return $li;
                        })))
                        .append($('<div class="col-sm-4">')
                            .append($('<div class="row">')
                                .append($('<div class="col-md-12" style="padding-top: 20px;">')
                                    .append($('<span class="pull-right">')
                                        .text(MetkaJS.User.displayName + ' | ')
                                        .append($('<a>', {
                                            target: '_blank',
                                            href: MetkaJS.contextPath + '/html/guide/guide.html'
                                        }).text(MetkaJS.L10N.get('topmenu.help')), ' | ', $('<a>', {
                                            href: "https://"+window.location.hostname+"/Shibboleth.sso/Logout"
                                        }).text(MetkaJS.L10N.get("topmenu.logout")))
                                )),
                                ['<div class="row">',
                                    '<div class="text-right col-sm-4 navbar-text" style="margin-left: 0px; margin-right: 0px; margin-bottom: 0px;">',
                                    '</div>',
                                    '<div class="col-sm-8">',
                                        '<div class="row">', '<form class="navbar-form">',
                                    '<div class="input-group">',
                                                    '<input type="text" class="form-control" autocomplete="on" placeholder="Aineistonumero">',
                                                    '<div class="input-group-btn">',
                                                        '<button class="btn btn-primary" type="submit">Hae</button>',
                                                    '</div>',
                                                '</div>',
                                            '</form>',
                                        '</div>',
                                    '</div>',
                                '</div>'].join(''))))));

    $('.navbar-form').submit(function () {
        function error() {
            require('./assignUrl')('/expert');
        }
        var $id = $(this).find('input[type="text"]').val();

        require('./server')('searchAjax', {
            data: JSON.stringify({
                searchApproved: true,
                searchDraft: true,
                searchRemoved: true,
                values: {
                    studyid: $id,
                    'key.configuration.type': "STUDY"
                }
            }),
            success: function(response) {
                if(resultParser(response.result).getResult() !== 'OPERATION_SUCCESSFUL' || !response.rows.length || response.rows.length == 0) {
                    error();
                } else {
                    require('./assignUrl')('view', {
                        PAGE: 'STUDY',
                        id: response.rows[0].id,
                        no: ''
                    });
                }
            }
        });

        return false;
    });
});