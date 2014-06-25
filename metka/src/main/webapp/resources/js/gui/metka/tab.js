(function () {
    'use strict';

    $.widget('metka.metkaTabTitle', $.metka.metka, {
        defaultElement: '<li>',
        _create: function () {
            this._super();
            this.togglable();

            this.element
                .append($('<a data-target="#' + this.options.id + '" href="javascript:void 0;" data-toggle="tab">')
                    .text(MetkaJS.L10N.localize(this.options, 'title')));
        }
    });

    $.widget('metka.metkaTabContent', $.metka.metka, {
        defaultElement: '<div class="tab-pane">',
        _create: function () {
            this._super();
            this.togglable();
            this.container();
            this.element
                .attr('id', this.options.id);
        }
    });

    $.metka.metka.prototype.addHandler(MetkaJS.E.Container.TAB, {
        create: function () {
            return function (content) {
                var options = {
                    id: GUI.id()
                };
                return {
                    title: this.children('metkaTabTitle')(content, options),
                    content: this.children('metkaTabContent')(content, options)
                };
            };
        },
        add: function (tabs) {
            // TODO: add Array.transform or something
            // tabs is array of objects. make it object which has arrays as properties
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