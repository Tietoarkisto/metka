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

    return function (options) {
        delete options.field.displayType;

        var $containers = null;
        function appendRow(lang) {
            var transferRow = {
                key: options.field.key,
                removed: false,
                unapproved: true,
                fields: {
                    "country": {
                        key: "country",
                        type: "VALUE",
                        values: {}
                    },
                    "countryabbr": {
                        key: "countryabbr",
                        type: "VALUE",
                        values: {}
                    }
                }
            };

            transferRow.fields.country.values[lang] = {
                current: "Suomi"
            };
            transferRow.fields.countryabbr.values[lang] = {
                current: "FI"
            };

            options.$events.trigger('container-{key}-{lang}-push'.supplant({
                key: options.field.key,
                lang: lang
            }), [transferRow]);

            /*$containers.filter(function(i, contnr) {
                var $container = $(contnr);
                if ($container.data('lang') === lang) {
                    options.
                    $container.data('addRow')(transferRow);
                }
            });*/
        }

        return {
            preCreate: function (options) {
                var $elem = this;
                if(!options.buttons) {
                    options.buttons = [];
                }
                if(!options.buttons.some(function(button){
                        if(button.buttonId) {
                            return button.buttonId === options.field.key+"_addFinland"
                        }
                        return false;
                    })) {
                    options.buttons.push({
                        buttonId: options.field.key+"_addFinland",
                        onClick: function(options) {
                            return appendRow(options.lang);
                        },
                        title: MetkaJS.L10N.get('general.table.countries.addFinland')
                    });
                }
            },
            postCreate: function(options) {
                $containers = $(this).children();
            }
        }
    };
});
