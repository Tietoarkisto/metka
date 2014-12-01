define(function (require) {
    'use strict';

    return function (key, requestOptions, onSaveSuccess) {
        require('./server')('viewAjax', $.extend({
            PAGE: 'STUDY_VARIABLE'
        }, requestOptions), {
            method: 'GET',
            success: function (data) {
                log(data);
                var modalOptions = $.extend(data.gui, {
                    title: 'Muokkaa muuttujaa',
                    data: data.transferData,
                    dataConf: data.configuration,
                    $events: $({}),
                    defaultLang: 'DEFAULT',
                    translatableCurrentLang: MetkaJS.User.role.defaultLanguage.toUpperCase(),
                    large: true
                });
                require('./isRelatedStudyDraftForCurrentUser')(modalOptions, function (isDraft) {
                    modalOptions.isRelatedStudyDraftForCurrentUser = isDraft;
                    var $modal = require('./modal')(modalOptions);
                });
            }
        });
    };
});