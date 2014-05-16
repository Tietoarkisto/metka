$(document).ready(function () {
	'use strict';
	$('.popupContainer').dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		width: 'auto',
		height: 'auto'
	});

	$('.largePopupContainer').dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		width: '60%',
		height: 'auto'
	});
});