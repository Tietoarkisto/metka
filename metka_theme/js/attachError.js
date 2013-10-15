$(document).ready(function(){
	$("#errorTypeSelect").on("change", function() {
		var errorType = $(this).children(":selected").attr("id");

		if ( errorType == "fileError") {
			$("#fileNameErrorRow").show();
		} else {
			$("#fileNameErrorRow").hide();
		}
	});
});
