$(document).ready(function(){
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
}