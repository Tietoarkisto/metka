define(function (require) {
    'use strict';

    var togglable = require('./togglable');
    var $tabContent = $('<div class="tab-content">');

    return {
        create: require('./inherit')(function (options) {
            var id = require('./autoId')();

            return {
                title: togglable.call($('<li>'), options)
                    .append($('<a data-target="#' + id + '" href="javascript:void 0;" data-toggle="tab">')
                        .text(MetkaJS.L10N.localize(options, 'title'))

                        // set tab content on first open
                        .one('shown.bs.tab', function (event) {
                            var $tab = $tabContent.children('.active');

                            // allow bootstrap to activate and display tab, before starting to set content
                            setTimeout(function () {

                                // set content
                                require('./container').call($tab, options)
                            }, 0);
                        })),
                content: togglable.call($('<div class="tab-pane">'), options)
                    .attr('id', id)
            };
        }),
        add: function (tabs) {
            function activate($a, id) {
                var $li = $a.parent();
                if (!$li.hasClass('containerHidden')) {
                    $a.tab('show');
                    //$li.addClass('active');
                    $tabContent.find(id).addClass('active');
                    return true;
                }
                return false;
            }

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
                    sessionStorage.setItem('currentTab', $(this).data('target').substring(1));
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
                    var $a = $navTabs.find('a[data-target="#' + currentTab + '"]');
                    if ($a.length) {
                        if (activate($a, '#' + currentTab)) {
                            // tab found, break
                            return;
                        }
                    }
                }

                // activate first visible tab
                $navTabs.find('a').each(function () {
                    var $a = $(this);
                    if (activate($a, $a.data('target'))) {
                        return false; // break (note: jQuery each loop)
                    }
                });
            }, 0);
        }
    };
});