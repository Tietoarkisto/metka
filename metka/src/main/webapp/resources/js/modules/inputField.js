define(function (require) {
    'use strict';

    return function (options, type, lang) {
        var getPropertyNS = require('./utils/getPropertyNS');
        var id = require('./autoId')();
        var key = options.field.key;
        var $label = require('./label')(options, lang)
            .attr('for', id);

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

            $field.append($input);

            if (isSelection) {
                require('./selectInput')(options, key, function (list) {
                    function setOptions(selectOptions) {
                        $input.empty();
                        if (list.includeEmpty === null || list.includeEmpty) {
                            $input
                                .append($('<option>')
                                    //.prop('disabled', true)
                                    .val('')
                                    .text(MetkaJS.L10N.get('general.selection.empty')));
                        }
                        $input.append(selectOptions.map(function (option, i) {
                            return $('<option>')
                                .val(option.value)
                                .text(require('./selectInputOptionText')(option));
                        }));
                        require('./data')(options).onChange(function () {
                            var value = require('./data')(options).getByLang(lang);
                            if (typeof value !== 'undefined' && $input.children('option[value="' + value + '"]').length) {
                                $input.val(value);
                            } else {
                                $input.children().first().prop('selected', true);
                            }
                            $input.change();
                        });
                    }

                    if (!list) {
                        return;
                    }

                    if (list.type === 'REFERENCE') {
                        var reference = getPropertyNS(options, 'dataConf.references', list.reference);
                        var getOptions = require('./reference').options(key, options, lang, setOptions);
                        if (reference && reference.type === 'DEPENDENCY') {
                            options.$events.on('data-change-{key}-{lang}'.supplant({
                                key: reference.target,
                                lang: lang
                            }), function (e) {
                                getOptions(options.data.fields, reference);
                            });
                        } else {
                            getOptions();
                        }
                    } else {
                        setOptions(list.options);
                    }
                    /*
                     var $freeText = require('./inherit')(function (options) {
                     return require('./inputField').call($('<div>'), options, 'STRING', lang);
                     })(options)({
                     horizontal: true,
                     title: 'Muu arvo',
                     field: {
                     key: list.freeTextKey
                     }
                     });

                     $field.append($freeText);
                     function showFreeText() {
                     $freeText.toggle(list.freeText.indexOf(require('./data')(options).getByLang(lang)) !== -1);
                     }
                     showFreeText();
                     $input.change(showFreeText);*/
                });
            } else {
                // textarea or input elements

                require('./data')(options).onChange(function () {
                    function setValue() {
                        require('./reference').option(key, options, lang, function (value) {
                            $input.val(value);
                        })(options.data.fields, reference);
                    }
                    var dataConf = getPropertyNS(options, 'dataConf.fields', key);
                    if (dataConf && dataConf.type === 'REFERENCE') {
                        var reference = getPropertyNS(options, 'dataConf.references', dataConf.reference);
                        options.$events.on('data-change-{key}-{lang}'.supplant({
                            key: reference.target,
                            lang: lang
                        }), setValue);
                        // TODO: setValue call is not necessary, if target is select input. select input triggers change event
                        setValue();
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
        }
        return this;
    };
});
