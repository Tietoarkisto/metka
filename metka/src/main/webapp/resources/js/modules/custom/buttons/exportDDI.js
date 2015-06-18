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

    return function(options) {
        this.click( function() {
            require('./../../modal')($.extend(true, require('./../../optionsBase')(options), {
                '&title': {
                    default: "Lataa DDI"
                },
                $events: $({}),
                defaultLang: "DEFAULT",
                ignoreTranslate: true,
                dataConf: {
                    selectionLists: {
                        language_list: {
                            key: "language_list",
                            type: "VALUE",
                            default: "default",
                            options: [
                                {
                                    value: "default",
                                    title: "Suomi"
                                }, {
                                    value: "en",
                                    title: "Englanti"
                                }, {
                                    value: "sv",
                                    title: "Ruotsi"
                                }
                            ]
                        }
                    },
                    fields: {
                        language: {
                            key: "language",
                            type: "SELECTION",
                            selectionList: "language_list"
                        }
                    }
                },
                data: {
                    fields: {

                    }
                },
                content: [
                    {
                        type: "COLUMN",
                        columns: 1,
                        rows: [
                            {
                                type: "ROW",
                                cells: [
                                    {
                                        type: "CELL",
                                        title: "DDI kieli",
                                        field: {
                                            key: "language"
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                buttons: [{
                    type: 'CUSTOM',
                    title: "Lataa",
                    create: function(options) {
                        this.click(function() {
                            var $id = require('./../../root')(options).data.key.id;
                            var $no = require('./../../root')(options).data.key.no;
                            var request = {
                                id: $id,
                                no: $no,
                                language: require('./../../data')(options)("language").getByLang("DEFAULT")
                            };
                            require('./../../server')('/study/ddi/export', {
                                data: JSON.stringify(request),
                                success: function(response) {
                                    if(response.result === "OPERATION_SUCCESSFUL") {
                                        saveAs(new Blob([response.content], {type: "text/xml;charset=utf-8"}), "id_"+response.id+"_revision_"+response.no+"_ddi_"+response.language+".xml");
                                    }
                                }
                            });
                        });
                    }
                }, {
                    type: 'DISMISS'
                }]
            }));
        });
    };
});
