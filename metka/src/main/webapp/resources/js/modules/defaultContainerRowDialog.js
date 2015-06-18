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
                                    translatable: options.fieldOptions.translatable ? false : dataConfig.translatable

                                }
                                , options.subfieldConfiguration && options.subfieldConfiguration[fieldKey]
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