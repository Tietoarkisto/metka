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

    /**
     * Creates language specific field element(s) inside this element,
     * detects field type and creates input element inside each language specific element.
     *
     * @this {jQuery} Cell element
     * @param {object} options UI configuration of this field, with properties for accessing data, configurations etc.
     * @return {jQuery} Object for chaining.
     */
    return function (options) {
        var $elem = this;
        //var errorMsgList = new Array();
        var labels = new Array();
        var text = "";
        var result = new Array();

        // Fire an event on any change in any input field
        $elem.on('keypress change input', function() {
            var evt = new CustomEvent('unsavedChanges');
            window.dispatchEvent(evt);
        });

        function storeErrorLabels(sentence) {
            labels.push(sentence);
        }

        function removeDublicates() {
            return labels.reduce(function(a,b){
                if (a.indexOf(b) < 0 ) a.push(b);
                return a;
            },[]);
        }

        function separateErrorLabels(message) {
            if (message.length > 0) {
                for(var i = 0; i <= message.length; i++) {
                    if(/[A-Z]/.test(message.charAt(i))) {
                        var error = message.charAt(i);
                        for(var j = i + 1; (j < message.length && !/[A-Z]/.test(message.charAt(j))); j++){
                            error += message.charAt(j);
                            i = j;
                        }
                        storeErrorLabels(error);
                    }
                }
            }
            return removeDublicates();
        }

        function addValidationErrorListener($container, getErrors) {
            $container.children('.help-block').remove();
            var errors = getErrors();
            // TODO: if saving, show warning/warning instead of error/danger
            if (errors.length) {
                $container.addClass('has-error');
                var $p = $('<p class="help-block">');
                $container.append($p);
                //$p.append(require('./dataValidationErrorText')(errors));
                require('./dataValidationErrorText')(errors, function(text) {
                    $p.append(text+"<br>");
                });
                if(options.horizontal) {
                    $p.addClass('col-sm-offset-'+(2 * (getPropertyNS(options, 'parent.parent.columns') || 1) / (options.colspan || 1)));
                }
                // Issue #587
                setTimeout(function() {
                    text = $('.has-error').find('label').text();
                    result = separateErrorLabels(text);
                    for(var i = 0; i < result.length; i++) {
                        require('./modal')($.extend(true, require('./optionsBase')(), {
                            title: MetkaJS.L10N.get('alert.notice.title'),
                            body: MetkaJS.L10N.get('alert.notice.reference').supplant({
                                key: result[i]
                            }),
                            buttons: [{
                                type: 'DISMISS'
                            }]
                        }));
                    }
                }, 0);
            }
        }

        function createInputWrapper(key) {
            function createInput(lang) {
                function onTranslationLangChange(e, currentLang) {
                    if (options.fieldOptions.translatable) {
                        var isVisible = lang === options.defaultLang || lang === currentLang;
                        $langField.toggleClass('hiddenByTranslationState', !isVisible);
                        if (isVisible) {
                            if (lang === currentLang) {
                                // TODO: enable field',lang, key)
                            } else {
                                // TODO: disable field',lang, key)
                            }
                        }
                    } else {
                        // non-translatable field

                        if ((type === 'CONTAINER' || type === 'REFERENCECONTAINER') && require('./containerHasTranslatableSubfields')(options)) {
                            var $table = $langField.find('table');
                            $table.find('> thead > tr > th').each(function (i) {
                                var lang = $(this).data('lang');
                                if (lang) {
                                    var isVisible = (!lang && currentLang === options.defaultLang) || lang === options.defaultLang || lang === currentLang;
                                    $table.find('tr').children(':nth-child({i})'.supplant({
                                        i: i + 1
                                    })).toggleClass('hiddenByTranslationState', !isVisible);
                                }
                            });
                        } else {
                            // if type is selection and free text field is translatable
                            if (type === 'SELECTION') {
                                var list = require('./selectionList')(options, options.field.key);
                                if (list) {
                                    var freeTextKey = list.freeTextKey;
                                    if (freeTextKey) {
                                        if (options.dataConf.fields[freeTextKey] && options.dataConf.fields[freeTextKey].translatable) {
                                            // don't toggle
                                            return;
                                        }
                                    }
                                }
                            }

                            // toggle visibility
                            $langField.toggleClass('hiddenByTranslationState', currentLang !== options.defaultLang);
                        }
                    }
                }

                var $langField = $('<div>');
                var type = options.field.displayType || options.fieldOptions.type;
                if (!type) {
                    log('field type is not set', key, options);
                    // Issue #832
                    require('./modal')($.extend(true, require('./optionsBase')(), {
                        title: MetkaJS.L10N.get('alert.error.title'),
                        body: MetkaJS.L10N.get('alert.error.reference').supplant({
                            key: key
                        }),
                        buttons: [{
                            type: 'DISMISS'
                        }]
                    }));
                } else {
                    (function () {
                        if (type === 'CONTAINER' || type === 'REFERENCECONTAINER') {
                            return require('./containerField').call($langField, options, lang);
                        }
                        if (type === 'BOOLEAN') {
                            return require('./checkboxField').call($langField, options, lang);
                        }
                        if (type === 'LINK') {
                            return require('./linkField')($langField, options, lang);
                        }
                        if(type === 'CUSTOM_JS') {
                            return;
                        }
                        // Issuet #639 & #640 & #646
                        if (options.field.key === 'relatedstudies_select' || options.field.key === 'studies_select' || (options.field.key === 'studyid' && options.fieldOptions.type === 'SELECTION') || (options.field.key === 'study' && options.fieldOptions.type === 'SELECTION')) {
                            //setTimeout(function() {
                            /*var modalTitles = document.getElementsByClassName("modal-title");
                            console.log("modalTitles.length: " + modalTitles.length);
                            var modalTitleArray = Array.prototype.slice.call(modalTitles);
                            if (modalTitleArray[0].innerHTML !== 'Mapitus') {
                                console.log("modalTitle: " + modalTitleArray[0].innerHTML);*/
                                var $searchInput = $('<input type="text">')
                                    .attr('id', 'myInput')
                                    .attr('placeholder', 'Hae...')
                                    .val('')
                                    .keyup(function() {
                                        var input, filter, div, a, i;
                                        input = document.getElementById("myInput");
                                        filter = input.value.toUpperCase();
                                        div = document.getElementById("METKA_UI_999");
                                        a = div.getElementsByTagName("option");
                                        for (i = 0; i < a.length; i++) {
                                            if (a[i].innerHTML.toUpperCase().indexOf(filter) > -1) {
                                                a[i].style.display = "";
                                            } else {
                                                a[i].style.display = "none";
                                            }
                                        }
                                    });
                                $langField.append($searchInput);
                                $langField.append('<br>');
                        }
                            //}, 200);
                            //}
                        require('./inputField').call($langField, options, type, lang);
                    })();

                    addValidationErrorListener($langField, function () {
                        return require('./data')(options).errorsByLang(lang);
                    });
                }
                $elem.append($langField);

                options.$events.on('translationLangChanged', onTranslationLangChange);

                // FIXME: Modals without translatable content won't have any content when some other language is selected
                if(!options.ignoreTranslate) {
                    onTranslationLangChange(undefined, $('input[name="translation-lang"]:checked').val() || (function r(options) {
                        return options && (options.translatableCurrentLang || r(options.parent));
                    })(options) || options.defaultLang);
                }
            }

            // All modifications to options should be here. At this point the actual input doesn't yet exist
            if (options.preCreate) {
                options.preCreate.call($elem, options);
            }

            createInput(options.defaultLang);
            if (options.fieldOptions.translatable && (options.translatable !== false)) {
                MetkaJS.Languages.filter(function (lang) {
                    return lang !== options.defaultLang;
                }).forEach(createInput);
            }
            // add TransferField error listener
            addValidationErrorListener($elem, function () {
                return require('./data')(options).errors();
            });

            // All modifications to the actual inputs should be here, it's too late to modify the options except to add triggers
            if (options.postCreate) {
                options.postCreate.call($elem, options);
            }
        }

        if (options.type === 'EMPTYCELL') {
            return this;
        }
        if(options.field.key === 'errorscore' || options.field.key === 'errordatasetpart' || options.field.key === 'errorpartsection' || options.field.key === 'errorlanguage' || options.field.key === 'errorlabel' || options.field.key === 'errortriggerdate' || options.field.key === 'errortriggerpro') {
            options.fieldOptions.type = 'STRING';
        }
        if(options.field.displayType === 'CUSTOM_JS' && options.field.key) {
            require(['./custom/fields/'+options.field.key], function(customField) {
                switch (typeof customField) {
                    case 'object':
                        $.extend(true, options, customField);
                        break;
                    case 'function':
                        $.extend(true, options, customField(options));
                        break;
                }
                // Let's fetch fieldOptions again since custom code might have changed something affecting this.
                $.extend(options, {
                    fieldOptions: getPropertyNS(options, 'dataConf.fields', options.field.key) || {}
                });
                createInputWrapper(options.field.key);
            });
        } else {
            createInputWrapper(options.field.key);
        }

        return this;
    };
});