define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options, lang) {
        switch(options.fieldOptions.type) {
            case 'REFERENCECONTAINER':
                /*// FIXME: Merge shared code with containerRowDialog.
                var fields = {};
                fields[key] = {
                    "key": key,
                    "translatable": false,
                    "type": "SELECTION",
                    "selectionList": "referenceContainerRowDialog_list"
                };
                var references = {};
                references[options.fieldOptions.reference] = $.extend(true, {}, options.dataConf.references[options.fieldOptions.reference], {
                    // TODO: add some type of 'ignoreSelf' parameter so that current revision is not included in results
                    approvedOnly: true,
                    ignoreRemoved: true
                });*/
                /*return require('./referenceContainerRowDialog')({
                    defaultLang: options.defaultLang,
                    dataConf: {
                        key: options.dataConf.key,
                        selectionLists: {
                            referenceContainerRowDialog_list: {
                                "type": "REFERENCE",
                                // TODO: somehow disable OK button if nothing is selected
                                "reference": options.fieldOptions.reference
                            }
                        },
                        references: references,
                        fields: fields
                    },
                    field: {
                        key: key
                    }
                }, lang, key);*/
                return require('./referenceContainerRowDialog')(options, lang);
            case 'CONTAINER':
                return require('./containerRowDialog')(options, lang, function () {
                    var freeTextKeys = [];
                    if (options.dataConf && options.dataConf.selectionLists) {
                        $.each(options.dataConf.selectionLists, function (selectionListKey, list) {
                            if (list.freeTextKey) {
                                freeTextKeys.push(list.freeTextKey);
                            }
                        });
                    }
                    return (options.fieldOptions.subfields || []).filter(function (fieldKey) {
                        // filter free text fields
                        return freeTextKeys.indexOf(fieldKey) === -1;
                    }).map(function (fieldKey) {
                        var dataConfig = $.extend(true, {}, options.dataConf.fields[fieldKey]);
                        return {
                            type: 'ROW',
                            cells: [$.extend(
                                true
                                , {
                                    type: 'CELL',
                                    translatable: options.fieldOptions.translatable ? false : dataConfig.translatable,
                                    //title: MetkaJS.L10N.get(fieldTitle(field)),
                                    //title: getTitle(field),

                                }
                                , options.extraDialogConfiguration && options.extraDialogConfiguration[fieldKey]
                                , {
                                    field: {
                                        key: fieldKey
                                    }
                                }
                            )]
                        };
                    });
                });
            default:
                return function() {};
        }
    }
});