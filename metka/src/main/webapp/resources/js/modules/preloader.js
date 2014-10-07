define(function (require) {
    'use strict';

    return function ($container) {
        $container
            .empty()
            .append([
                '<div class="row">',
                    '<div class="col-xs-4 col-xs-offset-4">',
                        '<div class="progress">',
                            '<div class="progress-bar progress-bar-striped active" style="width: 100%"></div>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join(''));
    };
});