/**
 * Insert default general handler
 * Handlers should be found from a data-attribute called 'handler'.
 * All handlers should provide at least basic interface functions 'show' and 'process'.
 * show: function(key[, handlerId, context])
 *      - key: Field key of the table containing data for the dialog). Required
 *      - handlerId: Handler id of the requested row. This is used to fetch information for dialog. Not required. If it's not provided then it's assumed this is a new object.
 *      - context: Configuration context. Under which key should field configuration be found. This can be ignored in the actual handler.
 *      show should return boolean telling whether the operation was successful or not.
 * process: function([key, context])
 *      - key: Field key of the container that should contain the data after it's collected from dialog. This can be ignored by the handler (for example if the data is automatically send to server)
 *      - context: Configuration context. Under which key should field configuration be found. This can be ignored in the actual handler.
 *      process should return boolean telling whether the operation was successful or not.
 */
MetkaJS.DialogHandlers.generalContainerHandler = function() {
    /**
     * Displays a dialog matching given key and initialises it matching given parameters.
     * @param key For what container should the dialog be opened.
     * @param isNew Is the dialog supposed to add or modify.
     * @param row Existing row for which data should be shown in the dialog.
     */
    function showGeneralContainerDialog(key, handlerId, context) {
        // Reset dialog, these are only changed if a row was found so until then we can assume new row.
        $("#"+key+"ContainerDialogRowId").val(null);
        $("#"+key+"ContainerDialogRowId").data("handlerId", null);

        $("#"+key+"ContainerDialogTable .dialogValue").val(null);
        $("#"+key+"ContainerDialogTable .dialogValue").prop("readonly", false);

        // Get content so we can find the actual data.
        var content = MetkaJS.TableHandler.readContent(key);
        if((content === 'undefined' || content == null) && (handlerId !== 'undefined' && handlerId != null)) {
            // No content was found but this is supposed to be an existing row. Return false since we can't be sure about anything at this point.
            return false;
        }
        var row = null;
        var found = false;
        if(content != null && handlerId !== 'undefined' && handlerId != null) {
            for(var i = 0; i < content.rows.length; i++) {
                if(content.rows[i].handlerId == handlerId) {
                    row = content.rows[i];
                    break;
                }
            }
        }

        // If row was given then use it, otherwise clear dialog and change to adding new row
        if(row != null && row.handlerId !== 'undefined' && row.handlerId != null) {
            found = true;
            $("#"+key+"ContainerDialogRowId").val(row.rowId);
            // rowId field can double as handler id data storage since this field is always present and doesn't relate to actual data
            $("#"+key+"ContainerDialogRowId").data("handlerId", row.handlerId);
            for(var i = 0; i < MetkaJS.JSConfig[context].fields[key].subfields.length; i++) {
                var subfield = MetkaJS.JSConfig[context].fields[key].subfields[i];
                if(subfield == null || subfield == "") {
                    // Sanity check, although this means that something is very wrong
                    continue;
                } else if(MetkaJS.JSConfig[context].fields[subfield].type == MetkaJS.E.Field.CONTAINER) {
                    // TODO: Handle recursive CONTAINERS
                    continue;
                }
                var input = $("#"+key+"Field"+subfield);
                // TODO: For now assumes input into val(), add exceptions as needed
                if(row.fields[subfield] == undefined || row.fields[subfield] == null) {
                    row.fields[subfield] = new Object();
                    row.fields[subfield].type = "value";
                    row.fields[subfield].value = "";
                }

                input.val(row.fields[subfield].value);

                if(MetkaJS.SingleObject.draft == false
                    || MetkaJS.JSConfig[context].fields[subfield].editable == false
                    || (
                    MetkaJS.JSConfig[context].fields[subfield].immutable == true
                        && (input.val() !== 'undefined' && input.val() != null && input.val() != ""))
                    ) {
                    input.prop("readonly", true);
                }
            }
        }

        // Open dialog
        $("#"+key+"ContainerDialog").dialog("open");
    }

    /**
     * Forms a row from general dialog and saves it to content using MetkaJS.TableHandler.saveRow function
     * @param key Key of target CONTAINER field
     * @param context Configuration context
     */
    function processGeneralContainerDialog(key, context) {
        var body = $("#"+key+"ContainerDialogTable tbody");
        var row = new Object();
        row.type = "row";
        row.rowId = $("#"+key+"ContainerDialogRowId").val();
        // Handler id is important since it tells if this row was existing or not.
        row.handlerId = $("#"+key+"ContainerDialogRowId").data("handlerId");
        row.key = key;
        row.fields = new Object();
        // RowId is set when row is added to container
        for(var i = 0; i < MetkaJS.JSConfig[context].fields[key].subfields.length; i++) {
            var subfield = MetkaJS.JSConfig[context].fields[key].subfields[i];
            if(subfield === 'undefined' || subfield == null || subfield == "") {
                // Sanity check, although this means that something is very wrong
                continue;
            } else if(MetkaJS.JSConfig[context].fields[subfield].type == MetkaJS.E.Field.CONTAINER) {
                // TODO: Handle recursive CONTAINERS
                continue;
            }
            var input = $("#"+key+"Field"+subfield);
            var value = new Object;
            value.type = "value";
            // TODO: Handle possible value extraction exception
            value.value = input.val();

            row.fields[subfield] = value;
        }

        MetkaJS.TableHandler.saveRow(row, context);
        // TODO: check for missing required information
        $("#"+key+"ContainerDialog").dialog("close");
    }

    return {
        show: showGeneralContainerDialog,
        process: processGeneralContainerDialog
    }
}();

/**
 * This provides a fallback handler for reference tables.
 * Since all reference tables require their own custom handlers this will just
 * throw an alert with suitable error message informing that the operation is not supported yet.
 */
MetkaJS.DialogHandlers.generalReferenceHandler = function() {
    function fallbackShow(key, handlerId, context) {
        var message = MetkaJS.L10N.get("general.errors.container.dialog.noImplementation");
        message = message.replace("{0}", key);
        message = message.replace("{1}", MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
        alert(message, "general.errors.title.noImplementation");
    }

    function fallbackProcess(key, context) {
        var message = MetkaJS.L10N.get("general.errors.container.dialog.noImplementation");
        message = message.replace("{0}", key);
        message = message.replace("{1}", MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
        alert(message, "general.errors.title.noImplementation");
    }

    return {
        show: fallbackShow,
        process: fallbackProcess
    }
}();