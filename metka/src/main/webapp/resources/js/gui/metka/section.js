(function () {
    'use strict';

    $.widget('metka.metkaSection', $.metka.metka, {
        defaultElement: '<div class="panel">',
        _create: function () {
            this._super();
            this.togglable();

            this.element
                .addClass(this.options.important ? 'panel-warning' : 'panel-default');
        }
    });

    $.widget('metka.metkaSectionBody', $.metka.metka, {
        defaultElement: '<div class="panel-body">',
        _create: function () {
            this._super();
            this.container();
        }
    });

    $.metka.metka.prototype.addHandler(MetkaJS.E.Container.SECTION, {
        create: function () {
            return function (config) {
                var id = GUI.id();
                return $.metka.metkaSection(config).element
                    .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading accordionTitle2">')
                        .toggleClass('collapsed', config.defaultState !== 'OPEN')
                        .append($('<h4 class="panel-title">')
                            .append($('<a href="javascript:void 0;">')
                                .text(MetkaJS.L10N.localize(config, 'title')))))
                    .append($('<div id="' + id + '" class="panel-collapse collapse">')
                        .toggleClass('in', config.defaultState === 'OPEN')
                        .append(this.child('metkaSectionBody', config)));
            };
        },
        add: function (sections) {
            this.element.append($('<div class="panel-group">')
                .append(sections));
        }
    });
})();
