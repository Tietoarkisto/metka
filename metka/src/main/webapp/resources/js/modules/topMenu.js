define(function (require) {
    'use strict';

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
            href: 'binder',
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
        require('./server')('/revision/studyIdSearch/{id}', {
            id: $(this).find('input[type="text"]').val()
        }, {
            method: 'GET',
            success: function (data) {
                if (!data.rows.length) {
                    error();
                    return;
                }

                require('./assignUrl')('view', {
                    PAGE: 'STUDY',
                    id: data.rows[0].id,
                    no: data.rows[0].revision
                });
            },
            error: error
        });
        return false;
    });
});