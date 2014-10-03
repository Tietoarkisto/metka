define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options) {

        function addValidationErrorListener($container, getErrors) {
            require('./data')(options).onChange(function () {
                $container.children('.help-block').remove();
                var errors = getErrors();
                // TODO: if saving, show warning/warning instead of error/danger
                if (errors.length) {
                    $container.addClass('has-error');
                    $container.append($('<p class="help-block">')
                        .append(require('./dataValidationErrorText')(errors)));
                }
            });
        }

        function createInputWrapper() {
            fieldDataOptions = getPropertyNS(options, 'dataConf.fields', key) || {};
            createInput(options.defaultLang);
            if (fieldDataOptions.translatable && (options.translatable !== false)) {
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

        function createInput(lang) {
            function onTranslationLangChange(e, currentLang) {
                if (fieldDataOptions.translatable) {
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
                        // toggle visibility
                        $langField.toggleClass('hiddenByTranslationState', currentLang !== options.defaultLang);
                    }
                }
            }

            var $langField = $('<div>');
            var type = options.field.displayType || getPropertyNS(options, 'dataConf.fields', key, 'type');
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
            onTranslationLangChange(undefined, $('input[name="translation-lang"]:checked').val() || options.defaultLang);
        }

        if (options.type === 'EMPTYCELL') {
            return this;
        }
        var key = options.field.key;

        var $elem = this;

        var fieldDataOptions;

        if(options.field.displayType === 'CUSTOM_JS') {
            require(['./custom/fields/'+key], function(customField) {
                switch (typeof customField) {
                    case 'object':
                        $.extend(true, options, customField);
                        break;
                    case 'function':
                        $.extend(true, options, customField(options));
                        break;
                }
                createInputWrapper();
            });
        } else {
            createInputWrapper();
        }

        return this;
    };
});