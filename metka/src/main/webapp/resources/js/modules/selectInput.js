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
            setOptions(list.options);
        }

        if (list.freeTextKey) {
            var $freeText = $('<div>');
            require('./inherit')(require('./field'))(options).call($freeText, {
                horizontal: true,
                title: MetkaJS.L10N.localize(options.fieldTitles[list.freeTextKey], 'title'),
                field: {
                    displayType: 'STRING',
                    key: list.freeTextKey
                }
            });

            $field.append($freeText);
            var showFreeText = function () {
                $freeText.toggle(list.freeText.indexOf(require('./data')(options).getByLang(lang)) !== -1);
            };
            showFreeText();
            $input.change(showFreeText);
        }
    }
});
