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

    return function ($input, options, lang, $field) {
        function setOptions(selectOptions) {
            $input.empty();
            if (list.includeEmpty === null || list.includeEmpty) {
                $input
                    .append($('<option>')
                        //.prop('disabled', true)
                        .val('')
                        .text(MetkaJS.L10N.get('general.selection.empty')));
            }
            if(selectOptions) {
                $input.append(selectOptions.map(function (option) {
                    return $('<option>')
                        .val(option.value)
                        .text(require('./selectInputOptionText')(option));
                }));
            }
            setValue();
        }

        function setValue() {
            var value = require('./data')(options).getByLang(lang);
            if (typeof value !== 'undefined' && $input.children("option[value='" + value + "']").length) {
                $input.val(value);
            } else {
                $input.children().first().prop('selected', true);
                require('./data')(options).setByLang(lang, $input.val());
            }
            /*$input.change();*/
        }

        var key = options.field.key;

        var list = require('./selectionList')(options, key);

        if (!list) {
            return;
        }

        if (list.type === 'REFERENCE') {
            var reference = getPropertyNS(options, 'dataConf.references', list.reference);
            if (!reference) {
                return;
            }
            var getOptions = require('./reference').optionsByPath(key, options, lang, setOptions);
            if (reference.type === 'DEPENDENCY') {
                options.$events.on('data-changed-{key}-{lang}'.supplant({
                    key: reference.target,
                    lang: lang
                }), function (e) {
                    getOptions(options.data.fields, reference);
                });
            } else {
                getOptions();
            }
        } else {
            // You can only empty the selection through trigger if it has a valid empty value
            if(list.includeEmpty) {
                options.$events.on('data-empty-{key}-{lang}'.supplant({
                    key: options.field.key,
                    lang: lang
                }), function() {
                    require('./data')(options).setByLang(lang, "");
                })
                options.$events.on('data-empty-{key}'.supplant({
                    key: options.field.key
                }), function() {
                    require('./data')(options).setByLang(lang, "");
                })
            }
            options.$events.on('data-changed-{key}-{lang}'.supplant({
                key: options.field.key,
                lang: lang
            }), setValue);

            setOptions(list.options);
        }

        if (list.freeTextKey) {
            var $freeText = $('<div>');
            // TODO: allow free text field configuration in the same way as extra dialog field configuration is done
            require('./inherit')(require('./field'))(options).call($freeText, {
                horizontal: true,
                //title: MetkaJS.L10N.localize(options.fieldTitles[list.freeTextKey], 'title'),
                fieldOptions: getPropertyNS(options, 'dataConf.fields', list.freeTextKey) || {},
                field: {
                    //displayType: 'STRING',
                    key: list.freeTextKey
                }
            });

            $field.append($freeText);
            var showFreeText = function () {
                var vis = list.freeText.indexOf(require('./data')(options).getByLang(lang)) !== -1;
                if(!vis) {
                    options.$events.trigger('data-empty-{key}'.supplant({
                        key: list.freeTextKey
                    }));
                }
                $freeText.toggle(vis);
            };
            showFreeText();
            $input.change(showFreeText);
        }
    }
});
