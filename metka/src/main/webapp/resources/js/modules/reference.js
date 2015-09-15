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

    return {
        optionsByPath: function (key, options, lang, callback) {
            return function (dataFields, reference, rowValue, transferRow) {

                // TODO: This should always be called with reference, also reference fetching should be generalized somewhere
                var target = getPropertyNS(options, 'dataConf.fields', key);
                if (!dataFields && options.data) {
                    dataFields = options.data.fields;
                }
                if(!reference) {
                    if(target.type === "REFERENCE" || target.type === 'REFERENCECONTAINER') {
                        reference = getPropertyNS(options, 'dataConf.references', target.reference);
                    } else if(target.type === "SELECTION") {
                        var list = getPropertyNS(options, 'dataConf.selectionLists', target.selectionList);
                        if(list.type === "REFERENCE") {
                            reference = getPropertyNS(options, 'dataConf.references', list.reference);
                        }
                    }
                }
                if(typeof reference === 'function') {
                    reference = reference(transferRow);
                }

                if(!reference) {
                    callback([]);
                }

                var root = function r(currentKey, dataFields, lang, reference, next) {
                    var path = {
                        reference: reference,
                        value: (function() {
                            var target = getPropertyNS(options, 'dataConf.fields', currentKey);
                            if(target.type === 'REFERENCECONTAINER') {
                                return rowValue;
                            } else {
                                return dataFields && dataFields[currentKey] ? require('./data').latestValue(dataFields[currentKey], lang) : undefined;
                            }
                        })(),
                        next: next
                    };

                    if(reference && reference.type === "DEPENDENCY") {
                        var target = getPropertyNS(options, 'dataConf.fields', reference.target);
                        var targetRef = null;
                        if(target.type === "REFERENCE" || target.type === 'REFERENCECONTAINER') {
                            targetRef = getPropertyNS(options, 'dataConf.references', target.reference);
                        } else if(target.type === "SELECTION") {
                            var list = getPropertyNS(options, 'dataConf.selectionLists', target.selectionList);
                            if(!list) {
                                log("No list found for "+target.key);
                                return path;
                            }
                            if(list.type === "REFERENCE") {
                                targetRef = getPropertyNS(options, 'dataConf.references', list.reference);
                            }
                        }
                        var prev = r(reference.target, dataFields, lang, targetRef, path);
                        if(prev) {
                            return prev;
                        } else {
                            return path;
                        }
                    }

                    return path;
                }(key, dataFields, lang, reference);

                var cur = root;
                while (cur.next) {
                    if(!cur.value) {
                        callback([]);
                        return;
                    } else {
                        cur = cur.next;
                    }
                }

                if (target.type === 'SELECTION') {
                    cur.value = null;
                }

                require('./server')('optionsByPath', {
                    data: JSON.stringify({
                        requests : [{
                            key: key,
                            container: "",
                            language: MetkaJS.L10N.locale.toUpperCase(),
                            root: root,
                            returnFirst: target.type === 'REFERENCE'
                        }]
                    }),
                    success: function (data) {
                        callback(getPropertyNS(data, 'responses.0.options') || []);
                    }
                });
            };
        },
        optionByPath: function request(key, options, lang, callback) {
            return this.optionsByPath(key, options, lang, function (options) {
                callback(options[0]);
            });
        }
    };
});
