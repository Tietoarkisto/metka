// entry point for JS application

define(function (require) {
    'use strict';

    require('./metka');
    require('./modules/topMenu');

    var $container = $('<div class="content container">');

    var options = {
        header: 'Metka',
        content: [],
        data: {},
        dataConf: {},
        $events: $({}),
        defaultLang: 'DEFAULT'
    };

    require('./modules/pageConfig')(options, function () {
        $container.append(require('./modules/header')(options.header));
        require('./modules/container').call($container, options);
        require('./modules/buttonContainer').call($container, options);

        $('body')
            .append($('<div class="wrapper">')
                .append($container));
    });
});
