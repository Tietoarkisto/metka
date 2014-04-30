<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${fn:toUpperCase(page)}" />
<c:set var="readonly" value="${empty param.readonly ? false : param.readonly}" />
<div class="tabs tab_variables">
    <%-- Move to separate js-file if this gets too long --%>
    <script>
        $(document).ready(function() {
            MetkaJS.StudyVariablesHandler = function() {
                var content = null;

                /**
                 * Writes variables data back to hidden field.
                 * Variables is such an complex construct that it doesn't make sense to continually
                 * read and parse it and then save it back. Saving variable data should be made only when absolutely
                 * necessary.
                 */
                function saveVariables() {
                    MetkaJS.TableHandler.saveContent(content);
                }

                /**
                 * Builds variables list using JSON in hidden CONTAINER element like data tables.
                 * Empties variablesList and variableView divs.
                 */
                function buildVariables() {
                    // Remove all old variables and variableView content
                    $("#variablesList").empty();
                    $("#variableView table tbody").empty();

                    // Get "variables" CONTAINER content
                    content = MetkaJS.TableHandler.readContent("variables");
                    var alternate = false;
                    // If we have content then
                    if(content != null) {
                        for(var i=0; i<content.rows.length; i++) {
                            var row = content.rows[i];
                            var div = $("<div>", {id: row.rowId, class: "pointerClass"});
                            div.click(showVariableRequest);
                            if(alternate) {
                                div.addClass("studyVariableAlternate");
                            }
                            alternate = !alternate;
                            div.append(row.fields["varlabel"].value);
                            $("#variablesList").append(div);
                        }
                    }
                }

                function showVariableRequest() {
                    var div = $(this);
                    var rowId = div.attr("id");
                    var row = null;
                    for(var i=0; i<content.rows.length; i++) {
                        if(content.rows[i].rowId == rowId) {
                            row = content.rows[i];
                            break;
                        }
                    }
                    if(row == null) {
                        return;
                    }

                    // We have a row
                    showVariable(row);
                }

                function showVariable(row) {
                    var view = $("#variableView table tbody");
                    view.empty();

                    // Show name
                    addTextRow(row, "varname", view);
                    // Show label
                    addTextRow(row, "varlabel", view);
                    // Show question text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "qstnlit", view);
                    // Show pre text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "preqtxt", view);
                    // Show post text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "postqtxt", view);
                    // Show help text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "ivuinstr", view);
                    // Show notes text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "varnotes", view);
                    // Show additional text TODO: Change to using CONTAINER and support row adding
                    addTextAreaRow(row, "vartext", view);
                    // Show security text
                    addTextAreaRow(row, "varsecurity", view);
                    // Show categories
                    addCategories(row, view);
                    // Show statistics
                    addStatistics(row, view);
                }

                function addTextRow(row, key, view) {
                    var tr, td;
                    tr = $("<tr>");

                    // Add label
                    td = $("<td>", {class: "labelColumn"});
                    td.append(MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
                    tr.append(td);

                    // Add data
                    td = $("<td>");
                    var data = (row.fields[key] !== 'undefined' && row.fields[key] != null) ? row.fields[key].value : null;
                    if(data !== 'undefined' && data != null) {
                        td.append(data);
                    }
                    tr.append(td);
                    view.append(tr);
                }

                // TODO: Make container based thing for multiple value rows
                function addTextAreaRow(row, key, view) {
                    var tr, td;

                    // Add label
                    td = $("<td>", {class: "labelColumn"});
                    td.append(MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field."+key));
                    tr =  $("<tr>").append(td);

                    // Add data
                    td = $("<td>");
                    var input = $("<textarea>");
                    input.data("key", key);
                    input.data("rowId", row.rowId);
                    // TODO: Add change detector
                    input.on("change", saveTextAreaChange);
                    input.prop("readonly", !MetkaJS.SingleObject.draft);

                    var data = (row.fields[key] !== 'undefined' && row.fields[key] != null) ? row.fields[key].value : null;
                    if(data !== 'undefined' && data != null) {
                        input.append(data);
                    }

                    view.append(tr.append(td.append(input)));
                }

                function addCategories(row, view) {
                    var tr, td;
                    tr = $("<tr>");

                    // Add label
                    td = $("<td>", {class: "labelColumn"});
                    td.append(MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase()+".field.categories"));
                    tr.append(td);

                    // Add data
                    td = $("<td>");


                    var categories = row.fields["categories"];
                    if(categories !== 'undefined' && categories != null && categories.type == "container") {
                        var catTable = $("<table>", {class: "catTable"});
                        addCategoryRows(categories, catTable);
                        td.append(catTable);
                    }

                    tr.append(td);
                    view.append(tr);
                }

                function addCategoryRows(categories, table) {
                    var tbody = $("<tbody>");
                    table.append(tbody);
                    var tr, td;
                    for(var i=0; i<categories.rows.length; i++) {
                        // New row
                        tr = $("<tr>");
                        tbody.append(tr);

                        // Add cells
                        var appendColumn = function(tr, key, row) {
                            var td = $("<td>");
                            var data = null;
                            if(row.fields[key] !== 'undefined' && row.fields[key] != null) {
                                data = row.fields[key].value;
                            }
                            if(data != null) {
                                td.append(data);
                            }
                            tr.append(td);
                        }
                        var row = categories.rows[i];

                        appendColumn(tr, "categoryvalue", row);
                        appendColumn(tr, "categorylabel", row);
                        appendColumn(tr, "categorystat", row);
                    }
                }

                function addStatistics(row, view) {
                    var statistics = row.fields["statistics"];
                    if(statistics !== 'undefined' && statistics != null && statistics.type == "container") {
                        for(var i=0; i<statistics.rows.length; i++) {
                            var row = statistics.rows[i];
                            if(row !== 'undefined' && row != null) {
                                addStatisticsRow(statistics.rows[i], view);
                            }
                        }
                    }
                }

                function addStatisticsRow(row, view) {
                    var tr, td, data;
                    tr = $("<tr>");

                    var choicelist = MetkaJS.JSConfigUtil.getField("statisticstype", MetkaJS.E.Conf.STUDY).choicelist;

                    td = $("<td>", {class: "labelColumn"});
                    data = row.fields["statisticstype"];
                    if(data !== 'undefined' && data != null) {
                        td.append(MetkaJS.L10N.get("STUDY."+choicelist+".choices."+data.value));
                    }
                    tr.append(td);

                    td = $("<td>");
                    data = row.fields["statisticsvalue"];
                    if(data !== 'undefined' && data != null) {
                        td.append(data.value);
                    }
                    tr.append(td);

                    view.append(tr);
                }

                function saveTextAreaChange() {
                    var input = $(this);
                    var rowId = input.data("rowId");
                    var key = input.data("key");

                    var row = null;
                    for(var i=0; i<content.rows.length; i++) {
                        if(content.rows[i].rowId == rowId) {
                            row = content.rows[i];
                            break;
                        }
                    }
                    if(row == null) {
                        return;
                    }
                    var field = row.fields[key];
                    if(field === 'undefined' || field == null) {
                        field = new Object();
                        field.type = "value";
                        row.fields[key] = field;
                    }
                    field.value = input.val();
                    row.change = true;
                    saveVariables();
                }

                return {
                    build: buildVariables,
                    save: saveVariables
                }
            }();

            MetkaJS.StudyVariablesHandler.build();
        });
    </script>
    <form:hidden path="values['variables']" />
    <div class="twoColumns">
        <div id="variablesList">
            <%-- All variables are inserted here as separate divs in the order they appear in variables json --%>
        </div>
        <div id="variableView">
            <table>
                <tbody>

                </tbody>
            </table>
        </div>
    </div>

</div>