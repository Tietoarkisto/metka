MetkaJS.DialogHandlers.studyFilesHandler = function() {
    /**
     * If given key and handlerId match reference in a table then fetch data for that reference from server
     * and display it in a dialog with id "fileManagementDialog". Since special handlers are mostly table specific
     * there's no problem with hardcoding the dialog id in these cases, but if it becomes a problem the id
     * can be moved to a data attribute on the table.
     * @param key This should be 'files' but it's just assumed that the table contains file references
     * @param handlerId This should always be provided (this function doesn't support adding through dialog for now)
     * @param context Configuration context, ignored since this function should only work with STUDY_ATTACHMENT dialogs.
     */
    function showFileDialog(key, handlerId, context) {
        // If adding files through dialog comes possible then remove this check, for now make sure there is a handlerId
        if(handlerId === 'undefined' || handlerId == null) {
            return false;
        }
        // This function is for handling "fileManagementDialog". This information can be moved to a data-attribute at a later time
        var dialogId = "fileManagementDialog";
        // This function is designed to always work with "STUDY_ATTACHMENT" context.
        context = "STUDY_ATTACHMENT";

        // Clear values from dialog.
        $("#fileManagementRowId").val(null);
        $("#fileManagementRowId").data("handlerId", null);
        $("#"+dialogId+" .dialogValue").val(null);
        $("#"+dialogId+" .dialogValue").prop("readonly", false);

        // Get table content
        var content = MetkaJS.TableHandler.readContent(key);
        if((content === 'undefined' || content == null) && (handlerId !== 'undefined' && handlerId != null)) {
            // No content was found but this is supposed to be an existing row. Return false since we can't be sure about anything at this point.
            return false;
        }
        if(content.type != "referencecontainer") {
            // Handles only referencecontainers
            return false;
        }
        var reference = null;
        if(content != null && handlerId !== 'undefined' && handlerId != null) {
            for(var i=0; i<content.references.length; i++) {
                if(content.references[i].handlerId == handlerId) {
                    reference = content.references[i];
                    break;
                }
            }
        }
        // For now, only handles existing references
        if(reference != null) {
            $.ajax({
                type: "POST",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: "json",
                url: MetkaJS.PathBuilder().add("file").add("edit").add(reference.value).build()
            }).done(function(data) {
                    // Sanity check
                    if(data === 'undefined' || data == null) {
                        return false;
                    }

                    var to = data.transferObject;
                    var config = data.configuration;

                    // Insert revision and config to data-attributes on dialog. This makes it easy to collect the values again for sending them back to server
                    $("#"+dialogId).data("revision", to);
                    $("#"+dialogId).data("config", config);

                    $("#fileManagementRowId").val(reference.rowId); // Insert rowId to dialog, it might be needed during processing
                    $("#fileManagementRowId").data("handlerId", handlerId); // Insert handlerId to dialog, it might be needed during processing

                    for(var key in config.fields) {
                        $("#fileManagementField"+key).val(to.values[key]);
                        if(MetkaJS.SingleObject.draft == false
                                || config.fields[key].editable == false
                                || (
                                    config.fields[key].immutable == true
                                    && (to.values[key] !== 'undefined' && to.values[key] != null))
                                ) {
                            $("#fileManagementField"+key).prop("readonly", true);
                        }
                    }

                    $("#"+dialogId).dialog("open");
            });

            return true;
        }
    }

    /**
     * Process file properties dialog with id "fileManagementDialog".
     * Collects all value and sends them to server, then if save is successful closes the dialog.
     * Server should send back a result object, display result in alert message.
     * @param key Ignored
     * @param context Ignored
     */
    function processFileDialog(key, context) {
        var dialogId = "fileManagementDialog";
        var to = $("#"+dialogId).data("revision");
        var config = $("#"+dialogId).data("config");
        if(to == null || to === 'undefined' || config === 'undefined' || config == null) {
            // Can't continue if save object or config are missing, these are minimum requirements for processing a file and should be set while opening the dialog.
            return false;
        }
        for(var key in config.fields) {
            to.values[key] = $("#fileManagementField"+key).val();
        }

        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            data: JSON.stringify(to),
            url: MetkaJS.PathBuilder().add("file").add("save").build()
        }).done(function(data) {
            data = JSON.parse(data);
            if(data != null && data !== 'undefined') {
                // TODO: actual error message
                alert(data.result, "Tallentaminen");
            }
        }).fail(function() {
                // TODO: actual error message
                alert("Virhe tiedoston tallennuksessa", "Virhe");
        });

        $("#"+dialogId).dialog("close");
        $("#"+dialogId).data("revision", null);
        $("#"+dialogId).data("config", null);
        return true;
    }

    return {
        show: showFileDialog,
        process: processFileDialog
    }
}();