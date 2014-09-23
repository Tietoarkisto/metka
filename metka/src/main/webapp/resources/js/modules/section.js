define(function (require) {
    'use strict';

    function show($section) {
        return $section
            .removeClass($section.data('options').important ? 'panel-warning' : 'panel-default')
            .addClass('panel-primary');
    }

    function hide($section) {
        return $section
            .removeClass('panel-primary')
            .addClass($section.data('options').important ? 'panel-warning' : 'panel-default');
    }

    return {
        create: require('./inherit')(function (options) {
            var id = require('./autoId')();

            var $section = $('<div class="panel">')
                .data('options', options)
                .append($('<div data-toggle="collapse" data-target="#' + id + '" class="panel-heading">')
                    .toggleClass('collapsed', options.defaultState !== 'OPEN')
                    .append($('<h4 class="panel-title">')
                        .append($('<a href="javascript:void 0;">')
                            .text(MetkaJS.L10N.localize(options, 'title')))))
                .append($('<div id="' + id + '" class="panel-collapse collapse">')
                    .toggleClass('bg-warning', !!options.important)
                    .toggleClass('in', options.defaultState === 'OPEN')
                    .append(require('./container').call($('<div class="panel-body">'), options)));

            require('./togglable').call($section, options);

            return hide($section);
        }),
        add: function ($sections) {
            this.append($('<div class="panel-group">')
                .append($sections)
                .on('hide.bs.collapse', '.panel', function () {
                    var $this = $(this);
                    if (!$this.is(':hover')) {
                        hide($this);
                    }
                })
                .on('show.bs.collapse', '.panel', function () {
                    show($(this));
                })
                .on('mouseenter', '.panel>.collapsed', function () {
                    show($(this).parent());
                })
                .on('mouseleave', '.panel>.collapsed', function () {
                    hide($(this).parent());
                }));
        }
    };
});
