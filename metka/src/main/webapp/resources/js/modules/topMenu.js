define(function (require) {
    'use strict';

    var metka = require('./../metka');

    $('body')
        .prepend($('<header class="navbar navbar-default navbar-static-top">')
            .append($('<div class="container">')
                .append($('<nav>')
                    .append($('<ul class="nav navbar-nav nav navbar-nav navbar-left">')
                        .append([{
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
                            ct: 'VARIABLES',
                            href: 'revision/search/variables',
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
                        }, {
                            ct: 'SETTINGS',
                            href: 'settings',
                            text: 'settings'
                        }].map(function (li) {
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
                            '<div class="col-sm-3 col-md-3 pull-right">',
                                '<div class="row">',
                                    '<div class="col-md-12">',
                                        '<div class="row">',
                                            '<div class="col-md-12" style="padding-top: 30px; padding-right: 30px;">',
                                                '<a href="#" class="navbar-link pull-right">Kirjaudu ulos</a>',
                                            '</div>',
                                        '</div>',
                                        '<div class="row">',
                                            '<div class="col-md-12">',
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
                                '</div>',
                            '</div>'/*,

                            '<div class="languageSelect">',
                                '<input type="radio" name="language" value="fi"/>fi',
                                '<input type="radio" name="language" value="en"/>en',
                                '<input type="radio" name="language" value="sv"/>sv',
                            '</div>'*/
                    ].join('')))));

    $('.navbar-form').submit(function () {
        require('./server')('/todo-get-study-by-id-url{id}', {
            id: $(this).find('input[type="text"]').val()
        }, {
            method: 'GET',
            success: function (data) {
                log('todo: navigate to study...');
            },
            error: function () {
                require('./assignUrl')('/expertSearch');
            }
        });
        return false;
    });
});