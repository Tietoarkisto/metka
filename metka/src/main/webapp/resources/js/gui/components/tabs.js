(function() {
    'use strict';
    GUI.Components.tabs = function ($container, content, readOnly, buildContainers) {
        var $navTabs = $('<ul class="nav nav-tabs">');
        var $tabContent = $('<div class="tab-content">');
        content.forEach(function (container) {
            var id = GUI.id();
            $navTabs.append($('<li>')
                .append($('<a data-target="#' + id + '" href="javascript:void 0;" data-toggle="tab">')
                    .text(MetkaJS.L10N.localize(container, 'title')))
                .toggleClass('containerHidden', container.hidden));
            $tabContent.append(buildContainers(
                $('<div class="tab-pane" id="' + id + '">')
                    .toggleClass('containerHidden', container.hidden),
                container, readOnly || container.readOnly));
        });

        if ($navTabs.children().length) {
            $container
                .append($navTabs)
                .append($('<div class="panel-body">')
                    .append($tabContent));

            $navTabs.on('shown.bs.tab', 'a', function (e) {
                sessionStorage.setItem('currentTab2', $(this).data('target').substring(1));
            });

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
    }
}());