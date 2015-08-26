/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

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

                var modalOptions = $.extend(true, require('./optionsBase')(options), {
                    type: type.toUpperCase(),
                    data:  $.extend(true, {}, transferRow),
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
                    subfieldConfiguration: options.subfieldConfiguration,
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
                                        transferRow.value = modalOptions.data.fields[options.field.key+'_select'].values[lang].current;
                                        onClose(transferRow);
                                    });
                            }
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                });

                // if not translatable container and has translatable subfields, show language selector
                if (!options.fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    modalOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || MetkaJS.User.role.defaultLanguage.toUpperCase();
                }

                require('./modal')(modalOptions);
            };
        }
    };
});