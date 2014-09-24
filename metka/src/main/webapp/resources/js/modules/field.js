define(function (require) {
    'use strict';

    var customFields = {
        custom_studyRelatedBinders: require('./custom/fields/studyRelatedBinders'),
        custom_studyErrors: require('./custom/fields/studyErrors'),
        custom_studyVariablesGrouped: require('./custom/fields/studyVariablesGrouped'),
        custom_studyVariablesGrouping: require('./custom/fields/studyVariablesGrouping'),
        files: require('./custom/fields/files'),
        variables: require('./custom/fields/variables')
    };

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
                            var isVisible = (!lang && currentLang === options.defaultLang) || lang === options.defaultLang || lang === currentLang;
                            $table.find('tr').children(':nth-child({i})'.supplant({
                                i: i + 1
                            })).toggleClass('hiddenByTranslationState', !isVisible);
                        });
                    } else {
                        // toggle visibility
                        $langField.toggleClass('hiddenByTranslationState', currentLang !== options.defaultLang);
                    }
                }
            }

            var type = options.field.displayType || require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'type');
            var $langField = $('<div>');
            if (!type) {
                log('field type is not set', key, options);
            } else {
                if (type === 'CONTAINER' || type === 'REFERENCECONTAINER') {
                    require('./containerField').call($langField, options, lang);
                } else {
                    if (type === 'BOOLEAN') {
                        require('./checkboxField').call($langField, options, lang);
                    } else {
                        require('./inputField').call($langField, options, type, lang);
                    }
                }

                addValidationErrorListener($langField, function () {
                    return require('./data')(options).errorsByLang(lang);
                });
            }
            $elem.append($langField);

            options.$events.on('translationLangChanged', onTranslationLangChange);
            onTranslationLangChange(undefined, $('input[name="translation-lang"]:checked').val() || options.defaultLang);
        }

        if(options.type === 'EMPTYCELL') {
            return this;
        }

        var $elem = this;

        //this.options.field.multichoice
        //this.options.field.showReferenceKey
        //this.options.field.showReferenceValue
        //this.options.field.handlerName

        //log(this.options.dataConf === x.dataConf)
        //log(this.options.dataConf.mo === x.dataConf.mo)

        var key = options.field.key;

        var customField = customFields[key];
        switch (typeof customField) {
            case 'object':
                $.extend(true, options, customField);
                break;
            case 'function':
                $.extend(true, options, customField(options));
                break;
        }

        var fieldDataOptions = require('./utils/getPropertyNS')(options, 'dataConf.fields', key) || {};

        createInput(options.defaultLang);

        if (fieldDataOptions.translatable && (options.translatable !== false)) {
            ['DEFAULT', 'EN', 'SV'].filter(function (lang) {
                return lang !== options.defaultLang;
            }).forEach(createInput);
        }

        // add TransferField error listener
        addValidationErrorListener(this, function () {
            return require('./data')(options).errors();
        });

        if (options.create) {
            options.create.call(this, options);
        }

        return this;
    };
});