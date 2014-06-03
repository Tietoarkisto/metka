(function() {
    'use strict';
    GUI.Components.sections = function ($container, content, readOnly, buildContainers) {
        $('<div class="panel-group">')
            .eachTo(content, function (i, container) {
                var id = GUI.id();
                this.append($('<div class="panel">')
                    .addClass(container.important ? 'panel-warning' : 'panel-default')
                    .toggleClass('containerHidden', container.hidden)
                    .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading accordionTitle2">')
                        .toggleClass('collapsed', container.defaultState !== 'OPEN')
                        .append($('<h4 class="panel-title">')
                            .append($('<a href="javascript:void 0;">')
                                .text(MetkaJS.L10N.localize(container, 'title')))))
                    .append($('<div id="' + id + '" class="panel-collapse collapse">')
                        .toggleClass('in', container.defaultState === 'OPEN')
                        .append(buildContainers(
                            $('<div class="panel-body">'),
                            container, readOnly || container.readOnly))));
            })
            .me(function () {
                if (this.children().length) {
                    $container.append(this);
                }
            });
    }
}());