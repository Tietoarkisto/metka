define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function ($input, options, lang, key) {
        var selectionListKey = getPropertyNS(options, 'dataConf.fields', key, 'selectionList');
        if (!selectionListKey) {
            return;
        }

        // TODO: prevent infinite recursion
        (function selectInput(listKey) {
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

            var list = getPropertyNS(options, 'dataConf.selectionLists', listKey);

            if (!list) {
                log('list not found', listKey, options);
            }
            if (list.type === 'SUBLIST') {
                return selectInput(list.sublistKey || list.key);
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
        })(selectionListKey);
    };
});
