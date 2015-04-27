define(function (require) {
    'use strict';

    var metka = require('./../metka');
    require('./topMenu');

    require('./uiLocalization');
    document.title = MetkaJS.L10N.get('page.title');

    var options = require('./optionsBase')();

    var getPropertyNS = require('./utils/getPropertyNS');

    function refreshMetka() {
        if(!content) {
            return true;
        }
        metka.id = getPropertyNS(options, 'data.key.id');
        metka.no = getPropertyNS(options, 'data.key.no');

        // (re-)render page
        content.empty();
        content.append(require('./header')(options.header));
        require('./container').call(content, options);
        require('./buttonContainer').call(content, options);

        //$(window).off('beforeunload', require('./onBeforeUnload'));
        return false;
    }
    var content = null;
    require('./pageConfig')(options, function () {
        options.$events.on('refresh.metka', refreshMetka);
        $('body')
            .append($('<div class="wrapper">')
                .append($('<div class="content container">')));

        content = $('.content.container');
        options.$events.trigger('refresh.metka');
                    // Inner elements may refresh page by triggering 'refresh.metka' event.
                    // If event is not captured before it propagates here, page will be re-rendered.
                    //.on('refresh.metka', refreshMetka)
                    /*.on('refresh.metka', function () {
                        log('refresh.metka');
                        metka.id = getPropertyNS(options, 'data.key.id');
                        metka.no = getPropertyNS(options, 'data.key.no');

                        // (re-)render page
                        var $this = $(this)
                            .empty()
                            .append(require('./header')(options.header));
                        require('./container').call($this, options);
                        require('./buttonContainer').call($this, options);

                        //$(window).off('beforeunload', require('./onBeforeUnload'));
                        return false;
                    })*/
                    // trigger once here immediately
                    //.trigger('refresh.metka')));
    });
});
