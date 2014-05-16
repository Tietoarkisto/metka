$(document).ready(function () {
	'use strict';

	$('#revisionsCloseBtn').click(function () {
		$('#revisionHistoryDialog').dialog('close');
	});

	$('#compareCloseBtn').click(function () {
		$('#revisionCompareDialog').dialog('close');
	});
});