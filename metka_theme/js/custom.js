$(document).ready(function(){
	/** GENERAL **/

	$(".tabNavi ul li a").click(function(){
		if(!$(this).hasClass("selected")){		
			var currentId = $(".tabNavi ul li a.selected").attr("id"); 
			$(".tabNavi ul li a").removeClass("selected");
			$(this).addClass("selected");
			var selectedId = $(this).attr("id");
			$(".tabs").hide();
			$("." + selectedId).show();
			$(".searchResult").hide();
		}
	});

	
	$(".saveNewQueryButton").click(function(){
		$(".saveNewQuery").fadeIn("slow");		
	});

	$(".querySaveBtn").click(function(){
		$(".saveNewQuery").fadeOut("slow");		

	});
	$(".doSearch").click(function(){
		$(".searchResult").fadeIn("slow");
	});

	$(".sortableTable").tablesorter();

	$( ".datepicker" ).datepicker();

	$(".fancyboxpopup").fancybox();

	jQuery(function($){
	    $.datepicker.regional['fi'] = {
			closeText: 'Sulje',
			prevText: '&laquo;Edellinen',
			nextText: 'Seuraava&raquo;',
			currentText: 'T&auml;n&auml;&auml;n',
			monthNames: ['Tammikuu','Helmikuu','Maaliskuu','Huhtikuu','Toukokuu','Kes&auml;kuu','Hein&auml;kuu','Elokuu','Syyskuu','Lokakuu','Marraskuu','Joulukuu'],
			monthNamesShort: ['Tammi','Helmi','Maalis','Huhti','Touko','Kes&auml;','Hein&auml;','Elo','Syys','Loka','Marras','Joulu'],
			dayNamesShort: ['Su','Ma','Ti','Ke','To','Pe','Su'],
			dayNames: ['Sunnuntai','Maanantai','Tiistai','Keskiviikko','Torstai','Perjantai','Lauantai'],
			dayNamesMin: ['Su','Ma','Ti','Ke','To','Pe','La'],
			weekHeader: 'Vk',
			dateFormat: 'dd.mm.yy',
			firstDay: 1,
			isRTL: false,
			showMonthAfterYear: false,
			yearSuffix: ''};
	    $.datepicker.setDefaults($.datepicker.regional['fi']);
	}); 
	
	$(".materialFileRow, .materialCodebookFileRow, .materialErrorRow, .desktopWidgetDataRow, " +
		".errorneousMaterialRow, .materialSearchResultRow, .publicationSearchResultRow, .seriesSearchResultRow, " + 
		".materialSeriesRow, .materialPublicationRow, .materialMaterialRow, #variablesListBasic li, " +
		".publicationSeriesRow, .publicationMaterialRow, .materialBinderRow, .binderRow, .link, " +
		".studyLevelIdRow, .parTitleRow, .otherMaterialRow, .relatedMaterialRow" + 
		".removeAddedElement, .versionRow, .materialNotificationRow").hover(function() {
		    $(this).css('cursor', 'pointer');
		}, function() {
		    $(this).css('cursor', 'auto');		    
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
			$(".translationBorder").addClass("translationEnBorder");
			$("#materialNameEnInput").attr("disabled", false);
		} else if ( language == "sv" ) {
			$(".translationSv").show();
			toggleFinnishTranslations(true);
			$(".translationBorder").addClass("translationSvBorder");
		}
	});

	function toggleFinnishTranslations(hide) {
		$(".translationFi").find("input").attr("disabled", hide);
		$(".translationFi").find("textarea").attr("disabled", hide);
		$(".translationFi").find("select").attr("disabled", hide);
		if ( hide ) {
			$(".rowContainer:not(.containsTranslations)").hide();
			$(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").hide();
			$(".studyLevelDataSetContainer:not(.translated), .studyLevelDataSetContainer:not(.translated)").hide();
			$("#normalDesktop").hide();
			$("#translatorDesktop").show();
			$(".translationBorder").removeClass("translationEnBorder");
			$(".translationBorder").removeClass("translationSvBorder");
			$("#studyLevelData").find(".translationFi").find("a").hide();
			$("#studyLevelData").find(".translationFi").find(".addNewElement").hide();
			$("#studyLevelData").find(".translationFi").find(".removeAddedElement").hide();
		} else {
			$(".rowContainer:not(.containsTranslations)").show();
			$(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").show();
			$(".studyLevelDataSetContainer:not(.translated), .studyLevelDataSetTextareaContainer:not(.translated)").show();
			$("#normalDesktop").show();
			$("#translatorDesktop").hide();
			$(".translationBorder").removeClass("translationSvBorder");
			$(".translationBorder").removeClass("translationEnBorder");
			$("#studyLevelData").find(".translationFi").find("a").show();
			$("#studyLevelData").find(".translationFi").find(".addNewElement").show();
			$("#studyLevelData").find(".translationFi").find(".removeAddedElement").show();
		}
	}

	/** ASETUKSET **/
	
    $('#vocabularyTable').dataTable( {
		"bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": true,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false,
        "oLanguage": {
		    "sProcessing":   "Hetkinen...",
		    "sLengthMenu":   "Näytä kerralla _MENU_ riviä",
		    "sZeroRecords":  "Tietoja ei löytynyt",
		    "sInfo":         "Näytetään rivit _START_ - _END_ (yhteensä _TOTAL_ )",
		    "sInfoEmpty":    "Näytetään 0 - 0 (yhteensä 0)",
		    "sInfoFiltered": "(suodatettu _MAX_ tuloksen joukosta)",
		    "sInfoPostFix":  "",
		    "sSearch":       "Etsi:",
		    "sUrl":          "",
		    "oPaginate": {
		        "sFirst":    "Ensimmäinen",
		        "sPrevious": "Edellinen",
		        "sNext":     "Seuraava",
		        "sLast":     "Viimeinen"
		    }
		}
    });
    
    $('#standardTextTable').dataTable( {
		"bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": true,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false,
        "oLanguage": {
		    "sProcessing":   "Hetkinen...",
		    "sLengthMenu":   "Näytä kerralla _MENU_ riviä",
		    "sZeroRecords":  "Tietoja ei löytynyt",
		    "sInfo":         "Näytetään rivit _START_ - _END_ (yhteensä _TOTAL_ )",
		    "sInfoEmpty":    "Näytetään 0 - 0 (yhteensä 0)",
		    "sInfoFiltered": "(suodatettu _MAX_ tuloksen joukosta)",
		    "sInfoPostFix":  "",
		    "sSearch":       "Etsi:",
		    "sUrl":          "",
		    "oPaginate": {
		        "sFirst":    "Ensimmäinen",
		        "sPrevious": "Edellinen",
		        "sNext":     "Seuraava",
		        "sLast":     "Viimeinen"
		    }
		}
    });
    
    
    /*** AINEISTO ***/
    
	$(".materialTabNavi ul li a").click(function(){	
		if(!$(this).hasClass("selected")){		
			var currentId = $(".materialTabNavi ul li a.selected").attr("id"); 
			$(".materialTabNavi ul li a").removeClass("selected");
			$(this).addClass("selected");
			var selectedId = $(this).attr("id");
			$(".tabs2").hide();
			$("." + selectedId).fadeIn("normal");
		}
	});

	$("#materialSeriesTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
					  {sWidth: '15%'},
		              {sWidth: '40%'},
		              {sWidth: '40%'},
		              {sWidth: '5%'}
        ]
     });
	
	$("#materialPublicationTable, #materialMaterialTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '80%'},
		              {sWidth: '5%'}
				  ]
	});

	$("#materialBinderTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '20%'},
		              {sWidth: '80%'}
				  ]
	});
	
	$("#materialAuthorTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,	
        "aoColumns": [
		              {"bVisible": false},
		              {sWidth: '35%'},
		              {sWidth: '40%'},
		              {sWidth: '20%'},
		              {sWidth: '5%'}
				  ]
	}).rowReordering();
	
	$("#materialCodebookFileTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '15%'},
		              {sWidth: '10%'},
		              {sWidth: '30%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '5%'}
				  ]
	}).rowReordering();

	$("#materialFileInfoTable, #materialRemovedFileTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '20%'},
		              {sWidth: '30%'},
		              {sWidth: '50%'}
        ]
	});

	$("#materialFileTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false, 
        "aoColumns": [
        	{sWidth: '40%'},
        	{sWidth: '40%'},
        	{sWidth: '15%'},
        	{sWidth: '5%'}
        ]
	});

	$("#materialErrorsTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '5%'},
		              {sWidth: '15%'},
		              {sWidth: '15%'},
		              {sWidth: '45%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'}
        ]
	});

	$("#materialNotificationTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '75%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '5%'}
        ]
	});
	
	$(".materialVersionTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '20%'},
		              {sWidth: '60%'}
        ]
	});

	$(".studyLevelOrderedTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false
	}).rowReordering();

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

	$(".errorneousMaterialRow, .materialSearchResultRow, .publicationMaterialRow, .materialMaterialRow, .desktopWidgetDataRow").on("click", function() {
		if ( $(this).hasClass("published") ) {
			window.location = "materialViewPUBLISHED.html";
		} else {
			window.location = "materialView.html";
		}
	});

	$(".publicationSearchResultRow, .materialPublicationRow").on("click", function() {
		window.location = "publicationView.html";
	});
	$(".seriesSearchResultRow, .materialSeriesRow, .publicationSeriesRow").on("click", function() {
		window.location = "seriesView.html";
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
    		var tree = $("#variableGroupsBox").fancytree("getTree");
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
		    var childNode = rootNode.addChildren({
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

	$("#versionHistoryButton").on("click", function() {
		$("#versionHistoryLink").click();
	});
	$("#approveChangesButton").on("click", function() {
		$("#approveChangesLink").click();
	});        
	$("#publishMaterialButton").on("click", function() {
		$("#publishMaterialLink").click();
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

	$(".materialFileRow, .materialErrorRow, .materialBinderRow, .binderRow, .versionRow, " + 
		".studyLevelIdRow, .parTitleRow, .otherMaterialRow, .relatedMaterialRow").on("click", function(e) {
		if($(e.target.nodeName).is('TD')){
			$(this).find("a").click();
		}
	});
	
	$("#filingContractFile").on("click", function() {
		$("#additionalFilingContractFile").toggle();
	});

	$("#addAltTitle, #addAppraisal, #addDataSource").on("click", function() {
		var newRow = $(this).parent().parent().parent().clone(true);
		$(newRow).find(".addNewElement").hide();
		$(newRow).find(".removeAddedElement").show();
		$(newRow).insertAfter($(this).parent().parent().parent());
	});

	$(".removeAddedElement").on("click", function() {
		$(this).parent().parent().remove();
	});


    $("#startWorkingButton").on("click", function() {
    	$(".shownButton").hide();
    	$(".additionalButton").show();
    	$(".draft").show();
    	$(".handler").show();
    	$(".version").hide();
    	$(".editButton").show();
    });
    $("#startTranslatingButton").on("click", function() {
    	$(".shownButton").hide();
    	$(".additionalButton").show();
    	$(".draft").show();
    	$(".handler").show();
    	$(".version").hide();
    	$(".translatorButton").show();
    	$(".editButton").hide();
    	// $(".rowContainer").hide();
    	// $(".containsTranslations").show();
    });


    // $("#toggle").on("click", function() {
  		// $(".readOnly").toggle();
  		// $(".materialDataSetContainer input, .materialDataSetContainer select, .materialDataSetContainerTopRow input").toggle();	
    // });

	/*** JULKAISU ***/
	
	$("#publicationPersonTable, #publicationIdentificationTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '50%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#publicationMaterialTable, #publicationSeriesTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '20%'},
		              {sWidth: '75%'},
		              {sWidth: '5%'}
				  ]
	});

	$("#addNewPublicationButton").on("click", function() {
		window.location = "publicationView.html";
	});


	/* SARJAT */

	$("#addNewSeriesButton").on("click", function() {
		window.location = "seriesView.html";
	});

	// createDataTable("publicationMaterialTable", ['20%','75%','5%']);

	// function creataDataTable(tableId, columnWidths) {
	// 	alert("foo");
	// 	var columns = [];
	// 	for ( var i = 0; i < columnWidths; i++ ) {
	// 		alert(columnWidths[i]);
	// 		columns[i] = {sWidth: columnWidths[i]};
	// 	}
	// 	var foo = [{sWidth: '20%'},{sWidth: '25%'},{sWidth: '55%'}];
	// 	$("#" + tableId).dataTable({
	// 		"bPaginate": false,
	//         "bFilter": false, 
	//         "bInfo": false,
	//         "bAutoWidth": false,
	//         "aoColumns": foo
	// 	});
	// }

	/** MAPIT **/

// Käännökset fileen ja haku sUrlilla
	$("#binderTable").dataTable({
        "bFilter": true, 
        "bInfo": true,
        "bPaginate": true,
        "bAutoWidth": false, 
        "sPaginationType": "full_numbers",
        "aoColumns": [
        	{sWidth: '10%'},
        	{sWidth: '30%'},
        	{sWidth: '15%'},
        	{sWidth: '10%'},
        	{sWidth: '35%'}
        ], 
        "aLengthMenu": [[10, 25, 50, 100, -1],[10, 25, 50, 100, "Kaikki"]],
        "oLanguage": {
		    "sProcessing":   "Hetkinen...",
		    "sLengthMenu":   "Näytä kerralla _MENU_ riviä",
		    "sZeroRecords":  "Tietoja ei löytynyt",
		    "sInfo":         "Näytetään rivit _START_ - _END_ (yhteensä _TOTAL_ )",
		    "sInfoEmpty":    "Näytetään 0 - 0 (yhteensä 0)",
		    "sInfoFiltered": "(suodatettu _MAX_ tuloksen joukosta)",
		    "sInfoPostFix":  "",
		    "sSearch":       "Etsi:",
		    "sUrl":          "",
		    "oPaginate": {
		        "sFirst":    "Ensimmäinen",
		        "sPrevious": "Edellinen",
		        "sNext":     "Seuraava",
		        "sLast":     "Viimeinen"
		    }
		}
	});
});
