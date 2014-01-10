$(document).ready(function(){
	$("#addNewPublicationButton").on("click", function() {
		window.location = "publicationView.html";
	});

	$(".publicationContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);

	$("#editPublicationButton").on("click", function() {
		$(".publicationContent .previewButton, .publicationContent .prevNextContainer").hide();
    	$(".publicationContent .editButton, .publicationContent .addRow, .publicationContent .removeRow, .publicationContent .editButton").show();
    	$(".publicationContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", false);
	});

	$("#savePublicationChangesButton").on("click", function() {
		$(".publicationContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);
		$(".publicationContent .prevNextContainer, .publicationContent .previewButton").show();
    	$(".publicationContent .addRow, .publicationContent .removeRow, .publicationContent .editButton").hide();
	});
});