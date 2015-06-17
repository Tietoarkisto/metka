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
            setValue();
        }

        function setValue() {
            var value = require('./data')(options).getByLang(lang);
            if (typeof value !== 'undefined' && $input.children("option[value='" + value + "']").length) {
                $input.val(value);
            } else {
                $input.children().first().prop('selected', true);
                require('./data')(options).setByLang(lang, $input.val());
            }
            /*$input.change();*/
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
            // You can only empty the selection through trigger if it has a valid empty value
            if(list.includeEmpty) {
                options.$events.on('data-empty-{key}-{lang}'.supplant({
                    key: options.field.key,
                    lang: lang
                }), function() {
                    require('./data')(options).setByLang(lang, "");
                })
                options.$events.on('data-empty-{key}'.supplant({
                    key: options.field.key
                }), function() {
                    require('./data')(options).setByLang(lang, "");
                })
            }
            options.$events.on('data-changed-{key}-{lang}'.supplant({
                key: options.field.key,
                lang: lang
            }), setValue);

            setOptions(list.options);
        }

        if (list.freeTextKey) {
            var $freeText = $('<div>');
            // TODO: allow free text field configuration in the same way as extra dialog field configuration is done
            require('./inherit')(require('./field'))(options).call($freeText, {
                horizontal: true,
                //title: MetkaJS.L10N.localize(options.fieldTitles[list.freeTextKey], 'title'),
                fieldOptions: getPropertyNS(options, 'dataConf.fields', list.freeTextKey) || {},
                field: {
                    //displayType: 'STRING',
                    key: list.freeTextKey
                }
            });

            $field.append($freeText);
            var showFreeText = function () {
                var vis = list.freeText.indexOf(require('./data')(options).getByLang(lang)) !== -1;
                if(!vis) {
                    options.$events.trigger('data-empty-{key}'.supplant({
                        key: list.freeTextKey
                    }));
                }
                $freeText.toggle(vis);
            };
            showFreeText();
            $input.change(showFreeText);
        }
    }
});
