$(document).ready(function(){
    $( "#seriesSearchSubmit" ).click(function() {
        $( "#seriesSearchForm" ).submit();
    });

    $( "#seriesSave" ).click(function() {
        $( "#modifyForm" ).attr("action", contextPath+"/series/save");
        $( "#modifyForm" ).submit();
	});

    $( "#seriesApprove" ).click(function() {
        $("#modifyForm").attr("action", contextPath+"/series/approve");
        $("#modifyForm").submit();
    });
});
