$(document).ready(function(){

	$(".binderNumber").on("click", function() {
		$("#binderList").hide();
		$("#binderInfo").show();
	});
	$("#backToBinderList").on("click", function() {
		$("#binderList").show();
		$("#binderInfo").hide();
	});
});