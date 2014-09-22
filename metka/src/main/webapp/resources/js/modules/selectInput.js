define(function (require) {
    'use strict';

    return function ($input, options, lang, key) {
        function setOptions(selectOptions, list) {
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



        var selectionListKey = require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'selectionList');
        if (!selectionListKey) {
            return;
        }

        // TODO: prevent recursion
        (function selectInput(listKey) {
            function request(fieldValues) {
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
                        if (data.responses && data.responses.length && data.responses[0].options) {
                            setOptions(data.responses[0].options, list);
                        }
                        // enable input, if it was enabled before request
                        //$input.prop('disabled', isDisabled);
                    }
                });
            }

            var list = require('./utils/getPropertyNS')(options, 'dataConf.selectionLists', listKey);
            if (!list) {
                log('list not found', listKey, options);
            }
            if (list.type === 'SUBLIST') {
                return selectInput(list.sublistKey || list.key);
            }

            if (list.type === 'REFERENCE') {
                var reference = require('./utils/getPropertyNS')(options, 'dataConf.references', list.reference);
                if (reference && reference.type === 'DEPENDENCY') {
                    //log(options, reference.valuePath, require('./data')(options)(reference.target).getByLang(lang))
                    //log(options, require('./data')(options)(reference.target).getByLang(lang))
                    options.$events.on('data-change-{key}-{lang}'.supplant({
                        key: reference.target,
                        lang: lang
                    }), function (e, value) {
                        var fieldValues = {};
                        fieldValues[reference.target] = value;
                        request(fieldValues);
                    });
                } else {
                    // while in progress, disable input
                    //var isDisabled = $input.prop('disabled');
                    //$input.prop('disabled', true);

                    request();
                }
            } else {
                setOptions(list.options, list);
            }
        })(selectionListKey);
    };
});
