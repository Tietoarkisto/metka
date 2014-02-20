$(document).ready(function(){
    $( "#studySearchSubmit" ).click(function() {
        $( "#studySearchForm" ).submit();
    });

    $( "#studySave" ).click(function() {
        $( "#modifyForm" ).attr("action", contextPath+"/study/save");
        $( "#modifyForm" ).submit();
    });

    $( "#studyApprove" ).click(function() {
        $("#modifyForm").attr("action", contextPath+"/study/approve");
        $("#modifyForm").submit();
    });
});