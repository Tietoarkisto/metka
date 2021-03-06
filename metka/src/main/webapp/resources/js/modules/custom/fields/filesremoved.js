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

    var resultParser = require('./../../resultParser');

    return function (options) {
        function partialRefresh() {
            require('./../../server')('viewAjax', {
                method: 'GET',
                success: function (response) {
                    if (resultParser(response.result).getResult() === 'VIEW_SUCCESSFUL') {
                        // on browser, overwrite these fields only, since there might be other unsaved fields on page
                        ['files', 'studyvariables'].forEach(function (field) {
                            options.data.fields[field] = response.data.fields[field];
                        });
                        //options.$events.trigger('refresh.metka');
                        var key = "redraw-{key}";
                        options.$events.trigger(key.supplant({key: "studyvariables"}));
                        options.$events.trigger(key.supplant({key: "filesexisting"}));
                        options.$events.trigger(key.supplant({key: "filesremoved"}));
                    }
                }
            });
        }

        function view(requestOptions) {
            require('./../../revisionModal')(options, requestOptions, 'STUDY_ATTACHMENT', partialRefresh, options.field.key, true);
        }

        function sendRefresh(options, row) {
            setTimeout(function(){ options.$events.trigger('container-{key}-{lang}-push'.supplant({
                key: options.field.key,
                lang: options.defaultLang
            }), [row])},0);
        }

        return {
            preCreate: function(options) {
                delete options.field.displayType;
                var fieldConf = options.dataConf.fields["files"];
                options.dataConf.fields[options.field.key] = $.extend(true, {}, fieldConf, {
                    key: options.field.key
                });
                options.fieldOptions = options.dataConf.fields[options.field.key];
            },
            field: {
                onClick: function (transferRow, replaceTr) {
                    view({
                        id: transferRow.value.split("-")[0],
                        no: transferRow.value.split("-")[1]
                    }, replaceTr);
                }
            },
            /**
             * Called during containerField redraw operation.
             * @param $tbody Container table body
             * @param options Field options
             * @param lang Container lang
             * @param page Current container page
             * @param $thead Container table header
             * @return boolean Return true if normal container redraw should be skipped
             */
            onRedraw: function($tbody, options, lang, $thead) {
                function mapRow(row) {
                    var transferRow = rowsMapped.find(function(r){return r.id === row.key});
                    if(row.value) {
                        sendRefresh(options,transferRow.transferRow);
                    }
                }

                require('./../../data')(options).removeRows('DEFAULT');
                var rows = require('./../../data')(options)("files").getByLang(options.defaultLang);

                if (!rows) {
                    return true;
                }

                var rowsMapped = rows
                    .filter(function(row){return !row.removed})
                    .map(function(row) {return {transferRow: row, id: parseInt(row.value.split("-")[0])}});

                if(rowsMapped) {

                    require('./../../server')('/revision/revisionablesLogicallyRemoved', {
                        method: 'POST',
                        data: JSON.stringify({values:rowsMapped.map(function(row){return row.id})}),
                        success: function (response) {
                            response.values.forEach(mapRow)
                        }
                    });

                }
                return true;
            }
        };
    };
});
