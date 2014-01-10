$(document).ready(function(){

	$("#addNewSeriesButton").on("click", function() {
		window.location = "seriesView.html";
	});

	$(".seriesContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);

	$("#editSeriesButton").on("click", function() {
		$(".seriesContent .previewButton, .seriesContent .prevNextContainer").hide();
    	$(".seriesContent .addRow, .seriesContent .removeRow, .seriesContent .editButton").show();
    	$(".seriesContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", false);
	});

	$("#saveSeriesChangesButton").on("click", function() {
		$(".seriesContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);
		$(".seriesContent .prevNextContainer, .seriesContent .previewButton").show();
    	$(".seriesContent .addRow, .seriesContent .removeRow, .seriesContent .editButton").hide();
	});

});
