$.widget('metka.metkaOptions', {
    select: function () {
        'use strict';
        var key = this.options.field.key;
        var $input = this.element;
        function setOptions(options) {
            options.forEach(function (option, i) {
                $input.append($('<option>', {
                    value: option.value
                })
                    // TODO: set text and value
                    //.text(MetkaJS.L10N.localize(MetkaJS.JSGUIConfig[MetkaJS.Globals.page.toUpperCase()].fieldTitles[cell.field.key][v], 'title')));
                    .text(option.title ? option.title.value : JSON.stringify(option)));
            });
            var value = MetkaJS.Data.get(key);
            if (typeof value !== 'undefined') {
                $input.val(value);
            } else {
                $input.children().first().prop('selected', true);
            }
        }
        var selectionListKey = MetkaJS.JSConfig[MetkaJS.Globals.page.toUpperCase()].fields[key].selectionList;
        // TODO: prevent recursion
        (function selectInput(key2) {
            var list = MetkaJS.objectGetPropertyFromNS(MetkaJS, 'JSConfig', MetkaJS.Globals.page.toUpperCase(), 'selectionLists', key2);
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
                $input.change(function () {
                    console.log(arguments);
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

                // TODO: while in progress, disable input
                $.ajax({
                    type: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    dataType: 'json',
                    url: MetkaJS.PathBuilder().add('references').add('collectOptionsGroup').build(),
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

                            if (!response.options) {
                                return;
                            }

                            setOptions(response.options);
                            // TODO: enable input
                        });
                    }
                });
            } else {
                setOptions(list.options);
            }
        })(selectionListKey);
    }
});