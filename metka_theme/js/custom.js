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
		              {sWidth: '30%'},
		              {sWidth: '10%'},
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
		              {sWidth: '80%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'}
        ]
	});

	$(".materialFileRow, .materialCodebookFileRow, .materialErrorRow, .desktopWidgetDataRow, " +
		".materialSearchResultRow, .publicationSearchResultRow, .seriesSearchResultRow, " + 
		".materialSeriesRow, .materialPublicationRow, .materialMaterialRow, " + 
		".publicationSeriesRow, .publicationMaterialRow").hover(function() {
		    $(this).css('cursor', 'pointer');
		}, function() {
		    $(this).css('cursor', 'auto');		    
	});
	
	$(".materialFileRow").on("click", function() {
		var id = $(this).attr("id");
		var fileName = $(this).find(".materialFileName").html();
		var selectedId = $("#materialFileInfoContent table .fileInfoContentFileName").attr("id");

		if ( selectedId != id ) {
			$("#materialFileInfoContent table .fileInfoContentFileName").html(fileName);
			$("#materialFileInfoContent table .fileInfoContentFileName").attr("id", id);
			$("#materialFileInfoRow").show();
		} else {
			$("#materialFileInfoTitle table .fileInfoContentFileName").html("");
			$("#materialFileInfoContent table .fileInfoContentFileName").attr("id", "");
			$("#materialFileInfoRow").hide();
		}	
	});
	
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
		$("#variablesTreeBasic").show();
		$("#variablesTreeGrouped").hide();

		$("#variableDataContent").hide();
		$("#variableGroupDataContent").hide();
	});

	$("#groupedVariableTree").on("click", function() {
		$("#variablesTreeGrouped").show();
		$("#variablesTreeBasic").hide();

		$("#variableDataContent").hide();
	});

	$("#variablesTreeBasic").fancytree({ 
		activate: function(event, data) {
	        var node = data.node;
			$("#variablesData").show();
			$("#variableDataContent").show();
			$("#variableGroupDataContent").hide();
			$("#var2").val(node.title);
		}
    });

    $("#variablesTreeGrouped").fancytree({ 
    	extensions: ["dnd", "filter"],
    	filter: {
    		mode: "hide"
	    },
		activate: function(event, data) {
			if ( data.node.children == null || data.node.children.length == 0 ) {
		        var node = data.node;
				$("#variablesData").show();
				$("#variableDataContent").show();
				$("#variableGroupDataContent").hide();
				$("#var2").val(node.title);
			} else {
				$("#variablesData").show();
				$("#variableGroupDataContent").show();
				$("#variableGroupDataContent").find("textarea").html(data.node.title);
				$("#variableDataContent").hide();
			}
		}, 
		dnd: {
	        preventVoidMoves: true, 
	        preventRecursiveMoves: true, 
	        autoExpandMS: 400,
	        onDragStart: function(node) {
	          return true;
	        },
	        onDragEnter: function(node, sourceNode) {
	           return true;
	        },
	        onDrop: function(node, sourceNode, hitMode, ui, draggable) {
	          sourceNode.moveTo(node, hitMode);
	        }
		}
    });

    // $("input[name=variableFilter]").keyup(function(e){
    // 	var tree = $("#variablesTreeBasic").fancytree("getTree");

    // 	if(e && e.which === $.ui.keyCode.ESCAPE || $.trim(match) === ""){
    // 		$("input[name=variableFilter]").val("")
    // 		tree.clearFilter();
    // 	}
    // 	var match = $(this).val();
    // 	var n = tree.applyFilter(match);
    // }).focus();

	
	$("#addFolderButton").on("click", function() {
		var title = $("#newFolderName").val();
		if ( title == "" ) {
			alert("Aseta nimi");
		} else {
			var rootNode = $("#variablesTreeGrouped").fancytree("getRootNode");
		    var childNode = rootNode.addChildren({
		        title: title,
		        folder: true
		    });
		}
	});
	
	$("#materialQualitySelect").on("change", function() {
		var materialQuality = $(this).children(":selected").attr("id");

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
});
