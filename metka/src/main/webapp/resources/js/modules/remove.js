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

    var resultParser = require('./resultParser');

    return function (options) {
        return function () {
            var id = options.data.key.id;
            var name = '';
            // A bit of a workaround, getting the correct ids to be shown
            switch (options.data.configuration.type) {
                case "STUDY":
                    id = options.data.fields.studyid.values[options.defaultLang].original || options.data.fields.studyid.values[options.defaultLang].current;
                    name = options.data.fields.title.values[options.defaultLang].original;
                    break;
                case "BINDER_PAGE":
                    if (options.data.state.uiState === 'DRAFT') {
                        id = options.data.key.id;
                    } else {
                        id = options.data.fields.studyid.values[options.defaultLang].original || options.data.fields.studyid.values[options.defaultLang].current;
                    }
                    break;
                case "PUBLICATION":
                    id = options.data.fields.publicationid.values[options.defaultLang].original || options.data.fields.publicationid.values[options.defaultLang].current;
                    name = options.data.fields.publicationtitle.values[options.defaultLang].current;
                    break;
                case "SERIES":
                    name = options.data.fields.seriesname.values[options.defaultLang].original || options.data.fields.seriesname.values[options.defaultLang].current;
                    break;
                case "STUDY_VARIABLES":
                    name = options.data.fields.varfileid.values[options.defaultLang].original || options.data.fields.varfileid.values[options.defaultLang].current;
                    break;
                case "STUDY_VARIABLE":
                    name = options.data.fields.varid.values[options.defaultLang].original || options.data.fields.varid.values[options.defaultLang].current;
                    break;
            }
            var operationType = options.data.state.uiState === 'DRAFT' ? 'draft' : 'logical';
            require('./modal')($.extend(true, require('./optionsBase')(), {
                title: MetkaJS.L10N.get('confirmation.remove.revision.title'),
                // TODO: simpler/unified way to supplement localization keys/texts
                body: MetkaJS.L10N.get('confirmation.remove.revision.{operationType}.text'.supplant({
                    operationType: operationType
                })).supplant({name: name, id: id, no: options.data.key.no}).supplant({
                    target: MetkaJS.L10N.get('confirmation.remove.revision.{operationType}.data.{type}'.supplant({
                        operationType: operationType,
                        type: options.data.configuration.type
                    }))
                }),
                buttons: [{
                    type: 'YES',
                    create: function () {
                        this
                            .click(function () {
                                $(".modal-footer").find("button").attr('disabled', 'disabled');
                                // Prevent the file attachment removal loop (issue #871)
                                if(options.customHandler === "studyAttachmentRemove") {
                                    $(".modal").remove();
                                    $('body').removeClass('modal-open');
                                    window.location.reload();
                                }
                                require('./server')('remove', {
                                    data: JSON.stringify(options.data.key),
                                    success: function (response) {
                                        $(".modal-footer").find("button").removeAttr('disabled');
                                        var success = $.extend({
                                            SUCCESS_LOGICAL: function () {
                                                require('./assignUrl')('view');
                                            },
                                            SUCCESS_DRAFT: function () {
                                                require('./assignUrl')('view', {no: ''});
                                            },
                                            SUCCESS_REVISIONABLE: function () {
                                                require('./assignUrl')('searchPage');
                                            }
                                        }, options.success);

                                        success = success[resultParser(response.result).getResult()] || function (response) {
                                            require('./resultViewer')(response.result, "remove");
                                        };

                                        success(response);

                                    },
                                    error: function() {
                                        $(".modal-footer").find("button").removeAttr('disabled');
                                    }
                                });
                            });
                    }
                }, {
                    type: 'NO'
                }]
            }));
        };
    };
});
