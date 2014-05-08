$(document).ready(function () {
	'use strict';

	// Init autobuild tables
	$('.autobuild').each(function (index) {
		var id = $(this).attr('id');
		MetkaJS.TableHandler.build(id, $(this).data('context'));
	});
});