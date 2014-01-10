$(document).ready(function(){
	$("#errorTypeSelect").on("change", function() {
		var errorType = $(this).children(":selected").attr("id");

		if ( errorType == "fileError") {
			$("#fileNameErrorRow").show();
		} else {
			$("#fileNameErrorRow").hide();
		}
	});
	
	$("#authorType").on("change", function() {
		var authorType = $(this).children(":selected").attr("id");

		if ( authorType == "personAuthor") {
			$(".organizationAuthor").hide();
			$(".personAuthor").show();
			$(".newOrganizationField").hide();
		} else if ( authorType == "organizationAuthor" ) {
			$(".personAuthor").hide();
			$(".organizationAuthor").show();
			$(".newOrganizationField").hide();
		} 
	});	

	$(".organizationSelect").on("change", function() {
		var organization = $(this).children(":selected").attr("class");

		if ( organization == "otherOrganization") {
			$(".newOrganizationField").show();
		} else {
			$(".newOrganizationField").hide();
		}
	});
	
	$("#collSelect").on("change", function() {
		var topic = $(this).children(":selected").attr("id");

		if ( topic == "chooseColl") {
			$(".wordFields").hide();
			$(".otherCollInput").hide();
		} else if ( topic == "otherColl") {
			$(".otherCollInput").show();
			$(".wordFields").show();
			$("#collectionId").attr("disabled", false);
			$("#collectionId").val("");
		}
		else {
			$(".wordFields").show();
			$(".otherCollInput").hide();
			$("#collectionId").attr("disabled", true);
			$("#collectionId").val("fooBar");
		} 
	});	
	
	$("#authorTypeSelect").on("change", function() {
		var authorType = $(this).children(":selected").attr("id");

		if ( authorType == "personCollector") {
			$(".organizationCollector").hide();
			$(".personCollector").show();
			$(".newOrganizationField").hide();
		} else if ( authorType == "organizationCollector" ) {
			$(".personCollector").hide();
			$(".organizationCollector").show();
			$(".newOrganizationField").hide();
		} 
	});	

	$(".organizationSelect").on("change", function() {
		var organization = $(this).children(":selected").attr("class");

		if ( organization == "otherOrganization") {
			$(".newOrganizationField").show();
		} else {
			$(".newOrganizationField").hide();
		}
	});
	
	$("#vocabularySelect").on("change", function() {
		var vocabularyType = $(this).children(":selected").attr("id");

		if ( vocabularyType == "chooseVocabulary") {
			$(".vocabularyFields").hide();
			$(".keywordFields").hide();
			$(".otherKeywordInput").hide();
		} else if ( vocabularyType == "noVocabulary") {
			$(".otherKeywordInput").show();
			$(".vocabularyFields").hide();
		} else {
			$(".vocabularyFields").show();
		} 
	});	

	$("#keywordSelect").on("change", function() {
		var keywordType = $(this).children(":selected").attr("id");

		if ( keywordType == "chooseKeyword") {
			$(".keywordFields").hide();
			$(".otherKeywordInput").hide();
		}
		else {
			$(".keywordFields").show();
			$(".otherKeywordInput").hide();
		} 
	});	
	
	$("#organizationSelect").on("change", function() {
		var organization = $(this).children(":selected").attr("id");
		if ( organization == "chooseOrganization") {
			$(".organizationField").hide();
			$(".newOrganizationField").hide();
		} else if ( organization == "otherOrganization") {
			$(".newOrganizationField").show();
			$(".organizationField").show();
		} else {
			$(".organizationField").show();
			$(".newOrganizationField").hide();
		}
	});
	
	$("#vocabularySelect").on("change", function() {
		var vocabularyType = $(this).children(":selected").attr("id");

		if ( vocabularyType == "chooseVocabulary") {
			$(".vocabularyFields").hide();
			$(".topicFields").hide();
		} else {
			$(".vocabularyFields").show();
		} 
	});	

	$("#topicSelect").on("change", function() {
		var topic = $(this).children(":selected").attr("id");

		if ( topic == "chooseTopic") {
			$(".topicFields").hide();
		} else {
			$(".topicFields").show();
		} 
	});
	
	$("#errorTypeSelect").on("change", function() {
		var errorType = $(this).children(":selected").attr("id");

		if ( errorType == "fileError") {
			$("#fileNameErrorRow").show();
			$("#studyLevelErrorRow").hide();
		} else if ( errorType == "studyLevelError" ) {
			$("#studyLevelErrorRow").show();
			$("#fileNameErrorRow").hide();
		} else {
			$("#fileNameErrorRow").hide();
			$("#studyLevelErrorRow").hide();
		}
	});
	
	$("input[type=radio][name=language]").on("click", function() {
		var language = $(this).val();
			$(".translationSv").hide();
			$(".translationEn").hide();

		if ( language == "fi" ) {
			toggleFinnishTranslations(false);
		} else if ( language == "en" ) {
			$(".translationEn").show();
			toggleFinnishTranslations(true);
		} else if ( language == "sv" ) {
			$(".translationSv").show();
			toggleFinnishTranslations(true);
		}
	});

	$("#compareVersionsButton").on("click", function() {
		// show compare dialog
	});

	$("input[type=checkbox][name=version]").click(function() {
		var bool = $("input[type=checkbox][name=version]:checked").length >= 2;     
    	$("input[type=checkbox][name=version]").not(":checked").attr("disabled",bool);
	});

	function toggleFinnishTranslations(hide) {
		$(".translationFi").find("input").attr("disabled", hide);
		$(".translationFi").find("textarea").attr("disabled", hide);
		$(".translationFi").find("select").attr("disabled", hide);
		if ( hide ) {
			$(".translationFi").find("a").hide();
		} else {
			$(".translationFi").find("a").show();
		}
	} 
	

});
