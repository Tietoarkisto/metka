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

    return function(result, operation, callback) {
        var dismiss = {
            type: 'DISMISS'
        };

        require('./server')('optionsByPath', {
            data: JSON.stringify({
                requests : [{
                    key: "resulttitle",
                    container: "",
                    language: MetkaJS.L10N.locale.toUpperCase(),
                    root: {
                        reference: {
                            key: null,
                            type: "JSON",
                            target: "result_code_descriptions",
                            valuePath: "key",
                            titlePath: "title"
                        },
                        value: result,
                        next: (operation ? {
                            reference: {
                                key: null,
                                type: "DEPENDENCY",
                                target: "result",
                                valuePath: "operations.key",
                                titlePath: "title"
                            },
                            value: operation
                        } : null)
                    }
                }, {
                    key: "resulttext",
                    container: "",
                    language: MetkaJS.L10N.locale.toUpperCase(),
                    root: {
                        reference: {
                            key: null,
                            type: "JSON",
                            target: "result_code_descriptions",
                            valuePath: "key",
                            titlePath: "text"
                        },
                        value: result,
                        next: (operation ? {
                            reference: {
                                key: null,
                                type: "DEPENDENCY",
                                target: "result",
                                valuePath: "operations.key",
                                titlePath: "text"
                            },
                            value: operation
                        } : null)
                    }
                }]
            }),
            success: function (data) {
                if(callback) {
                    dismiss.create = function () {
                        this.click(function () {
                            callback();
                        });
                    };
                }
                var body = getPropertyNS(data, 'responses.1.options.0.title.value');
                if(operation && !body) {
                    require('./server')('optionsByPath', {
                        data: JSON.stringify({
                            requests: [{
                                key: "resulttitle",
                                container: "",
                                language: MetkaJS.L10N.locale.toUpperCase(),
                                root: {
                                    reference: {
                                        key: null,
                                        type: "JSON",
                                        target: "result_code_descriptions",
                                        valuePath: "key",
                                        titlePath: "title"
                                    },
                                    value: result
                                }
                            }, {
                                key: "resulttext",
                                container: "",
                                language: MetkaJS.L10N.locale.toUpperCase(),
                                root: {
                                    reference: {
                                        key: null,
                                        type: "JSON",
                                        target: "result_code_descriptions",
                                        valuePath: "key",
                                        titlePath: "text"
                                    },
                                    value: result
                                }
                            }]
                        }),
                        success: function (data) {
                            var body = getPropertyNS(data, 'responses.1.options.0.title.value');
                            if(!body) {
                                body = result+(operation ? "."+operation : "");
                            } else {
                                body = result+(operation ? "."+operation : "")+"</br>"+body;
                            }
                            require('./modal')($.extend(true, require('./optionsBase')(), {
                                title: MetkaJS.L10N.get('alert.'+(getPropertyNS(data, 'responses.0.options.0.title.value') || "NOTICE").toLowerCase()+'.title'),
                                body: body,
                                buttons: [dismiss]
                            }));
                        }
                    });
                } else {
                    require('./modal')($.extend(true, require('./optionsBase')(), {
                        title: MetkaJS.L10N.get('alert.'+(getPropertyNS(data, 'responses.0.options.0.title.value') || "NOTICE").toLowerCase()+'.title'),
                        body: (body || result+(operation ? "."+operation : "")),
                        buttons: [dismiss]
                    }));
                }
            },
            error: function(jqXHR, status, thrown) {
                // TODO: Form exception dialog
            }
        });
    }
});