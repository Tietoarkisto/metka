MetkaJS.TableHandler = function() {
    /**
     * Reads out top level content (if it exists) with given key into a Javascript object.
     * @param key Key of the field where the content is found
     * @returns Javascript object representing the JSON string in a field, null if no data found
     */
    function readContent(key) {
        var field = MetkaJS.getModelInput(key);
        if(field != null && field.val() !== 'undefined' && field.val() != null && field.val() != "") {
            var content = JSON.parse(field.val());
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
        var input = MetkaJS.getModelInput(content.key);
        if(input == null) {
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
        var field = MetkaJS.JSConfig[context].fields[key];
        var changes = false;
        if(content.type != "container" || field.type != MetkaJS.E.Field.CONTAINER) return;

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
                if(MetkaJS.JSConfig[context].fields[subfield].summaryField == false) {
                    continue;
                }
                var value = rowContent.fields[subfield];
                if(value == undefined || value == null) {
                    // TODO: value is missing for some reason, make placeholder value so columns are not missing
                    value = new Object();
                    value.type = "value";
                    value.value = "";
                }
                if(value.type != "value" || MetkaJS.JSConfig[context].fields[subfield].type == MetkaJS.E.Field.CONTAINER || MetkaJS.JSConfig[context].fields[subfield].type == MetkaJS.E.Field.REFERENCECONTAINER) {
                    // TODO: Handle recursive containers somehow. Mostly table should not contain recursive containers
                    return;
                }

                var td = $("<td>");
                switch(MetkaJS.JSConfig[context].fields[subfield].type) {
                    case MetkaJS.E.Field.CHOICE:
                        td.text(MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+"."+MetkaJS.JSConfig[context].fields[subfield].choicelist+".choices."+value.value));
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
        // Handle only reference containers
        var key = content.key;
        var field = MetkaJS.JSConfig[context].fields[key];
        var changes = false;
        if(content.type != "referencecontainer" || field.type != MetkaJS.E.Field.REFERENCECONTAINER) return;

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
            // Add reference change listener for current row
            MetkaJS.EventManager.listen(MetkaJS.E.Event.REFERENCE_CONTAINER_CHANGE,
                reference.value, key,
                new MetkaJS.ReferenceHandler.ModelInputCallback(key, context, reference.value));
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
        saveRow: saveRow,
        handleReferenceOptions: handleReferenceOptions
    }
}();