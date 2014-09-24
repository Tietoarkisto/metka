// entry point for JS application

(function () {
    var version;

    // When new release: remove comment and increase version number
    //version = 1;

    require.config({
        urlArgs: 'v=' + (version || Date.now())
    });
})();

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

    require('./modules/uiLocalization');
    document.title = MetkaJS.L10N.get('page.title');

    require('./modules/pageConfig')(options, function () {
        if (options.fieldTitles) {
            require('./modules/addTranslation')('fieldTitles', options.fieldTitles);
        }
        $container.append(require('./modules/header')(options.header));
        require('./modules/container').call($container, options);
        require('./modules/buttonContainer').call($container, options);

        $('body')
            .append($('<div class="wrapper">')
                .append($container));
    });
});
