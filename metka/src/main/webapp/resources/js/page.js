// entry point for JS application

define([
    './metka',
    './modules/pageConfig',
    './modules/container',
    './modules/buttonContainer',
    './modules/header'
], function (metka, pageConfig, container, buttonContainer, header) {
    'use strict';

    var options = $.extend({
        header: 'Metka',
        content: [],
        data: null,
        dataConf: null
    }, pageConfig);

    var $container = $('<div class="content container">')
        .append(header(options.header));

    container.call($container, options);
    buttonContainer.call($container, options);

    $('body')
        .append($('<div class="wrapper">')
            .append($container));
});
