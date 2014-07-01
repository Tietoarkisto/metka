(function () {
    'use strict';

    $.widget('metka.metkaSection', $.metka.metka, {
        defaultElement: '<div class="panel">',
        _create: function () {
            this._super();
            this.togglable();

            var id = GUI.id();

            this.element
                .addClass(this.options.important ? 'panel-warning' : 'panel-default')
                .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading accordionTitle2">')
                    .toggleClass('collapsed', this.options.defaultState !== 'OPEN')
                    .append($('<h4 class="panel-title">')
                        .append($('<a href="javascript:void 0;">')
                            .text(MetkaJS.L10N.localize(this.options, 'title')))))
                .append($('<div id="' + id + '" class="panel-collapse collapse">')
                    .toggleClass('in', this.options.defaultState === 'OPEN')
                    .append($('<div class="panel-body">')
                        .metkaContainer(this.options)));
        }
    });
})();
