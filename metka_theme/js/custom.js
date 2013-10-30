$(document).ready(function(){
	/** GENERAL **/

	$(".tabNavi ul li a").click(function(){
		if(!$(this).hasClass("selected")){		
			var currentId = $(".tabNavi ul li a.selected").attr("id"); 
			$(".tabNavi ul li a").removeClass("selected");
			$(this).addClass("selected");
			var selectedId = $(this).attr("id");
			if(currentId > selectedId){ 
				$(".tabs").hide();
				$(".tab" + selectedId).show();
			}else{
				$(".tabs").hide();
				$(".tab" + selectedId).show();
			}
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
		".materialSearchResultRow, .publicationSearchResultRow, .seriesSearchResultRow, " + 
		".materialSeriesRow, .materialPublicationRow, .materialMaterialRow, #variablesListBasic li, " +
		".publicationSeriesRow, .publicationMaterialRow, .materialBinderRow, .binderRow, " + 
		".materialRemovedFileRow, #filingContractFile, .versionRow, " + 
		".link, .translationLink, .translationLinkEn, .translationLinkSv, .translationLinkFi, .variableTranslationLink, " + 
		".removeAddedElement").hover(function() {
		    $(this).css('cursor', 'pointer');
		}, function() {
		    $(this).css('cursor', 'auto');		    
	});


	$(".translationLinkSv").on("click", function() {
		$(".translationSv").toggle();
		
		if ( $(this).hasClass("clicked")) {
			$(".translationLinkSv").removeClass("clicked");
		} else {
			$(".translationLinkSv").addClass("clicked");
		}
		toggleTranslationVisibility($(this));
	});
 
	$(".translationLinkEn").on("click", function() {
		$(".translationEn").toggle();
		if ( $(this).hasClass("clicked")) {
			$(".translationLinkEn").removeClass("clicked");
		} else {
			$(".translationLinkEn").addClass("clicked");
		}
		toggleTranslationVisibility($(this));
	});

	function toggleTranslationVisibility(linkElement) {
		var translationSvVisible = $(linkElement).parent().find(".translationLinkSv").hasClass("clicked");
		var translationEnVisible = $(linkElement).parent().find(".translationLinkEn").hasClass("clicked");

		if ( !translationEnVisible && !translationSvVisible ) {
			$(".rowContainer:not(.containsTranslations)").show();
			$(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").show();
			// Jos toinen tiedosto, näytä tämä
			$("#additionalFilingContractFile").hide();
		} else {
			$(".rowContainer:not(.containsTranslations)").hide();
			$(".materialDataSetContainer:not(.translated), .materialDataSetTextareaContainer:not(.translated)").hide();
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
        "bAutoWidth": false
    });
    
    $('#standardTextTable').dataTable( {
		"bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": true,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false
    });
    
    
    /*** AINEISTO ***/
    
	$(".materialTabNavi ul li a").click(function(){	
		if(!$(this).hasClass("selected")){		
			var currentId = $(".materialTabNavi ul li a.selected").attr("id"); 
			$(".materialTabNavi ul li a").removeClass("selected");
			$(this).addClass("selected");
			var selectedId = $(this).attr("id");
			if(currentId > selectedId){ 
				$(".tabs2").hide();
				$(".tab" + selectedId).fadeIn("normal");
			}else{
				$(".tabs2").hide();
				$(".tab" + selectedId).fadeIn("normal");
			}
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
	
	$("#materialPersonTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '20%'},
		              {sWidth: '20%'},
		              {sWidth: '10%'},
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
		              {sWidth: '15%'},
		              {sWidth: '80%'},
		              {sWidth: '5%'}
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
		              {sWidth: '40%'},
		              {sWidth: '30%'},
		              {sWidth: '10%'},
		              {sWidth: '15%'},
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
	
	$("#materialVersionTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '15%'},
		              {sWidth: '20%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
        ]
	});

	$(".studyLevelOrderedTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false
	}).rowReordering();

	$(".materialFileRow").on("click", function() {
		showFileInfo($(this), "#materialFileInfoContent", "#materialFileInfoRow");	
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
	
	$(".materialErrorRow").on("click", function() {
		var id = $(this).attr("id");
		var selectedId = $("#materialErrorInfoContent table thead tr").attr("id");

		if ( selectedId != id ) {
			$("#materialErrorInfoContent table thead tr").attr("id", id);
			$("#materialErrorInfoRow").show();
		} else {
			$("#materialErrorInfoContent table thead tr").attr("id", "");
			$("#materialErrorInfoRow").hide();
		}	
	});

	$(".materialSearchResultRow, .publicationMaterialRow, .materialMaterialRow, .desktopWidgetDataRow").on("click", function() {
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

	$("#variableTranslationLinkSv").on("click", function() {
		if ( $(this).hasClass("clicked")) {
			$(this).removeClass("clicked");
			$("#basicVariableTreeContainer").show();
			$("#variableTranslationSv").hide();
		} else {			
			$(this).addClass("clicked");
			$("#basicVariableTreeContainer").hide();

			if ( $("#variableTranslationLinkEn").hasClass("clicked") ) {
				$("#variableTranslationLinkEn").removeClass("clicked");
				$("#variableTranslationEn").hide();
			}
			$("#variableTranslationSv").show();
		}
	});

	$("#variableTranslationLinkEn").on("click", function() {
		if ( $(this).hasClass("clicked")) {
			$(this).removeClass("clicked");
			$("#basicVariableTreeContainer").show();
			$("#variableTranslationEn").hide();
		} else {			
			$(this).addClass("clicked");
			$("#basicVariableTreeContainer").hide();			
			if ( $("#variableTranslationLinkSv").hasClass("clicked") ) {
				$("#variableTranslationLinkSv").removeClass("clicked");
				$("#variableTranslationSv").hide();
			}
			$("#variableTranslationEn").show();
		}
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

	$(".materialBinderRow, .binderRow").on("click", function(e) {
		if($(e.target.nodeName).is('TD')){
			$(this).find(".showBinderInfo").click();
		}
	});
	
	$("#filingContractFile").on("click", function() {
		$("#additionalFilingContractFile").toggle();
	});

	$(".versionRow").on("click", function(e) {
		if($(e.target.nodeName).is('TD')){
			$(this).find(".showVersionInfo").click();
		}
	});

	$("#studyLevelAltTitle, #studyLevelAppraisal, #studyLevelDataSource").on("click", function() {
		var newRow = $(this).parent().parent().clone(true);
		$(newRow).find(".link").attr("id", "");
		$(newRow).find("label").removeClass("link");
		$(newRow).find("img").show();
		$(newRow).insertAfter($(this).parent().parent());
	});

	$(".removeAddedElement").on("click", function() {
		$(this).parent().parent().remove();
	});

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
        "bFilter": false, 
        "bInfo": true,
        "bPaginate": true,
        "bAutoWidth": false, 
        "sPaginationType": "full_numbers",
        "aoColumns": [
        	{sWidth: '20%'},
        	{sWidth: '75%'},
        	{sWidth: '5%'}
        ], 
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
