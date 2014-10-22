define(function (require) {
    'use strict';

    var metka = require('./../metka');
    require('./topMenu');

    var options = {
        header: 'Metka',
        content: [],
        data: {},
        dataConf: {},
        $events: $({}),
        defaultLang: 'DEFAULT'
    };

    require('./uiLocalization');
    document.title = MetkaJS.L10N.get('page.title');

    require('./pageConfig')(options, function x() {
        $('body')
            .append($('<div class="wrapper">')
                .append($('<div class="content container">')
                    .on('refresh.metka', function () {
                        metka.id = options.data.key.id;
                        metka.no = options.data.key.no;
                        options.readOnly = require('./isDataReadOnly')(options.data);
                        var $this = $(this)
                            .empty()
                            .append(require('./header')(options.header));
                        require('./container').call($this, options);
                        require('./buttonContainer').call($this, options);

                        $(window).off('beforeunload', require('./onBeforeUnload'));
                        return false;
                    })
                    .trigger('refresh.metka')));
    });
});
