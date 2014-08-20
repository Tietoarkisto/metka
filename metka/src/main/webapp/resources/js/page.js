// entry point for JS application

define(function (require) {
    'use strict';

    require('./metka');
    require('./modules/topMenu');

    var $container = $('<div class="content container">');

    require('./modules/pageConfig')(function (pageOptions) {
        var options = $.extend({
            header: 'Metka',
            content: [],
            data: null,
            dataConf: null
        }, pageOptions);

        options.$events = $({});

        $container.append(require('./modules/header')(options.header));
        require('./modules/container').call($container, options);
        require('./modules/buttonContainer').call($container, options);

        $('body')
            .append($('<div class="wrapper">')
                .append($container));
    });
});
