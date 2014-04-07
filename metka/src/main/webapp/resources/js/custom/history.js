$(document).ready(function(){

    $("#revisionsCloseBtn").click(function() {
        $("#revisionHistoryDialog").dialog("close");
    });

    $("#compareCloseBtn").click(function() {
        $("#revisionCompareDialog").dialog("close");
    });

    $("#compareRevisions").click(function() {
        var beginVal = $("input[name='beginGrp']:checked").val();
        var endVal = $("input[name='endGrp']:checked").val();
        if(beginVal == undefined || endVal == undefined) {
            return;
        }
        var request = new Object();
        request.id = MetkaJS.SingleObject.id;
        request.begin = parseInt(beginVal);
        request.end = parseInt(endVal);
        request.type = MetkaJS.Globals.page.toUpperCase();
        $.ajax({
            type: "POST",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: "json",
            url: MetkaJS.PathBuilder().add("history").add("revisions").add("compare").build(),
            data: JSON.stringify(request),
            success: function(response) {
                // Fill compare dialog

                var changesTable = $("#revisionChangesTable");
                var titleRow = changesTable.children().first();
                changesTable.empty();
                changesTable.append(titleRow);

                var tbody = $("<tbody>");
                for(var i = 0; i < response.changes.length; i++) {
                    var rowData = response.changes[i];
                    var row = $("<tr>", {class: "revisionHistoryDialogRow"});
                    var prop = "";
                    if(rowData["section"] != null) {
                        prop += MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".section."+rowData["section"]);
                        prop += ": ";
                    }
                    prop += MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field."+rowData["property"]);
                    row.append($("<td>", {class: "revisionTableColumn", text: prop}));

                    // TODO: server should only send strings suitable for display and nothing more.
                    if(rowData.type == "CONTAINER") {

                    } else {
                        if(rowData["oldValue"].length > 0) {
                            row.append($("<td>", {class: "revisionTableColumn", text: rowData["oldValue"][0]}));
                        }
                        if(rowData["newValue"].length > 0) {
                            row.append($("<td>", {class: "revisionTableColumn", text: rowData["newValue"][0]}));
                        }
                    }

                    tbody.append(row);
                }

                changesTable.append(tbody);

                // close version dialog
                $("#revisionHistoryDialog").dialog("close");

                // show compare dialog
                var str = MetkaJS.L10N.get("general.revision.compare.title");
                str = str.replace("{0}", response["begin"]);
                str = str.replace("{1}", response["end"]);

                $("#revisionCompareDialog").dialog("option", "title", str);
                $("#revisionCompareDialog").dialog("open");
            },
            error: function(e) {
                alert("Error: "+JSON.stringify(e, null, 4));
            }
        });
    })

    $("#showRevisions").click(function() {
        $.ajax({
            type: "GET",
            url: MetkaJS.PathBuilder().add("history").add("revisions").add(MetkaJS.SingleObject.id).build(),
            success: function(response) {
                var revisionTable = $("#revisionTable");
                var titleRow = revisionTable.children().first();
                revisionTable.empty();
                revisionTable.append(titleRow);
                var tbody = $("<tbody>");
                for(var i = 0; i < response.length; i++) {
                    var rowData = response[i];
                    var row = $("<tr>", {class: "revisionHistoryDialogRow"});
                    var td = $("<td>", {class: "revisionTableColumn"});
                    td.append($("<a>", {
                        href: MetkaJS.PathBuilder().add(MetkaJS.Globals.page).add("view").add(MetkaJS.SingleObject.id).add(rowData["revision"]).build(),
                        text: rowData["revision"]
                    }));
                    row.append(td);
                    if(rowData["state"]=="DRAFT") {
                        row.append($("<td>", {class: "revisionTableColumn", text: MetkaJS.L10N.get("general.title.DRAFT")}));
                    } else {
                        row.append($("<td>", {class: "revisionTableColumn", text: rowData["approvalDate"]}));
                    }
                    var radioColumn = $("<td>", {class: "revisionTableColumn"});
                    var input = $("<input>", {type: "radio", name: "beginGrp", value: rowData["revision"]});
                    input.change(checkRadioGroups);
                    radioColumn.append(input);
                    row.append(radioColumn);
                    radioColumn = $("<td>", {class: "revisionTableColumn"});
                    input = $("<input>", {type: "radio", name: "endGrp", value: rowData["revision"]});
                    input.change(checkRadioGroups);
                    radioColumn.append(input);
                    row.append(radioColumn);
                    if(MetkaJS.SingleObject.draft) {
                        var replaceColumn = $("<td>", {class: "revisionTableColumn"});
                        replaceColumn.append($("<input>", {type: "button", class: "button", value: MetkaJS.L10N.get("general.revision.replace")}));
                        row.append(replaceColumn);
                    }
                    tbody.append(row);
                }

                revisionTable.append(tbody);
                checkRadioGroups();
                if(MetkaJS.SingleObject.draft) {
                    $("#revisionHistoryDialog #replaceColumn").show();
                } else {
                    $("#revisionHistoryDialog #replaceColumn").hide();
                }
                $("#revisionHistoryDialog").dialog("open");
            },
            error: function(e) {
                alert("Error: "+JSON.stringify(e, null, 4));
            }
        });
    });
});

function checkRadioGroups() {
    var beginVal = $("input[name='beginGrp']:checked").val();
    var endVal = $("input[name='endGrp']:checked").val();
    if(beginVal != undefined) {
        $("input[name='endGrp']").each(function(){
            if($(this).val() <= beginVal) {
                $(this).attr("disabled", true);
            } else {
                $(this).attr("disabled", false);
            }
        });
    }
    if(endVal != undefined) {
        $("input[name='beginGrp']").each(function(){
            if($(this).val() >= endVal) {
                $(this).attr("disabled", true);
            } else {
                $(this).attr("disabled", false);
            }
        });
    }
    if(beginVal == undefined || endVal == undefined) {
        $("#compareRevisions").attr("disabled", true);
    } else {
        $("#compareRevisions").attr("disabled", false);
    }
}