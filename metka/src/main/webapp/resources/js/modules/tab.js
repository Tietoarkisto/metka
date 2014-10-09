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