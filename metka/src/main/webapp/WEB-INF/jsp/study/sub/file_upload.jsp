<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<div class="tabs tab_file_management contentBox">
    <script>
        $(document).ready(function() {
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
                    /*.fileupload({
                url: MetkaGlobals.contextPath+"/file/upload",
                dataType: "json",
                autoUpload: false*/
/*
 ,
                done: function(e, data) {
                    $("#studyFileUploadProgress").val("done");
                },
                progressall: function(e, data) {
                    if(e.lengthComputable) {
                        $("#studyFileUploadProgress").val(parseInt((data.loaded / data.total) * 100) + '%');
                    }
                }
 });
 */

            /*$("#studyFileUpload").click(function(e) {
                var self = $("#studyFileInput");
                console.log(self.length, self[0], self[0].files);
                self.fileupload('send', {
                    files: self[0].files
                });
                //data.submit();
            });*/

            $("#studyFileUpload").click(function() {



                var file = $("#studyFileInput")[0].files[0];

                var data = new FormData();
                data.append("file", file);
                data.append("id", SingleObject.id);
                $.ajax({
                    url: MetkaGlobals.contextPath+"/file/upload",
                    type: "POST",
                    data: data,
                    processData: false,
                    contentType: false,
                    dataType: "text json",
                    error: function(jqXHR, textStatus, errorMessage) {
                        $("#studyFileUploadProgress").text("error");
                        alert(errorMessage);
                    }
                }).progress(function(e) {
                    if(e.lengthComputable) {
                        $("#studyFileUploadProgress").text((e.loaded / e.total) * 100 + '%');
                    }
                }).done(function(response) {
                    $("#studyFileUploadProgress").text("done");
                });
            });
        });
    </script>
    <div id="tempContent"></div>
    <form class="fileUploadForm" method="POST" enctype="multipart/form-data">
        <div class="fileSelector">
            <input id="studyFileInput" type="file" name="files[]" />
            <label for="studyFileName">Tiedosto</label>
            <span class="inputSpan"><input type="text" id="studyFileName" readonly="readonly"/></span>
            <input type="button" class="button" id="studyFileUpload" value="Lataa" />
            <span id="studyFileUploadProgress">0%</span>
        </div>
    </form>
</div>