define(function (require) {
    'use strict';

    return {
        create: require('./inherit')(function (options) {
            var $section = $('<div class="panel">');
            require('./togglable').call($section, options);

            var id = require('./autoId')();

            return $section
                .addClass(options.important ? 'panel-warning' : 'panel-default')
                .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading accordionTitle2">')
                    .toggleClass('collapsed', options.defaultState !== 'OPEN')
                    .append($('<h4 class="panel-title">')
                        .append($('<a href="javascript:void 0;">')
                            .text(MetkaJS.L10N.localize(options, 'title')))))
                .append($('<div id="' + id + '" class="panel-collapse collapse">')
                    .toggleClass('in', options.defaultState === 'OPEN')
                    .append(require('./container').call($('<div class="panel-body">'), options)));
        }),
        add: function ($sections) {
            this.append($('<div class="panel-group">')
                .append($sections));
        }
    };
});
