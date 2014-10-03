define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options, lang, key, rows) {
        var PAGE = require('./../metka').PAGE;
        var fieldOptions = getPropertyNS(options, 'dataConf.fields', key) || {};
        return function (title, button) {
            return function (transferRow, onClose) {
                // copy data, so if dialog is dismissed, original data won't change
                var transferRowCopy = $.extend(true, {}, transferRow);

                var containerOptions = {
                    title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                    data: transferRowCopy,
                    dataConf: options.dataConf,
                    $events: $({}),
                    defaultLang: fieldOptions.translatable ? lang : options.defaultLang,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: rows()
                        }
                    ],
                    buttons: [
                        {
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.' + button))
                                    .click(function () {
                                        $.extend(transferRow, transferRowCopy);
                                        onClose(transferRow);
                                    });
                            }
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                };
                log(containerOptions);
                // if not translatable container and has translatable subfields, show language selector
                if (!fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    containerOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || options.defaultLang;
                }

                var $modal = require('./modal')(containerOptions);

            };
        }
    };
});
