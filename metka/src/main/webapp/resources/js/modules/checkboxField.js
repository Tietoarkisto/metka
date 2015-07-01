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

    return function (options, lang) {
        var getPropertyNS = require('./utils/getPropertyNS');
        var reverseBoolean = !!options.field.reverseBoolean;
        var id = require('./autoId')();
        var $label = require('./label')(options, lang)
            .attr('for', id);
        var elemOptions = {
            'class': 'form-control',
            id: id,
            'data-metka-field-key': options.field.key,
            'data-metka-field-lang': lang,
            'type': 'checkbox'
        };

        var $input = $('<input>', elemOptions)
            .prop('disabled', require('./isFieldDisabled')(options))
            .change(function () {
                require('./data')(options).setByLang(lang, reverseBoolean !== $(this).prop('checked'));
            });

        var value = require('./data')(options).getByLang(lang);
        var boolValue = typeof value === 'string' ? value.bool() : !!value;
        $input.prop('checked', reverseBoolean !== boolValue);

        var $field = (function () {
            if (options.horizontal) {

                // horizontal label should be 2 boostrap main columns wide, even when it's inside column
                var labelWidth = 2 * (getPropertyNS(options, 'parent.parent.columns') || 1) / (options.colspan || 1);

                $label.addClass('col-xs-' + labelWidth);

                var $inputWrapper = $('<div>')
                    .addClass('col-xs-' + (12 - labelWidth));

                this
                    .append($('<div class="form-horizontal">')
                        .append($('<div class="form-group">')
                            .append($label)
                            .append($inputWrapper)));

                return $inputWrapper;
            } else {
                return this.append($label);
            }
        }).call(this);

        $field.append(require('./input').call($input, options));
    };
});
