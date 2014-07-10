(function () {
    'use strict';

    $.widget('metka.metkaInput', $.metka.metka, {
        _create: function () {
            this.element
                .toggleClass('alert-warning', this.options.important);
        },
        select: function () {
            var key = this.options.field.key;
            var $input = this.element;

            var selectionListKey = MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[key].selectionList;
            // TODO: prevent recursion
            (function selectInput(listKey) {
                function setOptions(options) {
                    $input.append(options.map(function (option, i) {
                        return $('<option>')
                            .val(option.value)
                            .text(
                                MetkaJS.objectGetPropertyNS(option, 'title.value')
                                ||
                            MetkaJS.L10N.get([MetkaJS.Globals.page.toUpperCase(), listKey, option.value].join('.')));
                    }));
                    var value = MetkaJS.Data.get(key);
                    if (typeof value !== 'undefined') {
                        $input.val(value);
                    } else {
                        $input.children().first().prop('selected', true);
                    }
                }

                var list = MetkaJS.objectGetPropertyNS(MetkaJS, 'JSConfig', MetkaJS.Globals.page.toUpperCase(), 'selectionLists', listKey);
                if (list.type === 'SUBLIST') {
                    return selectInput(list.key);
                }

                if (list.includeEmpty === null || list.includeEmpty) {
                    $input
                        .append($('<option>')
                            .prop('disabled', true)
                            .text(MetkaJS.L10N.get('general.list.empty')));
                }

                if (list.type === 'REFERENCE') {
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

                    // while in progress, disable input
                    var isDisabled = $input.prop('disabled');
                    $input.prop('disabled', true);

                    $.ajax({
                        type: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        dataType: 'json',
                        url: MetkaJS.url('options'),
                        data: JSON.stringify({
                            key: undefined,
                            requests : [{
                                key: key,
                                container: undefined,
                                confType: MetkaJS.JSConfigUtil.getConfigurationKey().type,
                                confVersion: MetkaJS.JSConfigUtil.getConfigurationKey().version,
                                dependencyValue: undefined
                            }]
                        }),
                        success: function (data) {
                            data.responses.forEach(function (response) {
                                if (response.messages) {
                                    response.messages.forEach(function (message) {
                                        MetkaJS.MessageManager.push(MetkaJS.MessageManager.Message(message.title, message.message));
                                        message.data.forEach(function (data) {
                                            MetkaJS.MessageManager.topMessage().pushData(data);
                                        });
                                    });
                                    MetkaJS.MessageManager.showAll();
                                }

                                setOptions(response.options);
                                // enable input, if it was enabled before request
                                $input.prop('disabled', isDisabled);
                            });
                        }
                    });
                } else {
                    setOptions(list.options);
                }
            })(selectionListKey);
        }
    });
})();
