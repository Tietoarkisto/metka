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
            // A bit of a workaround, getting the correct ids to be shown
            var id = options.data.key.id;
            if (options.data.fields.studyid != null && options.data.fields.studyid.values[options.defaultLang].original.startsWith("FSD")){ id = options.data.fields.studyid.values[options.defaultLang].original };
            if (options.data.fields.binderid != null && options.data.fields.binderid.values[options.defaultLang].original.startsWith("FSD")){ id = options.data.fields.binderid.values[options.defaultLang].original };
            if (options.data.fields.publicationid != null){ id = options.data.fields.publicationid.values[options.defaultLang].original };
            var operationType = options.data.state.uiState === 'DRAFT' ? 'draft' : 'logical';
            require('./modal')($.extend(true, require('./optionsBase')(), {
                title: MetkaJS.L10N.get('confirmation.remove.revision.title'),
                // TODO: simpler/unified way to supplement localization keys/texts
                body: MetkaJS.L10N.get('confirmation.remove.revision.{operationType}.text'.supplant({
                    operationType: operationType
                })).supplant({id: id, no: options.data.key.no}).supplant({
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
                                require('./server')('remove', {
                                    data: JSON.stringify(options.data.key),
                                    success: function (response) {
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
