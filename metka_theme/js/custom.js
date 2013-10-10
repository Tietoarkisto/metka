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

	$(".fancyboxpopup").fancybox();
	
	
	/** ASETUKSET **/
	
    $('#sanastoTable').dataTable( {
		"bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": true,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false
    });
    
    $('#vakiotekstiTable').dataTable( {
		"bJQueryUI": true,
        "bPaginate": false,
        "bLengthChange": false,
        "bFilter": true,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false
    });
    
    
    /*** AINEISTO ***/
    
	$(".aineistoTabNavi ul li a").click(function(){
		console.log("1");		
		if(!$(this).hasClass("selected")){		
			var currentId = $(".aineistoTabNavi ul li a.selected").attr("id"); 
			$(".aineistoTabNavi ul li a").removeClass("selected");
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
	
	$("#aineistoHenkiloTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '20%'},
		              {sWidth: '20%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#aineistoJulkaisuTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '50%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#aineistoAineistoTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '50%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
				  ]
	});
	
	$("#aineistoFileTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '50%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
				  ]
	});
	
	$('.aineistoFileRow').hover(function() {
		    $(this).css('cursor', 'pointer');
		}, function() {
		    $(this).css('cursor', 'auto');		    
		});
	
	$(".aineistoFileRow").on("click", function() {
		$("#aineistoFileInfoTitle a").html($(this).find(".aineistoFileName").html());
		$("#aineistoFileInfoRow").show();
	});
	
	/*** JULKAISU ***/
	
	$("#julkaisuHenkiloTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '50%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#julkaisuAineistoTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '20%'},
		              {sWidth: '75%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#julkaisuSarjaTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '10%'},
		              {sWidth: '85%'},
		              {sWidth: '5%'}
				  ]
		
	});
	$("#julkaisuLupaTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false,
        "aoColumns": [
		              {sWidth: '10%'},
		              {sWidth: '85%'},
		              {sWidth: '5%'}
				  ]
		
	});
});
