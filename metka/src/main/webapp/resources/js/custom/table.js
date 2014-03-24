$(document).ready(function() {
    // Init autobuild tables
    $(".autobuild").each(function(index) {
        var id = $(this).attr("id");
        MetkaJS.TableHandler.build(id, $(this).data("context"));
    });
});

/**
 * Insert default general handler
 * Handlers should be found from a data-attribute called 'handler'.
 * All handlers should provide at least basic interface functions 'show' and 'process'.
 * show: function(key[, row, context])
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
            for(var i = 0; i < MetkaJS.JSConfig[context][key].subfields.length; i++) {
                var subfield = MetkaJS.JSConfig[context][key].subfields[i];
                if(subfield === 'undefined' || subfield == null || subfield == "") {
                    // Sanity check, although this means that something is very wrong
                    continue;
                } else if(MetkaJS.JSConfig[context][subfield].type == "CONTAINER") {
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

                if(MetkaJS.JSConfig[context][subfield].editable == false
                    || (MetkaJS.JSConfig[context][subfield].immutable == true
                    && (input.val() !== 'undefined' && input.val() != null && input.val() != ""))) {
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
        for(var i = 0; i < MetkaJS.JSConfig[context][key].subfields.length; i++) {
            var subfield = MetkaJS.JSConfig[context][key].subfields[i];
            if(subfield === 'undefined' || subfield == null || subfield == "") {
                // Sanity check, although this means that something is very wrong
                continue;
            } else if(MetkaJS.JSConfig[context][subfield].type == "CONTAINER") {
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
        var message = MetkaJS.L18N.get("general.errors.container.dialog.noImplementation");
        message = message.replace("{0}", key);
        message = message.replace("{1}", MetkaJS.L18N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
        alert(message, "general.errors.title.noImplementation");
    }

    function fallbackProcess(key, context) {
        var message = MetkaJS.L18N.get("general.errors.container.dialog.noImplementation");
        message = message.replace("{0}", key);
        message = message.replace("{1}", MetkaJS.L18N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
        alert(message, "general.errors.title.noImplementation");
    }

    return {
        show: fallbackShow,
        process: fallbackProcess
    }
}();

MetkaJS.TableHandler = function() {
    /**
     * Reads out top level content (if it exists) with given key into a Javascript object.
     * @param key Key of the field where the content is found
     * @returns Javascript object representing the JSON string in a field, null if no data found
     */
    function readContent(key) {
        if(key === 'undefined' || key == null || key == "") {
            return null;
        }
        if($("#values\\'"+key+"\\'").val() != undefined
                && $("#values\\'"+key+"\\'").val() != null
                && $("#values\\'"+key+"\\'").val() != "") {
            var content = JSON.parse($("#values\\'"+key+"\\'").val());
            return content;
        }
        return null;
    }

    /**
     * Save given content back to a field.
     * Checks to see that the content, as well as the field exists so you can't clear a whole table with this.
     * @param content Content to be saved, must not be null or doesn't do anything at all.
     */
    function saveContent(content) {
        if(content === 'undefined'
                || content == null
                || content.key === 'undefined'
                || content.key == null
                || content.key == "") {
            return;
        }
        var input = $("#values\\'"+content.key+"\\'");
        if(input === 'undefined' || input == null) {
            return;
        }
        input.val(JSON.stringify(content));
    }

    /**
     * Checks which function is needed to build a table for given content
     * @param content
     */
    function buildTable(key, context) {
        var content = readContent(key);
        if(content != null) {
            if(content.type == "container") {
                buildContainertable(content, context);
            } else if(content.type == "referencecontainer") {
                buildReferencetable(content, context);
            }
        }
    }
    DialogOpener = function(handler, key, handlerId, context) {
        return function() {
            MetkaJS.DialogHandlers[handler].show(key, handlerId, context);
        }
    }
    /**
     * Builds a table for given container content.
     * Follows configuration parameters for table columns and display of extra columns
     * @param content JSON object, usually from hidden field. Must match type container.
     */
    function buildContainertable(content, context) {
        // Handle only containers
        var key = content.key;
        var field = MetkaJS.JSConfig[context][key];
        var changes = false;
        if(content.type != "container" || field.type != "CONTAINER") return;

        var body = $("#"+key+" tbody");
        body.empty();
        var handler = $("#"+key).data("handler");
        for(var row = 0; row < content.rows.length; row++) {
            var tr = $("<tr>", {class: "pointerClass"});
            var rowContent = content.rows[row];
            if(rowContent.handlerId ==='undefined' || rowContent.handlerId == null) {
                // Add handler id so row can be found after editing, these do not affect actual data on serverside in any way
                rowContent.handlerId = MetkaJS.Globals.globalId();
                changes = true;
            }
            var opener = new DialogOpener(handler, key, rowContent.handlerId, context);
            tr.click(opener);
            if(rowContent.temporary) {
                tr.addClass("temporary");
            } else {
                tr.removeClass("temporary");
            }
            for(i = 0; i < field.subfields.length; i++) {
                var subfield = field.subfields[i];
                if(MetkaJS.JSConfig[context][subfield].summaryField == false) {
                    continue;
                }
                var value = rowContent.fields[subfield];
                if(value == undefined || value == null) {
                    // TODO: value is missing for some reason, make placeholder value so columns are not missing
                    value = new Object();
                    value.type = "value";
                    value.value = "";
                }
                if(value.type != "value" || MetkaJS.JSConfig[context][subfield].type == "CONTAINER" || MetkaJS.JSConfig[context][subfield].type == "REFERENCECONTAINER") {
                    // TODO: Handle recursive containers somehow. Mostly table should not contain recursive containers
                    return;
                }

                var td = $("<td>");
                switch(MetkaJS.JSConfig[context][subfield].type) {
                    case "CHOICE":
                        td.text(MetkaJS.L18N.get(MetkaJS.Globals.page.toUpperCase()+"."+MetkaJS.JSConfig[context][subfield].choicelist+".choices."+value.value));
                        break;
                    default:
                        td.text(value.value);
                        break;
                }
                tr.append(td);
            }
            if(field.showSaveInfo == true) {
                var value = rowContent.savedAt;
                var td = $("<td>");
                if(value != undefined) td.text(value);
                tr.append(td);

                value = rowContent.savedBy;
                td = $("<td>");
                if(value != undefined) td.text(value);
                tr.append(td);
            }
            body.append(tr);
        }
        if(changes) {
            // Save content back to field since there has been new handlerId:s inserted
            // These are not saved to database but are vital to handling dialogs and modifying content
            saveContent(content);
        }
    }

    /**
     * Builds a table for given referencecontainer content.
     * Follows configuration parameters for table columns and display of extra columns
     * @param content JSON object, usually from hidden field. Must match type container.
     */
    function buildReferencetable(content, context) {
        // Handle only containers
        var key = content.key;
        var field = MetkaJS.JSConfig[context][key];
        var changes = false;
        if(content.type != "referencecontainer" || field.type != "REFERENCECONTAINER") return;

        var body = $("#"+key+" tbody");
        body.empty();
        var handler = $("#"+key).data("handler");
        for(var row = 0; row < content.references.length; row++) {
            var tr = $("<tr>", {class: "pointerClass"});
            var reference = content.references[row];
            if(reference.temporary) {
                tr.addClass("temporary");
            } else {
                tr.removeClass("temporary");
            }
            if(reference.handlerId ==='undefined' || reference.handlerId == null) {
                // Add handler id so reference can be found after editing, these do not affect actual data on serverside in any way
                reference.handlerId = MetkaJS.Globals.globalId();
                changes = true;
            }
            var opener = new DialogOpener(handler, key, reference.handlerId, context);
            tr.click(opener);
            // Display reference id if required
            if(field.showReferenceKey) {
                var value = reference.value;
                var td = $("<td>");
                if(value !== 'undefined') td.text(value);
                tr.append(td);
            }
            // TODO: display extra fields for referenced information

            if(field.showSaveInfo == true) {
                var value = reference.savedAt;
                var td = $("<td>");
                if(value !== 'undefined') td.text(value);
                tr.append(td);

                value = reference.savedBy;
                td = $("<td>");
                if(value !== 'undefined') td.text(value);
                tr.append(td);
            }
            body.append(tr);
        }
        if(changes) {
            // Save content back to field since there has been new handlerId:s inserted
            // These are not saved to database but are vital to handling dialogs and modifying content
            saveContent(content);
        }
    }



    function saveRow(row, context) {
        if(row.type=="row") {
            saveContainerRow(row, context);
        } else if(row.type == "reference") {
            saveReferenceRow(row, context);
        }
    }

    /**
     * Save a given CONTAINER row to right hidden input.
     * Makes needed changes to JSON and sets it to input.
     * If given row's rowId is null or undefined assumes new row, otherwise updates old row.
     * In case of modified row the row in original content is overwritten.
     * If original row is not found then row is assumed to be new, rowId is overwritten to null
     * and row is pushed to content.
     *
     * After modifications are done table is rebuilt.
     * @param row Row being saved to JSON hidden field
     */
    function saveContainerRow(row, context) {
        if(row.type != "row") return;
        // Fetch previous data from hidden field
        var content = readContent(row.key);
        if(content == null) {
            content = new Object();
            content.type = "container";
            content.key = row.key;
        }

        if(content.key != row.key) { // Sanity check that row is being saved to correct container
            return;
        }
        row.change = true; // Set row to changed so it's easy to find while saving user input on server
        // Make modification
        var found = false;
        if(content.rows == undefined) {
            content.rows = new Array();
        }
        if(row.handlerId != null && row.handlerId != undefined) { // Check for old row
            for(var i = 0; i < content.rows.length; i++) {
                if(content.rows[i].handlerId == row.handlerId) {
                    found = true;
                    content.rows[i] = row; // Just replace old row with new row. All info should be there already
                    break;
                }
            }
        }
        if(found == false) { // Either a new row or old row was not found.
            row.rowId = null; // Make sure that rowId is null when adding a new row.
            content.rows.push(row);
        }

        if(content !== 'undefined' && content != null) {
            // Put modified data back to hidden field
            saveContent(content);
            // Rebuild table
            buildTable(content.key, context);
        }
    }

    /**
     * Save a given REFERENCECONTAINER reference to right hidden input.
     * Makes needed changes to JSON and sets it to input.
     * If given reference's rowId is null or undefined assumes new reference, otherwise updates old reference.
     * In case of modified reference the reference in original content is overwritten.
     * If original reference is not found then reference is assumed to be new, rowId is overwritten to null
     * and row is pushed to content.
     *
     * After modifications are done table is rebuilt.
     * @param reference Reference being saved to JSON hidden field
     */
    function saveReferenceRow(reference, context) {
        if(reference.type != "reference") return;
        // Fetch previous data from hidden field
        var content = readContent(reference.key);
        if(content == null) {
            content = new Object();
            content.type = "referencecontainer";
            content.key = reference.key;
        }

        if(content.key != reference.key) { // Sanity check that reference is being saved to correct container
            return;
        }
        reference.change = true; // Set reference to changed so it's easy to find while saving user input on server
        // Make modification
        var found = false;
        if(content.references == undefined) {
            content.references = new Array();
        }
        if(reference.handlerId != null && reference.handlerId != undefined) { // Check for old reference
            for(var i = 0; i < content.references.length; i++) {
                if(content.reference[i].handlerId == reference.handlerId) {
                    found = true;
                    content.references[i] = reference; // Just replace old reference with new reference. All info should be there already
                    break;
                }
            }
        }
        if(found == false) { // Either a new reference or old reference was not found.
            reference.rowId = null; // Make sure that rowId is null when adding a new reference.
            content.references.push(reference);
        }

        if(content !== 'undefined' && content != null) {
            // Put modified data back to hidden field
            saveContent(content);
            // Rebuild table
            buildTable(content.key, context);
        }
    }

    return {
        readContent: readContent,
        saveContent: saveContent,
        build: buildTable,
        saveRow: saveRow
    }
}();