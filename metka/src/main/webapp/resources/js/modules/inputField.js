define(function (require) {
    'use strict';

    return function (options, type, lang) {
        var id = require('./autoId')();
        var key = options.field.key;
        var $label = require('./label')(options, lang)
            .attr('for', id);

        var $field = (function () {
            if (options.horizontal) {

                // horizontal label should be 2 boostrap main columns wide, even when it's inside column
                var labelWidth = 2 * (options.parent.parent.columns || 1) / (options.colspan || 1);

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
            id: id
        };
        var isSelection = type === 'SELECTION';
        var nodeType = (function () {
            if (isSelection) {
                return 'select';
            }

            if (options.field.multiline) {
                elemOptions.rows = '4';
                return 'textarea';
            }

            // STRING, INTEGER, CONCAT

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
                    require('./data')(options).setByLang(lang, $(this).val());
                });

            if (isSelection) {
                require('./selectInput').call($input, options, lang);
            } else {
                // textarea or input elements

                require('./data')(options).onChange(function () {
                    var dataConf = options.dataConf.fields[key];
                    if (dataConf && dataConf.type === 'REFERENCE') {



                        var reference = require('./utils/getPropertyNS')(options, 'dataConf.references', dataConf.reference);
                        options.$events.on('data-change-{key}-{lang}'.supplant({
                            key: reference.target,
                            lang: lang
                        }), function (e, value) {
                            var fieldValues = {};
                            fieldValues[reference.target] = value;
                            log(reference, dataConf)
                            fieldValues['keywordvocab'] = 'Test 1';
                            log('va',value)
                            require('./server')('options', {
                                data: JSON.stringify({
                                    requests : [{
                                        key: key,
                                        confType: options.dataConf.key.type,
                                        confVersion: options.dataConf.key.version,
                                        language: lang,
                                        fieldValues: fieldValues
                                    }]
                                }),
                                success: function (data) {
                                    function optionText(option) {
                                        if (MetkaJS.L10N.hasTranslation(option, 'title')) {
                                            return MetkaJS.L10N.localize(option, 'title');
                                        }

                                        if (option.title) {
                                            if (option.title.type === 'LITERAL') {
                                                return option.title.value;
                                            }
                                        }

                                        return option.value;
                                    }
                                    if (data.responses && data.responses.length && data.responses[0].options && data.responses[0].options[0]) {
                                        $input.val(optionText(data.responses[0].options[0]));
                                    }
                                }
                            });
                        });



                    } else {
                        $input.val(type === 'CONCAT'
                            ?
                            dataConf.concatenate.map(function (key) {
                                return require('./data')(options)(key).getByLang(lang);
                            }).join('')
                            :
                            require('./data')(options).getByLang(lang) || '');

                    }
                });
            }

            $field.append($input);
        }
    };
});
