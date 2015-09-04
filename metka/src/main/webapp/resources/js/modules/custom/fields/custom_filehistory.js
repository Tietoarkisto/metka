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
        delete options.field.displayType;

        return {
            field: {
                onClick: function (transferRow) {
                    if(!transferRow.value) {
                        return;
                    }
                    var split = transferRow.value.split("-");
                    if(split.length < 2) {
                        return;
                    }
                    require('./../../server')('viewAjax', {
                        PAGE: 'STUDY_ATTACHMENT',
                        no: split[1],
                        id: split[0]
                    }, {
                        method: 'GET',
                        success: function (response) {
                            if (resultParser(response.result).getResult() === 'VIEW_SUCCESSFUL') {
                                $.extend(options.data, response.data);
                                $.extend(require('./../../root')(options).content, response.gui.content);
                                options.type = options.isReadOnly(options) ? 'VIEW' : 'MODIFY';
                                options.$events.trigger('refresh.metka');
                            }
                        }
                    })
                }
            },
            onRedraw: function($tbody, options, lang, $thead) {
                require('./../../data')(options).removeRows('DEFAULT');
                require('./../../server')('/study/attachmentHistory/', {
                    data: JSON.stringify(options.data),
                    success: function (data) {
                        //var objectToTransferRow = require('./../../map/object/transferRow');
                        var revisions = data.rows.map(function (result) {
                            return {
                                key: 'custom_fileHistory',
                                value: result.id+"-"+result.no
                            };
                        });

                        revisions && revisions.forEach(function (row) {
                            options.$events.trigger('container-{key}-{lang}-push'.supplant({
                                key: options.field.key,
                                lang: options.defaultLang
                            }), [row]);
                            //$containerField.data('addRow')(row);
                        });
                    }
                });
                return true;
            }
        }
    };
});