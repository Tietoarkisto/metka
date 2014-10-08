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

    var metka = require('./metka');
    require('./modules/topMenu');

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

        $('body')
            .append($('<div class="wrapper">')
                .append($('<div class="content container">')
                    .on('refresh.metka', function () {
                        metka.id = options.data.key.id;
                        metka.no = options.data.key.no;
                        options.readOnly = !options.data.state.uiState === 'DRAFT' || !(options.data.state.handler === MetkaJS.User.userName);
                        var $this = $(this)
                            .empty()
                            .append(require('./modules/header')(options.header));
                        require('./modules/container').call($this, options);
                        require('./modules/buttonContainer').call($this, options);
                        return false;
                    })
                    .trigger('refresh.metka')));
    });
});
