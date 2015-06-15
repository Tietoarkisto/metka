define(function (require) {
    'use strict';

    return function (key, requestOptions, partialRefresh) {
        require('./server')('viewAjax', $.extend({
            PAGE: 'STUDY_VARIABLE'
        }, requestOptions), {
            method: 'GET',
            success: function (data) {
                var modalOptions = $.extend(data.gui, {
                    title: 'Muokkaa muuttujaa',
                    data: data.data,
                    dataConf: data.configuration,
                    $events: $({}),
                    defaultLang: 'DEFAULT',
                    //translatableCurrentLang: MetkaJS.User.role.defaultLanguage.toUpperCase(),
                    large: true
                });

                modalOptions.$events.on('attachment.refresh', partialRefresh);

                require('./modal')($.extend(true, require('./optionsBase')(), modalOptions));
            }
        });
    };
});