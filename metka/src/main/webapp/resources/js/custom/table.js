$(document).ready(function() {
    // Init autobuild tables
    $(".autobuild").each(function(index) {
        var id = $(this).attr("id");
        var content = JSON.parse($("#values\\'"+id+"\\'").val());
        buildDatatable(content);
    });
});

function buildDatatable(content) {
    // Handle only containers
    var key = content.key;
    var field = containerConfig[key];
    if(content.type != "container" || field.type != "CONTAINER") return;

    var body = $("#"+key+" tbody");
    body.empty();
    DialogOpener = function(containerKey, isNew, rowId) {
        return function() {
            showGeneralDialog(containerKey, isNew, rowId);
        }
    }
    for(var row = 0; row < content.rows.length; row++) {
        var tr = $("<tr>", {class: "pointerClass"});
        var rowContent = content.rows[row];
        var opener = new DialogOpener(key, false, rowContent.rowId);
        tr.click(opener);
        for(i = 0; i < containerConfig[key].subfields.length; i++) {
            var subfield = field.subfields[i];
            if(subfield.summaryField == false) {
                continue;
            }
            var subkey = subfield.key;
            var value = rowContent.fields[subkey];
            if(value == undefined || value == null) {
                // TODO: value is missing for some reason, make placeholder value so columns are not missing
                value = new Object();
                value.type = "value";
                value.value = "";
            }
            if((containerConfig[subkey] != undefined && containerConfig[subkey].type == "CONTAINER") || value.type != "value") {
                // TODO: Handle recursive containers somehow. Mostly datatable should not contain recursive containers
                return;
            }
            var td = $("<td>");
            td.text(value.value);
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
}

function showGeneralDialog(key, isNew, rowId) {
    $("#"+key+"ContainerDialog").dialog("open");
    // TODO: if isNew is true, clear all values or texts from the dialog that might be there
    if(isNew) {
        $("#"+key+"ContainerDialogRowId").val(null);
        $("#"+key+"ContainerDialogTable .dialogValue").val(null);
    } else {
        var content = JSON.parse($("#values\\'"+key+"\\'").val());
        var row = null;
        for(var i = 0; i < content.rows.length; i++) {
            if(content.rows[i].rowId == rowId) {
                row = content.rows[i];
                break;
            }
        }
        if(row != null) { // If row was found then use it, otherwise clear dialog and change to adding new row
            $("#"+key+"ContainerDialogRowId").val(row.rowId);
            for(var i = 0; i < containerConfig[key].subfields.length; i++) {
                var subfield = containerConfig[key].subfields[i];
                if(subfield == undefined) {
                    // Sanity check, although this means that something is very wrong
                    continue;
                } else if(subfield.type == "CONTAINER") {
                    // TODO: Handle recursive CONTAINERS
                    continue;
                }
                var input = $("#"+key+"Field"+subfield.key);
                // TODO: For now assumes input into val(), add exceptions as needed
                if(row.fields[subfield.key] == undefined || row.fields[subfield.key] == null) {
                    // TODO: value is missing for some reason, make placeholder value so columns are not missing
                    row.fields[subfield.key] = new Object();
                    row.fields[subfield.key].type = "value";
                    row.fields[subfield.key].value = "";
                }
                input.val(row.fields[subfield.key].value);
            }
        } else {
            $("#"+key+"ContainerDialogRowId").val(null);
            $("#"+key+"ContainerDialogTable .dialogValue").val(null);
        }
    }

    // TODO: if isNew is false then initialise inputs with values from container
}

function handleGeneralContainerDialog(key) {
    var body = $("#"+key+"ContainerDialogTable tbody");
    var row = new Object();
    row.type = "row";
    row.rowId = $("#"+key+"ContainerDialogRowId").val();
    row.key = key;
    row.fields = new Object();
    // RowId is set when row is added to container
    for(var i = 0; i < containerConfig[key].subfields.length; i++) {
        var subfield = containerConfig[key].subfields[i];
        if(subfield == undefined) {
            // Sanity check, although this means that something is very wrong
            continue;
        } else if(subfield.type == "CONTAINER") {
            // TODO: Handle recursive CONTAINERS
            continue;
        }
        var input = $("#"+key+"Field"+subfield.key);
        var value = new Object;
        value.type = "value";
        // TODO: Handle possible value extraction exception
        value.value = input.val();

        row.fields[subfield.key] = value;
    }

    saveDatatableRow(row);
    // TODO: check for missing required information
    $("#"+key+"ContainerDialog").dialog("close");
}

/**
 * Save a given CONTAINER row to right hidden input.
 * Makes needed changes to JSON and sets it to input.
 * If given row's rowId is null or undefined assumes new row, otherwise updates old row.
 * In case of modified row the row in original content is overwritten.
 * If original row is not found then row is assumed to be new, rowId is overwritten to null
 * and row is pushed to content.
 *
 * After modifications are done datatable is rebuilt.
 * @param row Row being saved to JSON hidden field
 */
function saveDatatableRow(row) {
    if(row.type != "row") return;
    // Fetch previous data from hidden field
    var contentStr = $("#values\\'"+row.key+"\\'").val();
    var content = null;
    if(contentStr == undefined || contentStr == "") {
        content = new Object();
        content.type = "container";
        content.key = row.key;
    } else {
        content = JSON.parse(contentStr);
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
    if(row.rowId != null && row.rowId != undefined) { // Check for old row
        for(var i = 0; i < content.rows.length; i++) {
            if(content.rows[i].rowId == row.rowId) {
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

    // Put modified data back to hidden field
    $("#values\\'"+row.key+"\\'").val(JSON.stringify(content));
    // Rebuild table
    buildDatatable(content);
}
