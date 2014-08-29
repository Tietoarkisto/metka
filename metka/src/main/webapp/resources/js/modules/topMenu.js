define(function (require) {
    'use strict';

    var metka = require('./../metka');

    var items = [{
        ct: 'DESKTOP',
        href: 'desktop',
        text: 'desktop'
    }, {
        ct: 'EXPERTSEARCH',
        href: 'expertSearch',
        text: 'expert'
    }, {
        ct: 'STUDY',
        href: 'revision/search/study',
        text: 'study'
    }, {
        ct: 'STUDY_VARIABLES',
        href: 'revision/search/study_variables',
        text: 'variables'
    }, {
        ct: 'PUBLICATION',
        href: 'revision/search/publication',
        text: 'publication'
    }, {
        ct: 'SERIES',
        href: 'revision/search/series',
        text: 'series'
    }, {
        ct: 'BINDER',
        href: 'binder/all',
        text: 'binder'
    }, {
        ct: 'REPORT',
        href: 'report/all',
        text: 'report'
    }];
    if (MetkaJS.User.role.canViewSettingsPage) {
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
                                    href: metka.contextPath + '/' + li.href
                                })
                                    .text(MetkaJS.L10N.get('topmenu.' + li.text)));

                            if (metka.PAGE === li.ct) {
                                $li.addClass('active');
                            }
                            return $li;
                        })))
                        .append([
                            '<div class="col-sm-4">',
                                '<div class="row">',
                                    '<div class="col-md-12" style="padding-top: 20px;">',
                                        '<a href="#" class="navbar-link pull-right">Kirjaudu ulos</a>',
                                    '</div>',
                                '</div>',
                                '<div class="row">',
                                    '<div class="text-right col-sm-4 navbar-text" style="margin-left: 0px; margin-right: 0px; margin-bottom: 0px;">',
                                    '</div>',
                                    '<div class="col-sm-8">',
                                        '<div class="row">',
                                            '<form class="navbar-form">',
                                                '<div class="input-group">',
                                                    '<input type="text" class="form-control" autocomplete="on" placeholder="Aineistonumero">',
                                                    '<div class="input-group-btn">',
                                                        '<button class="btn btn-primary" type="submit">Hae</button>',
                                                    '</div>',
                                                '</div>',
                                            '</form>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>'
                    ].join('')))));

    $('.navbar-form').submit(function () {
        function error() {
            require('./assignUrl')('/expertSearch');
        }
        require('./server')('/revision/studyIdSearch/{id}', {
            id: $(this).find('input[type="text"]').val()
        }, {
            success: function (data) {
                if (!data.rows.length) {
                    error();
                    return;
                }

                require('./assignUrl')('view', {
                    page: 'study',
                    id: data.rows[0].id,
                    no: data.rows[0].revision
                });
            },
            error: error
        });
        return false;
    });
});