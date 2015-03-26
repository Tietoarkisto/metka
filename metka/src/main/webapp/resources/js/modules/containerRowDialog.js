define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options, lang, rows) {
        var PAGE = require('./../metka').PAGE;
        return function (type, button) {
            return function (transferRow, onClose) {
                // copy data, so if dialog is dismissed, original data won't change
                var transferRowCopy = $.extend(true, {}, transferRow);
                var modalOptions = $.extend((require('./isFieldDisabled')(options, lang) ? {
                    type: 'VIEW',
                    buttons: [{
                        type: 'DISMISS'
                    }]
                } : {
                    type: type.toUpperCase(),
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
                }), {
                    data: transferRowCopy,
                    containerKey: options.field.key,
                    dataConf: options.dataConf,
                    $events: $({}),
                    defaultLang: options.fieldOptions.translatable ? lang : options.defaultLang,
                    fieldTitles: options.fieldTitles,
                    dialogTitle: options.field.dialogTitle,
                    dialogTitles: options.dialogTitles,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: rows()
                        }
                    ]
                });

                // if not translatable container and has translatable subfields, show language selector
                if (!options.fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    modalOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || MetkaJS.User.role.defaultLanguage.toUpperCase();
                }
                require('./modal')($.extend(true, require('./optionsBase')(), modalOptions));
            };
        }
    };
});
