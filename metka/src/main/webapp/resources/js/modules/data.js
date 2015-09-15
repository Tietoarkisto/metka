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
/*
    var onModified = (function () {
        var hasDataChanged = false;
        return function () {
            if (!hasDataChanged) {
                hasDataChanged = true;
                $(window).on('beforeunload.metka', require('./onBeforeUnload'));
            }
        };
    })();
*/

    function data(options) {
        function io(key) {
            function byFieldKey(key) {
                return io(key);
            }

            function getTransferField(createIfUndefined) {
                var transferField = getPropertyNS(options, 'data.fields', key);

                if (transferField) {
                    return transferField;
                }

                var type = getPropertyNS(options, 'dataConf.fields', key, 'type');
                if (type !== 'CONTAINER' && type !== 'REFERENCECONTAINER') {
                    type = 'VALUE';
                }
                if (createIfUndefined) {
                    return require('./utils/setPropertyNS')(options, 'data.fields', key, {
                        key: key,
                        type: type
                    })
                }
            }

            byFieldKey.getByLang = function (lang) {
                var transferField = getTransferField();

                if (transferField) {
                    if (transferField.type === 'VALUE') {
                        return data.latestValue(transferField, lang);
                    } else {
                        return getPropertyNS(transferField, 'rows', lang);
                    }
                }
            };
            byFieldKey.errors = function () {
                var transferField = getTransferField();

                if (transferField) {
                    return transferField.errors || [];
                }
                return [];
            };
            byFieldKey.errorsByLang = function (lang) {
                var transferField = getTransferField();

                if (transferField && transferField.values && transferField.values[lang]) {
                    return transferField.values[lang].errors || [];
                }
                return [];
            };

            byFieldKey.setByLang = function (lang, value) {
                var transferField = getTransferField(true);

                transferField.values = transferField.values || {};
                transferField.values[lang] = transferField.values[lang] || {};
                transferField.type = transferField.type || 'VALUE';


                transferField.values[lang].current = value;
                options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                    key: key,
                    lang: lang
                }), [value]);

                //onModified();
            };

            /**
             * This clears all rows from a transfer field.
             * Should not be used to remove all rows from normal form-containers (in that case all rows should be marked removed)
             * but instead for custom forms that are not saved to database and are compiled on the client.
             * @param lang
             */
            byFieldKey.removeRows = function(lang) {
                var transferField = getTransferField(true);
                if(transferField.rows) {
                    transferField.rows[lang] = [];
                }
            };

            byFieldKey.validRows = function(lang) {
                var transferField = getTransferField(false);
                if(!transferField || !transferField.rows || !transferField.rows[lang] || transferField.rows[lang].length < 1 ) {
                    return 0;
                }

                var amnt = 0;
                transferField.rows[lang].forEach(function(row) {
                   if(!row.removed) {
                       amnt++;
                   }
                });

                return amnt;
            };

            byFieldKey.appendByLang = function (lang, transferRow, stopEvent) {
                var transferField = getTransferField(true);

                transferField.rows = transferField.rows || {};
                transferField.rows[lang] = transferField.rows[lang] || [];
                transferRow.key = transferRow.key || key;

                transferField.rows[lang].push(transferRow);

                if(!stopEvent) {
                    options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                        key: key,
                        lang: lang
                    }), [transferRow]);
                }
            };

            return byFieldKey;
        }

        return io(options.field ? options.field.key : undefined);
    }

    data.latestValue = function (transferField, lang) {
        var current = getPropertyNS(transferField, 'values', lang, 'current');
        if (MetkaJS.exists(current)) {
            return current;
        }
        return getPropertyNS(transferField, 'values', lang, 'original');
    };

    return data;
});
