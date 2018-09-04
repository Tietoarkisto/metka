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

    return function (options, lang, rows) {
        //var PAGE = require('./../metka').PAGE;
        return function (type, button) {
            return function (transferRow, onClose) {

                var modalOptions = $.extend(true, require('./optionsBase')(options), (require('./isFieldDisabled')(options, lang) ? {
                    type: 'VIEW',
                    buttons: [{
                        type: 'DISMISS'
                    }]
                } : {
                    type: type.toUpperCase(),
                    buttons: [
                        {
                            title: MetkaJS.L10N.get('general.buttons.' + button),
                            create: function () {
                                this
                                    .click(function () {
                                        $.extend(transferRow, modalOptions.data);
                                        onClose(transferRow);
                                    });
                            }
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                }), {
                    // copy data, so if dialog is dismissed, original data won't change
                    data: $.extend(true, {}, transferRow),
                    containerKey: options.field.key,
                    dataConf: options.dataConf,
                    defaultLang: options.fieldOptions.translatable ? lang : options.defaultLang,
                    fieldTitles: options.fieldTitles,
                    dialogTitle: options.field.dialogTitle,
                    dialogTitles: options.dialogTitles,
                    title: 'Muokkaa',  // Issue #769
                    subfieldConfiguration: options.subfieldConfiguration,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: rows()
                        }
                    ]
                });

                // if not translatable container and has translatable subfields, show language selector
                if (!options.fieldOptions.translatable && require('./containerHasTranslatableSubfields')(options)) {
                    modalOptions.translatableCurrentLang = $('input[name="translation-lang"]:checked').val() || MetkaJS.User.role.defaultLanguage.toUpperCase();
                }

                if(options.prepareModal) {
                    options.prepareModal(modalOptions);
                }
                // Issue #769
                if (modalOptions.type === 'ADD') {
                    modalOptions.title = "Lisää";
                }
                require('./modal')(modalOptions);
                //require('./modal')($.extend(true, {}, options, modalOptions));
            };
        }
    };
});
