<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="context" value="${empty param.context ? fn:toUpperCase(page) : fn:toUpperCase(param.context)}" />
<div class="tabs tab_file_management contentBox">
    <script>
        $(document).ready(function() {
            var targetField = "${param.targetField}";
            var visCheck = function() {
                var name = $("#studyFileName").val();
                if(name != undefined && name != null && name != "") {
                    $("#studyFileUpload").show();
                } else {
                    $("#studyFileUpload").hide();
                }
            };
            visCheck();

            $("#studyFileName").click(function() {
                $("#studyFileInput").focus().click();
            });

            $("#studyFileInput").change(function() {
                var name = $(this).val();
                $("#studyFileName").val(name);
                visCheck();
                return false;
            })

            $("#studyFileUpload").click(function() {
                var file = $("#studyFileInput")[0].files[0];

                var data = new FormData();
                data.append("file", file);
                data.append("id", MetkaJS.SingleObject.id);
                data.append("targetField", targetField);
                $.ajax({
                    url: MetkaJS.url('fileUpload'),
                    type: "POST",
                    data: data,
                    processData: false,
                    contentType: false,
                    dataType: "text json",
                    error: function(jqXHR, textStatus, errorMessage) {
                        $("#studyFileUploadProgress").text("error");
                        alert(errorMessage);
                    }
                }).done(fileUploadSuccess);
            });

            function fileUploadSuccess(response) {
                if(response !== 'undefined' && response != null && response != "") {
                    response = JSON.parse(response);
                    response.temporary = true;
                    MetkaJS.TableHandler.saveRow(response, "${context}");
                } else {
                    $("#studyFileUploadProgress").text("couldn't save: "+response);
                }
            }
        });
    </script>
    <div id="tempContent"></div>
    <form class="fileUploadForm" method="POST" enctype="multipart/form-data">
        <div class="fileSelector">
            <input id="studyFileInput" class="hiddenFile" type="file" name="files[]" />
            <label for="studyFileName">Tiedosto</label>
            <span class="inputSpan"><input type="text" id="studyFileName" readonly="readonly"/></span>
            <input type="button" class="button" id="studyFileUpload" value="Lataa" />
            <span id="studyFileUploadProgress">0%</span>
            <span id="studyFileUploadProgress2"></span>
        </div>
    </form>
</div>