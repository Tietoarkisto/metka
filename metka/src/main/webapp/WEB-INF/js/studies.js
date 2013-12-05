$(document).ready(function(){
	
	$(".materialTabNavi ul li a").click(function(){	
		if(!$(this).hasClass("selected")){		
			$(".materialTabNavi ul li a").removeClass("selected");
			$(this).addClass("selected");
			var selectedId = $(this).attr("id");
			$(".tabs2").hide();
			$("." + selectedId).fadeIn("normal");
		}
	});	

	$(".materialRemovedFileRow").on("click", function() {
		showFileInfo($(this), "#materialRemovedFileInfoContent", "#materialRemovedFileInfoRow");	
	});

	function showFileInfo(fileElement, fileInfoTableId, fileInfoRowId) {
		var id = $(fileElement).attr("id");
		var fileName = $(fileElement).find(".materialFileName").html();
		var selectedId = $(fileInfoTableId + " table .fileInfoContentFileName").attr("id");

		if ( selectedId != id ) {
			$(fileInfoTableId + " table .fileInfoContentFileName").html(fileName);
			$(fileInfoTableId + " table .fileInfoContentFileName").attr("id", id);
			$(fileInfoRowId).show();
		} else {
			$(fileInfoTableId + " table .fileInfoContentFileName").html("");
			$(fileInfoTableId + " table .fileInfoContentFileName").attr("id", "");
			$(fileInfoRowId).hide();
		}	
	}

	$(".errorneousMaterialRow, .materialSearchResultRow, .desktopWidgetDataRow").on("click", function() {
		window.location = "materialView.html";
	});

	$(".materialMaterialRow, .publicationMaterialRow").on("click", function() {
		window.open("materialView.html");
	});

	$(".publicationSearchResultRow").on("click", function() {
		window.location = "publicationView.html";
	});
	$(".materialPublicationRow").on("click", function() {
		window.open("publicationView.html");
	});

	$(".seriesSearchResultRow, .materialSeriesRow, .publicationSeriesRow").on("click", function() {
		window.location = "seriesView.html";
	});
	$("materialSeriesRow, .publicationSeriesRow").on("click", function() {
		window.open("seriesView.html");
	});

	$(".materialCodebookRow").on("click", function() {
		$("#materialCodebookTitle a").html($(this).find(".materialCodebookFileName").html());
		$("#materialCodebookRow").show();
	});	
	
	$("#basicVariableTree").on("click", function() {
		$("#basicVariableTreeContainer").show();

		$("#groupedVariableTreeContainer").hide();
		$("#variableDataContainer").hide();
		$("#variablesGroupingContainer").hide();
	});

	$("#groupedVariableTree").on("click", function() {
		$("#groupedVariableTreeContainer").show();

		$("#basicVariableTreeContainer").hide();
		$("#variablesGroupingContainer").hide();
		$("#variableDataContainer").hide();
		$("#variableGroupData").hide();
	});

	$("#variablesGrouping").on("click", function() {
		$("#variablesGroupingContainer").show();

		$("#basicVariableTreeContainer").hide();
		$("#groupedVariableTreeContainer").hide();
		$("#variableDataContainer").hide();
		$("#variableGroupData").hide();
	});

	$('#variableFilterInput').fastLiveFilter('#variablesListBasic');

	$("#variablesListBasic li").on("click", function() {
		$("#variableDataContainer").show();
		$("#variableData").show();
		$("#variableGroupData").hide();
		$("#variablesListBasic li").each(function() {
			$(this).removeClass("selectedVariable");
		});
		$(this).addClass("selectedVariable");
		$("#var").val($(this).html());
	});

    $("#groupedVariableTreeContainer").fancytree({ 
		activate: function(event, data) {
			if ( data.node.children == null || data.node.children.length == 0 ) {
		        var node = data.node;
				$("#variableDataContainer").show();
				$("#variableData").show();
				$("#variableGroupData").hide();
				$("#var").val(node.title);
			} else {
				$("#variableDataContainer").show();
				$("#variableData").hide();
				$("#variableGroupData").show();
				$("#variableGroupData").find("textarea").html(data.node.title);
			}
		}
    });

    $("#variableGroupsBox").fancytree({
    	checkbox: true,
    	selectMode: 1, 
    	beforeExpand: function(event, data) {
    		// TODO
    		//alert(data.node.title);
    		//for ( var i = 0; i < data.node.children; i++ ) {
    		//	alert(data.node.children[i]);
    		//}
        	
        	//$("#variableGroupsBox ul.fancytree-container ul li span").each(function() {
        	//	alert("parent: " + $(this).parent().attr("class") + " this.class: " + $(this).attr("class") + " this.html" + $(this).html());
        		//$(this).parent().find(".fancytree-checkbox").remove();
        	//});
        }
    });

    $("#variablesToGroupArrowBox").on("click", function() {
    	$("#variablesBox li input:checked").each(function() {
    		$("#variableGroupsBox").fancytree("getTree");
    		$(this).parent().remove();
    	});
    });

	$('#variablesGroupingFilterInput').fastLiveFilter('#variablesGroupingVariablesList');

	$("#variablesGroupingVariablesList li").on("click", function() {
		$(this).find("input[type=checkbox]").attr("checked", "checked");
	});
	
	$("#addFolderButton").on("click", function() {
		var title = $("#newFolderName").val();
		if ( title == "" ) {
			alert("Aseta nimi");
		} else {
			var rootNode = $("#variableGroupsBox").fancytree("getRootNode");
		    rootNode.addChildren({
		        title: title,
		        folder: true
		    });
		}
	});
	
	$("#materialQualitySelect").on("change", function() {
		var materialQuality = $(this).children(":selected").attr("class");

		if ( materialQuality == "quantitative" ) {
			$("#materialType").removeClass("required");
			$("#materialFilingCategory").removeClass("required");
			$("#materialFSDCedes").addClass("required");

		} else if ( materialQuality == "qualitative" ) {
			$("#materialType").addClass("required");
			$("#materialFilingCategory").addClass("required");
			$("#materialFSDCedes").removeClass("required");
		
		} else if ( materialQuality == "both" ) {
		
		} else if ( materialQuality == "unknown" ) {
			$(".materialRowContainer label").removeClass("required");
		}
	});

	$("#addVocabularyButton").on("click", function() {
		alert($(this).find(".fancyboxpopup").attr("class"));
	});

	$(".versionHistoryButton, .publishMaterialButton, .approveChangesButton").on("click", function() {
		$(this).find("a").click();
	});

	$("#studyLevelData").accordion({
		heightStyle: "content",
		collapsible: true
	});

	var allVisible = false;

	$("#toggleAccordion").on("click", function() {
		if ( !allVisible ) {
			$('#studyLevelData .ui-accordion-content').show();
			allVisible = true;
			$(this).val("Sulje kaikki");
		} else {
			$('#studyLevelData .ui-accordion-content').hide();
			allVisible = false;
			$(this).val("Avaa kaikki");
		}
	});

	$(".materialFileRow, .materialErrorRow, .materialBinderRow, .versionRow, " + 
		".studyLevelIdRow, .parTitleRow, .otherMaterialRow, .relatedMaterialRow").on("click", function(e) {
		if($(e.target.nodeName).is('TD')){
			$(this).find("a").click();
		}
	});
	
	$("#addFilingContractFile").on("click", function() {
		$("#additionalFilingContractFile").toggle();
		$("#addFilingContractFile").toggle();
	});
	$("#removeAdditionalFilingContractFile").on("click", function() {
		$("#additionalFilingContractFile").toggle();
		$("#addFilingContractFile").toggle();
	});

	$("#addAltTitle, #addAppraisal, #addDataSource").on("click", function() {
		var newRow = $(this).parent().parent().parent().clone(true);
		$(newRow).find(".addRow").hide();
		$(newRow).find(".removeRow").show();
		$(newRow).insertAfter($(this).parent().parent().parent());
	});

	$(".removeAddedElement").on("click", function() {
		$(this).parent().parent().remove();
	});

	$(".materialContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);

    $(".reserveMaterialButton").on("click", function() {
    	$(".reservedButton, .handlerInfo").show();
    	$(".previewButton").hide();
    });
    $(".releaseMaterialButton").on("click", function() {
    	$(".reservedButton, .handlerInfo").hide();
    	$(".previewButton").show();
    });
    $(".editMaterialButton").on("click", function() {
    	$(".materialContent .reservedButton, .materialContent .previewButton, .publishedInfo, .materialContent .prevNextContainer").hide();
    	$(".materialContent .addRow, .materialContent .removeRow, .materialContent .editButton, .draftInfo").show();
    	$(".materialContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", false);
    	$(".weightCoefficient").attr("disabled", true);
    	$("#weightCoefficientToggle").attr("checked", true);
    });
	$(".saveAsDraftButton").on("click", function() {
		$(".reservedButton, .materialContent .prevNextContainer").show();
    	$(".materialContent .addRow, .materialContent .removeRow, .materialContent .editButton").hide();
    	$(".materialContent").find("select, input[type=text], textarea, input[type=checkbox]").attr("disabled", true);
	});

	$("#weightCoefficientToggle").on("click", function() {
		var checked = $(this).is(":checked");
		$(".weightCoefficient").attr("disabled", checked);
	});

	$(".packagingRow").on("click", function() {
		$("#packagingHistoryContainer").toggle();
	});
});