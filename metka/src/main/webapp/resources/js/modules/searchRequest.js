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

define(function(require) {
    'use strict';

    return function(options, requestConf, lang, prefix) {
        var data = require('./data')(options);
        if (typeof requestConf === 'function') {
            return requestConf();
        }
        if (Array.isArray(requestConf)) {
            var requestData = require('./commonSearchBooleans')(prefix).requestData(options, {
                values: {}
            });
            requestConf.map(function (field) {
                // validate input and (in case of string) transform to object
                switch (typeof field) {
                    case 'string':
                        return {
                            key: field
                        };
                    case 'object':
                        if (field !== null) {
                            return field;
                        }
                }
                throw 'Illegal search configuration entry.';
            }).map(function (searchOptions) {
                // set defaults
                return $.extend({
                    useSelectionText: true,
                    exactValue: false,
                    addWildcard: false,
                    addParens: true,
                    value: null,
                    rename: null,
                    useSubquery: null
                }, searchOptions);
            }).forEach(function (searchOptions) {
                var key = searchOptions.key;
                var value = searchOptions.value || (function() {
                        var temp = data(searchOptions.key).getByLang(options.defaultLang) || '';
                        if (require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'type') === 'SELECTION') {
                            if (temp && searchOptions.useSelectionText) {
                                temp = $('select[data-metka-field-key="{key}"][data-metka-field-lang="{lang}"] option[value="{value}"]'.supplant({
                                    key: key,
                                    lang: options.defaultLang,
                                    value: temp
                                })).text();
                            }
                        }
                        return temp;
                    })();
                if(!value) {
                    return;
                }
                if (searchOptions.exactValue) {
                    value = '/' + (searchOptions.addWildcard?".*":"") + value + (searchOptions.addWildcard?".*":"") + '/';
                } else if(searchOptions.addWildcard) {
                    // Escape regex characters in non-exact searches
                    value = value.replace(/[-[\]{}()*+!<=:?.\/\\^$|#\s,]/g, '\\$&');
                    value = "*"+value+"*";
                } else {
                    value = value.replace(/[-[\]{}()*+!<=:?.\/\\^$|#\s,]/g, '\\$&');
                }
                if (searchOptions.addParens) {
                    value = '(' + value + ')';
                }
                if(searchOptions.subQuery != null) {
                    value = searchOptions.subQuery.supplant({
                        input: value
                    });
                }
                if (!requestData.values[searchOptions.rename || key]) {
                    requestData.values[searchOptions.rename || key] = value;
                } else {
                    requestData.values[searchOptions.rename || key] = "(" + requestData.values[searchOptions.rename || key] + " AND " + value + ")";
                }
            });
            if(lang) {
                requestData.values['key.language'] = lang;
            }
            return requestData;
        }
        throw 'Illegal search request';
    }
});