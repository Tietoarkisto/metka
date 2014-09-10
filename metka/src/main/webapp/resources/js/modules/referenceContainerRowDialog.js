define(function (require) {
    'use strict';

    return function (page, title, key, content) {
        return function (requestOptions, onSaveSuccess) {
            var metka = require('./../metka');
            require('./server')('viewAjax', $.extend({
                page: page
            }, requestOptions), {
                method: 'GET',
                success: function (data) {
                    var modalOptions = $.extend(data.gui, {
                        title: title,
                        data: data.transferData,
                        dataConf: data.configuration,
                        $events: $({}),
                        defaultLang: 'DEFAULT',
                        large: true,
                        content: content,
                        buttons: [{
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.save'))
                                    .click(require('./formAction')('save')(modalOptions, function (response) {
                                        require('./server')('/references/referenceRowRequest', {
                                            data: JSON.stringify({
                                                type: metka.PAGE,
                                                id: metka.id,
                                                no: metka.no,
                                                path: key,
                                                reference: response.data.key.id
                                            }),
                                            success: function (data) {
                                                onSaveSuccess(data.row);
                                            }
                                        });
                                    }));
                            }
                        }, {
                            type: 'CANCEL'
                        }]
                    });
                    log(modalOptions);
                    require('./modal')(modalOptions);
                }
            });
        };
    };
});
