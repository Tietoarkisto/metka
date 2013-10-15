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
					  {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '20%'},
		              {sWidth: '25%'},
		              {sWidth: '20%'},
		              {sWidth: '5%'}
        ]
     });
	
	$("#materialPersonTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '10%'},
		              {sWidth: '20%'},
		              {sWidth: '25%'},
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
		              {sWidth: '50%'},
		              {sWidth: '45%'},
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
		              {sWidth: '10%'},
		              {sWidth: '15%'},
		              {sWidth: '10%'},
		              {sWidth: '40%'},
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

	$('.materialFileRow, .materialCodebookFileRow').hover(function() {
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
	
	$(".materialCodebookRow").on("click", function() {
		$("#materialCodebookTitle a").html($(this).find(".materialCodebookFileName").html());
		$("#materialCodebookRow").show();
	});	
	
	$("#variablesTree").jstree({ "plugins" : ["themes","html_data","ui"] })
	        .bind("loaded.jstree", function (event, data) { })
	        .one("reopen.jstree", function (event, data) { })
	        .one("reselect.jstree", function (event, data) { })
	        .bind("select_node.jstree", function (event, data) {
				$("#variableDataContent").html(data.rslt.obj.attr("id") + " :" + data.rslt.obj.find("a").html());
				$(".selectedVariableId").attr("id", data.rslt.obj.attr("id"));
				//$(".variablePrev").attr("id", );
				//$(".variableNext").attr("id", );
			});
	
	$("#approveChanges").on("click", function() {
		confirm("Oletko varma?");
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
