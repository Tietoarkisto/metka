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

        function addValidationErrorListener($container, getErrors) {
            require('./data')(options).onChange(function () {
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
                }
            });
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

            createInput(options.defaultLang);
            if (options.fieldOptions.translatable && (options.translatable !== false)) {
                ['DEFAULT', 'EN', 'SV'].filter(function (lang) {
                    return lang !== options.defaultLang;
                }).forEach(createInput);
            }
            // add TransferField error listener
            addValidationErrorListener($elem, function () {
                return require('./data')(options).errors();
            });
            if (options.create) {
                options.create.call($elem, options);
            }
        }

        if (options.type === 'EMPTYCELL') {
            return this;
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