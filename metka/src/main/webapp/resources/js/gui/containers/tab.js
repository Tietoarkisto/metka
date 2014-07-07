(function () {
    'use strict';

    $.metka.addContainerType(MetkaJS.E.Container.TAB, {
        create: function () {
            return function (content) {
                var options = {
                    id: $.metka.metka.prototype.autoId()
                };
                return {
                    title: this.children('metkaTabTitle')(content, options),
                    content: this.children('metkaTabContent')(content, options)
                };
            };
        },
        add: function (tabs) {
            // TODO: add Array.transform or something
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
                    sessionStorage.setItem('currentTab2', $(this).data('target').substring(1));
                });

            this.element
                .append($navTabs)
                .append($('<div class="panel-body">')
                    .append($('<div class="tab-content">').append(tabs.content)));

            // try to activate last tab from session
            var currentTab = sessionStorage.getItem('currentTab2');
            if (currentTab) {
                var $a = $navTabs.find('a[data-target="#' + currentTab + '"]');
                if ($a.length && !$a.parent().hasClass('containerHidden')) {
                    $a.tab('show');
                    return; // tab found, break
                }
            }

            // activate first visible tab
            $navTabs.find('a').each(function () {
                var $this = $(this);
                if (!$this.parent().hasClass('containerHidden')) {
                    $this.tab('show');
                    return false; // break
                }
            });
        }
    });
})();