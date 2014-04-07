$(document).ready(function() {
    MetkaJS.ReferenceHandler = function() {
        function revisionableReferenceModelInput(key, context) {
            var field = MetkaJS.JSConfigUtil.getField(key);
            if(field == null) {
                // Sanity check, field has to exist for this to make sense.
                return;
            }
            if(field.type == "CHOICE") {
                var choicelist = MetkaJS.JSConfigUtil.getRootChoicelist(field.choicelist);
                if(choicelist == null || choicelist.type != "REFERENCE") {
                    // Sanity check, if field is choice then choicelist has to excist and the type has to be REFERENCE for this to make sense
                    return;
                }
            }
            var request = new Object();
            request.key = key;
            request.id = MetkaJS.SingleObject.id;
            request.revision = MetkaJS.SingleObject.revision;

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
                if(data.id != request.id
                    || data.revision != request.revision
                    || data.key != request.key) {
                    return;
                }
                if(data.messages !== 'undefined' && data.messages != null && data.messages.length > 0) {
                    for(var i= 0, iLength = data.messages.length; i<iLength; i++) {
                        var message = data.messages[i];
                        MetkaJS.ErrorManager.push(MetkaJS.ErrorManager.ErrorObject(message.title, message.message));
                        for(var j= 0, jLength = message.data.length; j < jLength; j++) {
                            MetkaJS.ErrorManager.topError().pushData(message.data[j]);
                        }
                    }
                    MetkaJS.ErrorManager.showAll();
                } else {
                    handleReferenceInput(key, data, context);
                }
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
        function handleReferenceInput(key, data, context) {
            var field = MetkaJS.JSConfigUtil.getField(key);
            if(field.type == "CHOICE") {
                var choicelist = MetkaJS.JSConfigUtil.getRootChoicelist(field.choicelist);
                if(field.subfield == true) {

                } else {
                    var input = MetkaJS.getModelInput(key);
                    createModelReferenceSelection(input, key, data.options, choicelist.includeEmpty);
                }
            }
        }

        /**
         * Fills given selection input with options from the provided options array
         * @param input Selection input to be filled
         * @param options Options array containing values and titles
         * @param includeEmpty Should empty option be included.
         */
        function createModelReferenceSelection(input, key, options, includeEmpty) {
            includeEmpty = (includeEmpty == null) ? true : includeEmpty;
            var curVal = input.val();
            var select = $("<select>", {id: MetkaJS.getModelInputId(key), name: MetkaJS.getModelInputName(key)})
            if(includeEmpty == true) {
                var option = $("<option>", {value: null});
                option.append(MetkaJS.L10N.get("general.list.empty"));
                select.append(option);
            }
            for(var i= 0, length = options.length; i<length; i++) {
                var option = $("<option>", {value: options[i].value});
                option.append(options[i].title);
                select.append(option);
            }
            select.val(curVal);
            input.replaceWith(select);
        }

        return {
            handleRevisionableReferenceModelInput: revisionableReferenceModelInput
        }
    }();
});