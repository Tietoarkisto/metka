MetkaJS.ReferenceHandler = (function() {
    ModelInputCallback = function(key, context, dependencyValue) {
        return function() {
            MetkaJS.ReferenceHandler.handleReference(key, context, dependencyValue);
        }
    }

    function referenceHandlerChooser(key, context, dependencyValue) {
        var field = MetkaJS.JSConfigUtil.getField(key);
        if(field == null) {
            // Sanity check, field has to exist for this to make sense.
            return;
        }

        switch(field.type) {
            case MetkaJS.E.Field.REFERENCECONTAINER:
                // Collects
                referenceContainer(field, context, dependencyValue);
                break;
            case MetkaJS.E.Field.CONTAINER:
                // For now, do nothing
                break;
            default:
                // Make single request with parameters related to given field
                modelInputReference(field, context, dependencyValue);
                break;
        }
    }

    function referenceContainer(field, context, dependencyValue) {
        if(field.subfield == true) {
            // Let's not handle recursive referencecontainers just yet.
            return;
        } else if(field.type != MetkaJS.E.Field.REFERENCECONTAINER) {
            // Sanity check. This function is meant only for referencecontainers
            return;
        } else if(dependencyValue == null) {
            // Sanity check, this function is meant to handle one specific row at a time.
            // It no dependencyValue is provided then we can not make the request.
            return;
        }

        var requests = new Array();
        for(var i= 0, length=field.subfields.length; i<length; i++) {
            var request = new Object();
            request.key = field.subfields[i];
            request.confType = MetkaJS.JSConfigUtil.getConfigurationKey().type;
            request.confVersion = MetkaJS.JSConfigUtil.getConfigurationKey().version;
            request.dependencyValue = dependencyValue;
            requests.push(request);
        }

        makeReferenceGroupRequest(requests, context, MetkaJS.TableHandler.handleReferenceOptions);
    }

    function modelInputReference(field, context, dependencyValue) {
        if(field.subfield == true) {
            // Sanity check, this function is meant for top level fields
            // TODO: Generalise to any field
            return;
        }
        var reference = null;
        switch(field.type) {
            case MetkaJS.E.Field.CHOICE:
                var choicelist = MetkaJS.JSConfigUtil.getRootChoicelist(field.choicelist);
                if(choicelist != null && choicelist.type == MetkaJS.E.Choice.REFERENCE) {
                    reference = MetkaJS.JSConfigUtil.getReference(choicelist.reference);
                }
                break;
            case MetkaJS.E.Field.REFERENCE:
                reference = MetkaJS.JSConfigUtil.getReference(field.reference);
                break;
        }
        if(reference == null) {
            // Sanity check, field has to have a reference for this function to make sense
            return;
        }
        if(dependencyValue == null) {
            if(reference.type == MetkaJS.E.Ref.DEPENDENCY) {
                var depField = MetkaJS.JSConfigUtil.getField(reference.target);
                MetkaJS.EventManager.listen(MetkaJS.E.Event.FIELD_CHANGE, field.key, {key: depField.key}, new ModelInputCallback(field.key, context, null));
                var input = null;
                switch(depField.type) {
                    case MetkaJS.E.Field.REFERENCE:
                    case MetkaJS.E.Field.STRING:
                    case MetkaJS.E.Field.CHOICE:
                        // TODO: We are assuming here that current field and dependency field are both top level fields. This should get generalises at some point.

                        // With choice we don't really care about if the choice is REFERENCE etc. since we only want the current value.
                        // Server can make better judgements on how to use that value.
                        input = MetkaJS.getModelInput(depField.key);
                        break;
                }
                if(input != null) {
                    dependencyValue = input.val();
                }
            }
        }

        // Make single request
        makeReferenceRequest(field.key, dependencyValue, context);
    }

    /**
     * Sends a single ReferenceOption request to server.
     * Requests a list of options from server and displays them in a select component with previous selection selected
     * as default.
     * @param key Field key
     * @param dependencyValue Value of dependency field
     * @param context Configuration context
     */
    function makeReferenceRequest(key, dependencyValue, context) {
        var request = new Object();
        request.key = key;
        request.confType = MetkaJS.JSConfigUtil.getConfigurationKey().type;
        request.confVersion = MetkaJS.JSConfigUtil.getConfigurationKey().version;
        request.dependencyValue = dependencyValue;

        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            url: MetkaJS.PathBuilder().add("references").add("collectOptions").build(),
            data: JSON.stringify(request)
        }).done(function(data) {
            handleReferenceInput(data, context);
        });
    }

    /**
     * Sends a group of reference option requests to server.
     * Requests a list of options from server and displays them in a select component with previous selection selected
     * as default.
     * @param key Field key
     * @param context Configuration context
     */
    function makeReferenceGroupRequest(requests, context, handler) {
        var request = new Object();
        request.requests = requests;

        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            url: MetkaJS.PathBuilder().add("references").add("collectOptionsGroup").build(),
            data: JSON.stringify(request)
        }).done(function(data) {
            handler(data, context);
        });
    }

    /**
     * Checks how to handle a certain reference request data.
     *
     * TODO: Missing handlers
     * @param key Field key where the data is meant to be inserted
     * @param data Request response
     * @param context Context where the data is expected
     */
    function handleReferenceInput(data, context) {
        if(data.messages !== 'undefined' && data.messages != null && data.messages.length > 0) {
            for(var i= 0, iLength = data.messages.length; i<iLength; i++) {
                var message = data.messages[i];
                MetkaJS.ErrorManager.push(MetkaJS.ErrorManager.ErrorMessage(message.title, message.message));
                for(var j= 0, jLength = message.data.length; j < jLength; j++) {
                    MetkaJS.ErrorManager.topError().pushData(message.data[j]);
                }
            }
            MetkaJS.ErrorManager.showAll();
            return;
        }

        if(data.options == null) {
            // No options
            return;
        }
        var key = data.key;

        var field = MetkaJS.JSConfigUtil.getField(key);
        switch(field.type) {
            case MetkaJS.E.Field.CHOICE:
                var choicelist = MetkaJS.JSConfigUtil.getRootChoicelist(field.choicelist);
                if(field.subfield == true) {

                } else {
                    createModelReferenceSelection(key, data.options, choicelist.includeEmpty);
                }
                break;
            case MetkaJS.E.Field.REFERENCE:
                if(data.options.length <= 0) {
                    data.options[0] = {
                        value: "",
                        title: {
                            type: MetkaJS.E.RefTitle.LITERAL,
                            value: ""
                        }
                    }
                }
                if(field.subfield == true) {

                } else {
                    insertModelReferenceText(key, data.options[0]);
                }
                break;
        }
    }

    /**
     * Fills selection input with options from the provided options array
     * @param key Field key where the data is meant to be inserted
     * @param options Options array containing values and titles
     * @param includeEmpty Should empty option be included.
     */
    function createModelReferenceSelection(key, options, includeEmpty) {
        includeEmpty = (includeEmpty == null) ? true : includeEmpty;
        var input = MetkaJS.getModelInput(key);
        var curVal = input.val();
        var select = $("<select>", {id: MetkaJS.getModelInputId(key), name: MetkaJS.getModelInputName(key)});
        select.data("key", key);
        select.change(function() {
            var $this = $(this);
            MetkaJS.EventManager.notify(MetkaJS.E.Event.FIELD_CHANGE, {key: $this.data("key")});
        });
        if(includeEmpty == true) {
            var option = $("<option>", {value: null});
            option.append(MetkaJS.L10N.get("general.list.empty"));
            select.append(option);
        }
        for(var i= 0, length = options.length; i<length; i++) {
            var option = $("<option>", {value: options[i].value});
            option.append(options[i].title.value);
            select.append(option);
        }
        select.val(curVal);
        input.replaceWith(select);
    }

    /**
     * Inserts given option to top level field.
     * Option value goes to hidden field and option text goes to text field / text area.
     * @param key Field key where the data is meant to be inserted
     * @param option Option containing value and title
     */
    function insertModelReferenceText(key, option) {
        var hidden = MetkaJS.getModelInput(key);
        var text = $("#"+key+"_text");

        hidden.val(option.value);
        // TODO: Handle VALUE type titles by selecting correct choice translation
        text.val(option.title.value);
    }

    return {
        ModelInputCallback: ModelInputCallback,
        handleReference: referenceHandlerChooser
    }
})();