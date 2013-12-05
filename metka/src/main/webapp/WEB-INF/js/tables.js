$(document).ready(function(){
	
	$("#binderRelatedMaterialsTable").dataTable({
		"bPaginate": true,
        "bFilter": false, 
        "bInfo": true, 
        "bAutoWidth": false,
			"sPaginationType": "full_numbers",
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '85%'}
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
	
	$("#binderRelatedMaterialsTable").dataTable({
		"bPaginate": true,
        "bFilter": false, 
        "bInfo": true, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '15%'},
		              {sWidth: '85%'}
        ]
	});
	
	$(".sanastoTablesorter").dataTable( {
		"bJQueryUI": true,
		"sPaginationType": "full_numbers",
		"bLengthChange": false,
		"bFilter": true,
		"bSort": true,
		"bInfo": false,
		"bAutoWidth": false

	} );
	
	// Käännökset fileen ja haku sUrlilla
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

	// Käännökset fileen ja haku sUrlilla
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

    $("#savedQueries").dataTable( {
		"bPaginate": false,
        "bLengthChange": false,
        "bFilter": false,
        "bSort": false,
        "bInfo": false,
        "bAutoWidth": false,
        "aoColumns": [
        	{sWidth: "60%"},
        	{sWidth: "20%"},
        	{sWidth: "10%"},
        	{sWidth: "10%"},
        ]
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

	$(".studyLevelCollectingTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '35%'},
		              {sWidth: '30%'},
		              {sWidth: '30%'},
		              {sWidth: '5%'}
        ]
	});

	$(".studyLevelTwoHeadersTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '55%'},
		              {sWidth: '45%'},
		              {sWidth: '5%'}
        ]
	});

	$(".studyLevelVocabularyTable").dataTable({
		"bPaginate": false,
        "bFilter": false, 
        "bInfo": false, 
        "bAutoWidth": false,
        "aoColumns": [
		              {sWidth: '10%'},
		              {sWidth: '45%'},
		              {sWidth: '20%'},
		              {sWidth: '20%'},
		              {sWidth: '5%'}
        ]
	});
	
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