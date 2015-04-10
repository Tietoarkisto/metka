define(function (require) {
    'use strict';

    return function ($container, lang) {
        if (lang && lang.toUpperCase() !== 'DEFAULT') {
            $container.append(' ', $('<span class="label label-lang">')
                .addClass('label-' + lang.toUpperCase())
                .text(lang.toUpperCase()));
        }

        return $container;
    };
});
