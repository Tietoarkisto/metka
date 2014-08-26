define(function (require) {
    'use strict';

    return function ($container, lang) {
        if (lang && lang !== 'DEFAULT') {
            $container.append(' ', $('<span class="label label-lang">')
                .addClass('label-' + lang)
                .text(lang));
        }

        return $container;
    };
});
