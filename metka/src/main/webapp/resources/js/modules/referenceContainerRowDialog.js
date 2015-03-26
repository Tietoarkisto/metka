define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');
    // FIXME: Merge shared code with containerRowDialog.
    return function (options, lang) {
        var PAGE = require('./../metka').PAGE;
        return function (type, button) {
            return function (transferRow, onClose) {
                // FIXME: Merge shared code with containerRowDialog.
                var fields = {};
                fields[options.field.key+'_select'] = {
                    "key": options.field.key+'_select',
                    "translatable": false,
                    "type": "SELECTION",
                    "selectionList": "referenceContainerRowDialog_list"
                };
                var references = {};
                references[options.fieldOptions.reference] = $.extend(true, {}, options.dataConf.references[options.fieldOptions.reference], {
                    // TODO: add some type of 'ignoreSelf' parameter so that current revision is not included in results
                    approvedOnly: true,
                    ignoreRemoved: true
                });
                // copy data, so if dialog is dismissed, original data won't change
                var transferRowCopy = $.extend(true, {}, transferRow);

                var modalOptions = {
                    type: type.toUpperCase(),
                    data: transferRowCopy,
                    dataConf: $.extend({}, {
                        fields: fields,
                        references: references,
                        selectionLists: {
                            referenceContainerRowDialog_list: {
                                "type": "REFERENCE",
                                // TODO: somehow disable OK button if nothing is selected
                                "reference": options.fieldOptions.reference
                            }
                        }
                    }),
                    containerKey: options.field.key,
                    $events: $({}),
                    defaultLang: options.fieldOptions.translatable ? lang : options.defaultLang,
                    fieldTitles: options.fieldTitles,
                    dialogTitle: options.field.dialogTitle,
                    dialogTitles: options.dialogTitles,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: [
                                {
                                    type: 'ROW',
                                    cells: [
                                        {
                                            type: 'CELL',
                                            title: " ",
                                            field: {
                                                key: options.field.key+'_select'
                                            }
                                        }
                                    ]
                                }
                            ]
                            /*rows: (function () {
                                var dataConfig = {
                                    "key": options.field.key+'_select',
                                    "translatable": false,
                                    "type": "SELECTION",
                                    "selectionList": 'referenceContainerRowDialog_list'
                                };
                                return [{
                                    type: 'ROW',
                                    cells: [$.extend({}, dataConfig, {
                                        type: 'CELL',
                                        //title: MetkaJS.L10N.get(fieldTitle(field)),
                                        //title: getTitle(field),
                                        title: " ",
                                        field: dataConfig
                                    })]
                                }];
                            })()*/
                        }
                    ],
                    buttons: [
                        {
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.' + button))
                                    .click(function () {
                                        transferRow.value = transferRowCopy.fields[options.field.key+'_select'].values[lang].current;
                                        onClose(transferRow);
                                    });
                            }
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                };

                // if not translatable container and has translatable subfields, show language selector
                if (!options.fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    modalOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || MetkaJS.User.role.defaultLanguage.toUpperCase();
                }

                require('./modal')($.extend(true, require('./optionsBase')(), modalOptions));
            };
        }
    };
});