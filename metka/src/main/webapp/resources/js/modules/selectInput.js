define(function (require) {
    'use strict';

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

    function getSelectOptions(options, lang, transferRow, key, callback) {
        var selectionListKey = require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'selectionList');
        if (!selectionListKey) {
            return;
        }

        // TODO: prevent recursion
        (function selectInput(listKey) {
            function set(fieldValues) {
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
                            callback(data.responses[0].options, list);
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
                    if (transferRow) {
                        // table row
                        var fieldValues = {};
                        fieldValues[reference.target] = transferRow.fields[reference.target].values.DEFAULT.current;
                        set(fieldValues);
                    } else {

                        //log(options, reference.valuePath, require('./data')(options)(reference.target).getByLang(lang))
                        //log(options, require('./data')(options)(reference.target).getByLang(lang))
                        options.$events.on('data-change-{key}-{lang}'.supplant({
                            key: reference.target,
                            lang: lang
                        }), function (e, value) {
                            var fieldValues = {};
                            fieldValues[reference.target] = value;
                            set(fieldValues);
                        });

                        /*var reference = MetkaJS.JSConfigUtil.getReference(list.reference);
                         if (reference.type === MetkaJS.E.Ref.DEPENDENCY) {

                         var depField = MetkaJS.JSConfigUtil.getField(reference.target);
                         // TODO: Container fields pose a problem since we can have multiple dependency fields with the same key, albeit a different id, at the same time. Maybe we should listen to input id of the notifier also.
                         MetkaJS.EventManager.listen(MetkaJS.E.Event.FIELD_CHANGE, field.key, depField.key, referenceInputCallback(field.key, context, null));
                         var input = null;
                         switch (depField.type) {
                         case MetkaJS.E.Field.REFERENCE:
                         case MetkaJS.E.Field.STRING:
                         case MetkaJS.E.Field.SELECTION:
                         // TODO: We are assuming here that current field and dependency field are both top level fields. This should get generalises at some point.

                         // With selection we don't really care about if the selection is REFERENCE etc. since we only want the current value.
                         // Server can make better judgements on how to use that value.
                         if (!depField.subfield) {
                         // If dependency field is not a subfield then try to get a top level field
                         input = MetkaJS.getValuesInput(depField.key);
                         } else if (depField.subfield && container !== null) {
                         // If dependency field is a subfield and there is a container then try to get a field within that container
                         // There should be only one instance of given field at any moment and it should be from the row that is currently open.
                         input = $('#' + container + 'Field' + depField.key);
                         }
                         break;
                         }
                         if (input.length !== 0) {
                         dependencyValue = input.val();
                         }
                         }*/
                    }
                } else {
                    // while in progress, disable input
                    //var isDisabled = $input.prop('disabled');
                    //$input.prop('disabled', true);

                    set();
                }
            } else {
                callback(list.options, list);
            }
        })(selectionListKey);
    }

    function SelectInput(options, lang) {
        var $input = this;
        getSelectOptions(options, lang, undefined, options.field.key, function (selectOptions, list) {
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
                    .text(optionText(option));
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
        });
    }

    SelectInput.optionText = function (options, lang, column, transferRow, value, callback) {
        getSelectOptions(options, lang, transferRow, column, function (selectOptions, list) {
            var option = selectOptions.find(function (option) {
                return option.value === value;
            });
            callback(option ? optionText(option) : undefined);
        });
    };

    return SelectInput;
});
