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

    return function (options, type, lang) {
        var getPropertyNS = require('./utils/getPropertyNS');
        var id = require('./autoId')();
        var key = options.field.key;
        var $label = require('./label')(options, lang)
            .attr('for', id);

        function emptyField() {
            require('./data')(options).setByLang(lang, "");
        }

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

        var elemOptions = {
            'class': 'form-control',
            id: id,
            'data-metka-field-key': options.field.key,
            'data-metka-field-lang': lang
        };
        var isSelection = type === 'SELECTION';


        if (type === 'RICHTEXT') {
            var $input = require('./input').call($('<div>'), options);
            $field.append($input);

            var $content = (function () {
                if (require('./isFieldDisabled')(options, lang)) {
                    return $input
                        .addClass('form-control richtext')
                        .attr('disabled', true);
                } else {
                    setTimeout(function() {
                        $input.summernote({
                            height: 200,
                            toolbar: [
                                ['style', ['bold', 'italic', 'underline', 'clear']],
                                ['font', ['strikethrough']],
                                ['fontsize', ['fontsize']],
                                ['color', ['color']],
                                ['para', ['ul', 'ol', 'paragraph']],
                                ['height', ['height']],
                                ['insert', ['link']],
                                ['misc', ['fullscreen', 'codeview']]
                            ],
                            htmlMode:false,
                            mode:"text/xhtml",
                            onblur: function(contents, $editable) {
                                require('./data')(options).setByLang(lang, $(this).html());
                            },
                            lang: 'fi-FI'
                        });
                    }, 0);
                    return $input;
                }
            })();

            $content.html(require('./data')(options).getByLang(lang) || '');

        } else {
            var nodeType = (function () {
                if (isSelection) {
                    return 'select';
                }

                if (options.field.multiline) {
                    elemOptions.rows = '4';
                    return 'textarea';
                }

                // STRING, INTEGER, ...

                elemOptions.type = 'text';

                return 'input';
            })();

            var $input = require('./input').call($('<' + nodeType + '>', elemOptions), options);

            if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
                require('./datetime').call($field, options, type, $input, lang);
            } else {
                $input
                    .prop('disabled', require('./isFieldDisabled')(options, lang))
                    .change(function () {
                        if(!!options.allowChange) {
                            options.allowChange({
                                current: require('./data')(options).getByLang(lang),
                                change: $(this).val(),
                                reverseChange: function() {
                                    $input.val(require('./data')(options).getByLang(lang));
                                },
                                performChange: function(newVal) {
                                    $input.val(newVal);
                                    require('./data')(options).setByLang(lang, newVal);
                                }
                            });
                        } else {
                            require('./data')(options).setByLang(lang, $(this).val());
                        }
                    });

                $field.append($input);

                if (isSelection) {
                    require('./selectInput')($input, options, lang, $field);
                } else {
                    // textarea or input elements

                    var changeTriggerKey = 'data-changed-{key}-{lang}'.supplant({
                        key: options.fieldOptions.key,
                        lang: lang
                    });

                    // Empties language specific field
                    var emptyTriggerLangKey = 'data-empty-{key}-{lang}'.supplant({
                        key: options.fieldOptions.key,
                        lang: lang
                    });

                    // Empties field of every language
                    var emptyTriggerKey = 'data-empty-{key}'.supplant({
                        key: options.fieldOptions.key
                    });

                    // Each input type can be emptied using triggers, although you have to be careful with reference fields, especially dependencies
                    options.$events.register(emptyTriggerLangKey, emptyField);
                    options.$events.register(emptyTriggerKey, emptyField);
                    if (type === 'REFERENCE') {
                        var reference = getPropertyNS(options, 'dataConf.references', options.fieldOptions.reference);
                        changeTriggerKey = 'data-changed-{key}-{lang}'.supplant({
                            key: reference.target,
                            lang: lang
                        });
                        options.$events.on(changeTriggerKey, function() {
                            require('./reference').optionByPath(key, options, lang, function (option) {
                                $input.val(require('./selectInputOptionText')(option));
                            })(options.data.fields, reference);
                        });
                    } else {
                        options.$events.on(changeTriggerKey, function() {
                            $input.val(require('./data')(options).getByLang(lang) || '');
                        });
                    }
                    options.$events.trigger(changeTriggerKey);
                }
            }
        }

        return this;
    };
});
