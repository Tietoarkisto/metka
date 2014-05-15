(function () {
    'use strict';

    MetkaJS.ReferenceHandler = (function() {
        /**
         * Builds a function for reference input (non container) related callbacks
         * @param key
         * @param context
         * @param dependencyValue
         * @returns {Function}
         */
        function referenceInputCallback(key, context, dependencyValue) {
            return function (notifier) {
                referenceHandlerChooser(key, context, dependencyValue, notifier.container);
            };
        }

        /**
         * Builds a function for reference container related callbacks
         * @param key
         * @param context
         * @param dependencyValue
         * @returns {Function}
         */
        function referenceContainerCallback(key, context, dependencyValue) {
            return function (notifier) {
                if (notifier.id === dependencyValue) {
                    referenceHandlerChooser(key, context, dependencyValue, notifier.container);
                }
            };
        }

        /**
         * Request object for reference options group requests.
         *
         * @param key Field key of the field making the group request (usually a referencecontainer)
         * @constructor
         */
        function ReferenceOptionsGroupRequest(key) {
            this.key = key;
            this.requests = [];
        }

        /**
         * Request object for reference options request
         *
         * @param key
         * @param container
         * @param confType
         * @param confVersion
         * @param dependencyValue
         * @constructor
         */
        function ReferencyOptionsRequest(key, container, confType, confVersion, dependencyValue) {
            this.key = key;
            this.container = container;
            this.confType = confType;
            this.confVersion = confVersion;
            this.dependencyValue = dependencyValue;
        }

        /**
         * Decides what reference handler to use for given field key.
         * Doesn't make deep analysis of if the field has to be handled at all,
         * just passes it along to the most likely handler which will then make a better decision.
         * Can be used as a general catch all where field keys are just fed to the function and
         * something happens if they are applicable.
         *
         * @param key Field key of the field to be handled
         * @param context Configuration context of the field
         * @param dependencyValue Possible dependency value for the field
         * @param container Field key of the possible container of the field
         */
        function referenceHandlerChooser(key, context, dependencyValue, container) {
            var field = MetkaJS.JSConfigUtil.getField(key);
            if (field === null) {
                // Sanity check, field has to exist for this to make sense.
                return;
            }

            switch (field.type) {
                case MetkaJS.E.Field.REFERENCECONTAINER:
                    // Collects
                    if (container === null) {
                        referenceContainer(field, context, dependencyValue);
                    } /*else {
                 // TODO: Handle reference container within a container
                 }*/
                    break;
                case MetkaJS.E.Field.CONTAINER:
                    // For now, do nothing
                    break;
                default:
                    // Make single request with parameters related to given field
                    referenceInput(field, context, dependencyValue, container);
                    break;
            }
        };

        function referenceContainer(field, context, dependencyValue) {
            if (field.subfield === true) {
                // Let's not handle recursive referencecontainers just yet.
                return;
            } else if (field.type !== MetkaJS.E.Field.REFERENCECONTAINER) {
                // Sanity check. This function is meant only for referencecontainers
                return;
            } else if (dependencyValue === null) {
                // Sanity check, this function is meant to handle one specific row at a time.
                // It no dependencyValue is provided then we can not make the request.
                return;
            }

            var group = new ReferenceOptionsGroupRequest(field.key);
            var i, length;
            for (i = 0, length = field.subfields.length; i < length; i++) {
                group.requests.push(new ReferencyOptionsRequest(
                    field.subfields[i],
                    null,
                    MetkaJS.JSConfigUtil.getConfigurationKey().type,
                    MetkaJS.JSConfigUtil.getConfigurationKey().version,
                    dependencyValue
                ));
            }

            makeReferenceGroupRequest(group, context, MetkaJS.TableHandler.handleReferenceOptions);
        };

        /**
         * Handles reference inputs where the field is not a container.
         * Can be fed any field key and if the field is applicable will handle given field as needed.
         * If no reference is found on the field then nothing is done leaving the field
         * as it is at the moment in the UI.
         * @param field Field key of the field to be analysed
         * @param context Configuration context of the field
         * @param dependencyValue Possible dependency value for value queries
         * @param container Field key of the possible container where this field is located
         */
        function referenceInput(field, context, dependencyValue, container) {
            var reference = null;
            switch (field.type) {
                case MetkaJS.E.Field.SELECTION:
                    var selectionList = MetkaJS.JSConfigUtil.getRootSelectionList(field.selectionList);
                    if (selectionList !== null && selectionList.type === MetkaJS.E.Selection.REFERENCE) {
                        reference = MetkaJS.JSConfigUtil.getReference(selectionList.reference);
                    }
                    break;
                case MetkaJS.E.Field.REFERENCE:
                    reference = MetkaJS.JSConfigUtil.getReference(field.reference);
                    break;
            }
            if (reference === null) {
                // Sanity check, field has to have a reference for this function to make sense
                return;
            }
            if (dependencyValue === null) {
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
                }
            }

            // Make single request
            makeReferenceRequest(field.key, dependencyValue, context, container);
        };

        /**
         * Sends a single ReferenceOption request to server.
         * Requests a list of options from server and displays them in a select component with previous selection selected
         * as default.
         * @param key Field key
         * @param dependencyValue Value of dependency field
         * @param context Configuration context
         * @param container Possible key for a container in which this field is located, can be undefined or null
         */
        function makeReferenceRequest(key, dependencyValue, context, container) {
            var group = new ReferenceOptionsGroupRequest();

            group.requests.push(new ReferenceOptionsRequest(
                key,
                container,
                MetkaJS.JSConfigUtil.getConfigurationKey().type,
                MetkaJS.JSConfigUtil.getConfigurationKey().version,
                dependencyValue
            ));

            makeReferenceGroupRequest(group, context, handleReferenceInput);
        };

        /**
         * Sends a group of reference option requests to server.
         * Requests a list of options from server and displays them in a select component with previous selection selected
         * as default.
         * @param key Field key
         * @param context Configuration context
         */
        function makeReferenceGroupRequest(request, context, handler) {
            $.ajax({
                type: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: 'json',
                url: MetkaJS.PathBuilder().add('references').add('collectOptionsGroup').build(),
                data: JSON.stringify(request)
            }).done(function (data) {
                    if (data !== null && data.responses !== null && data.responses.length > 0) {
                        // Handle responses
                        handler(data, context);
                    }
                });
        };

        /**
         * Checks how to handle a certain reference request data.
         *
         * TODO: Missing handlers
         * @param data Request response
         * @param context Context where the data is expected
         */
        function handleReferenceInput(data, context) {
            data = data.responses[0];
            var i, j, iLength, jLength;
            if (typeof data.messages !== 'undefined' && data.messages !== null && data.messages.length > 0) {
                for (i = 0, iLength = data.messages.length; i < iLength; i++) {
                    var message = data.messages[i];
                    MetkaJS.ErrorManager.push(MetkaJS.ErrorManager.ErrorMessage(message.title, message.message));
                    for (j = 0, jLength = message.data.length; j < jLength; j++) {
                        MetkaJS.ErrorManager.topError().pushData(message.data[j]);
                    }
                }
                MetkaJS.ErrorManager.showAll();
                return;
            }

            if (data.options === null) {
                // No options
                return;
            }
            var key = data.key;

            var field = MetkaJS.JSConfigUtil.getField(key);

            switch (field.type) {
                case MetkaJS.E.Field.SELECTION:
                    var selectionList = MetkaJS.JSConfigUtil.getRootSelectionList(field.selectionList);
                    createReferenceSelection(field, data.options, selectionList.includeEmpty, data.container);
                    break;
                case MetkaJS.E.Field.REFERENCE:
                    if (data.options.length <= 0) {
                        data.options[0] = {
                            value: '',
                            title: {
                                type: MetkaJS.E.RefTitle.LITERAL,
                                value: ''
                            }
                        };
                    }
                    createReferenceText(field, data.options[0], data.container);
                    break;
            }
        };

        /**
         * Fills selection input with options from the provided options array.
         * @param field Field where the data is meant to be inserted
         * @param options Options array containing values and titles
         * @param includeEmpty Should empty option be included.
         * @param container Possible container field key, should only be present on subfields.
         */
        function createReferenceSelection(field, options, includeEmpty, container) {
            // Sanity check, if field is subfield but there is no container then nothing can be done.
            if (field.subfield && (container === null || container === '')) {
                return;
            }

            // Check if empty value is needed as part of the selection
            includeEmpty = (includeEmpty === null) ? true : includeEmpty;

            // Remove possible text fields added by reference handling
            $('#' + field.key + '_reftext').remove();
            if (container !== null) {
                $('#' + container + 'Field' + field.key + '_reftext').remove();
            }

            var readonly = MetkaJS.isReadOnly(field);

            var i, length, curVal, option;

            if (readonly) {
                // Make text input instead of select
                option = null;
                for (i = 0, length = options.length; i < length; i++) {
                    if (options[i].value === curVal) {
                        option = options[i];
                        break;
                    }
                }
                if (option === null) {
                    option = {
                        value: '',
                        title: {
                            type: MetkaJS.E.RefTitle.LITERAL,
                            value: ''
                        }
                    };
                }
                createReferenceText(field, option);
            } else {
                // Get correct input and its value
                var input = (field.subfield) ? $('#' + container + 'Field' + field.key) : MetkaJS.getValuesInput(field.key);
                curVal = input.val();

                // Make select
                var select;
                if (field.subfield) {
                    select = $('<select>', {id: container + 'Field' + field.key, 'class': 'dialogValue'});
                    select.change(function () {
                        var $this = $(this);
                        MetkaJS.EventManager.notify(MetkaJS.E.Event.FIELD_CHANGE, {target: $this.data('key'), container: container});
                    });
                } else {
                    select = $('<select>', {id: MetkaJS.getValuesInputId(field.key), name: MetkaJS.getValuesInputName(field.key)});
                    select.change(function () {
                        var $this = $(this);
                        MetkaJS.EventManager.notify(MetkaJS.E.Event.FIELD_CHANGE, {target: $this.data('key')});
                    });
                }
                select.data('key', field.key);

                if (includeEmpty) {
                    option = $('<option>', {value: null});
                    option.append(MetkaJS.L10N.get('general.list.empty'));
                    select.append(option);
                }
                for (i = 0, length = options.length; i < length; i++) {
                    option = $('<option>', {value: options[i].value});
                    option.append(options[i].title.value);
                    select.append(option);
                }
                select.val(curVal);
                input.replaceWith(select);
            }
        };

        /**
         * Inserts given option to top level field.
         * Option value goes to hidden field and option text goes to text field / text area.
         *
         * @param field Field where the data is meant to be inserted
         * @param option Option containing value and title
         * @param container Possible container field key, should only be present on subfields.
         */
        function createReferenceText(field, option, container) {
            // Sanity check, if field is subfield but there is no container then nothing can be done.
            if (field.subfield && (container === null || container === '')) {
                return;
            }

            var input;
            var inputText;
            var hidden;
            var text;
            if (field.subfield) {
                input = $('#' + container + 'Field' + field.key);
                inputText = $('#' + container + 'Field' + field.key + '_reftext');
                hidden = $('<input>', {
                    type: 'hidden',
                    id: container + 'Field' + field.key,
                    'class': 'dialogValue'
                });

                if (field.multiline) {
                    text = $('<textarea>', {id: container + 'Field' + field.key + '_reftext'});
                } else {
                    text = $('<input>', {type: 'text', id: container + 'Field' + field.key + '_reftext'});
                }

            } else {
                input = MetkaJS.getValuesInput(field.key);
                inputText = $('#' + field.key + '_reftext');
                hidden = $('<input>', {type: 'hidden', id: MetkaJS.getValuesInputId(field.key), name: MetkaJS.getValuesInputName(field.key)});

                if (field.multiline) {
                    text = $('<textarea>', {id: field.key + '_reftext'});
                } else {
                    text = $('<input>', {type: 'text', id: field.key + '_reftext'});
                }

            }

            var readonly = MetkaJS.isReadOnly(field);

            if (readonly) {
                hidden.prop('readonly', true);
                text.prop('readonly', true);
            } else {
                hidden.prop('readonly', false);
                text.prop('readonly', false);
            }

            hidden.val(option.value);
            // TODO: Handle VALUE type titles by selecting correct selection translation
            text.val(option.title.value);

            input.replaceWith(hidden);
            if (inputText.length !== 0) {
                inputText.replaceWith(text);
            } else {
                text.insertAfter(hidden);
            }
        };

        return {
            referenceInputCallback: referenceInputCallback,
            referenceContainerCallback: referenceContainerCallback,
            handleReference: referenceHandlerChooser
        };
    }());
}());