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
                            onblur: function(contents, $editable) {
                                require('./data')(options).setByLang(lang, $(this).html());
                            },
                            lang: 'fi-FI'
                        });
                    }, 0);
                    return $input;
                }
            })();
            require('./data')(options).onChange(function () {
                $content
                    .html(require('./data')(options).getByLang(lang) || '');
            });
        } else {
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

                    require('./data')(options).onChange(function () {
                        function setValue() {
                            require('./reference').optionByPath(key, options, lang, function (value) {
                                $input.val(value);
                            })(options.data.fields, reference);
                        }

                        if (type === 'REFERENCE') {
                            log(options);
                            var reference = getPropertyNS(options, 'dataConf.references', options.fieldOptions.reference);
                            options.$events.on('data-changed-{key}-{lang}'.supplant({
                                key: reference.target,
                                lang: lang
                            }), setValue);
                            // TODO: setValue call is not necessary, if target is select input. select input triggers change event
                            setValue();
                        } else {
                            $input.val(type === 'CONCAT'
                                ?
                                options.fieldOptions.concatenate.map(function (key) {
                                    return require('./data')(options)(key).getByLang(lang);
                                }).join('')
                                :
                                require('./data')(options).getByLang(lang) || '');
                        }
                    });
                }
            }
        }


        return this;
    };
});
