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

	/**
	 * Replace native alert with jQuery dialog.
	 * This provides non blocking alert dialog that can be styled.
	 *
	 * @param message - Message to be shown to user
	 * @param title - Title for this alert
	 */
	window.alert = function (message, title) {
		if (!title) {
			title = 'general.errors.title.notice';
		}
		var alertDlg = $('#alertDialog')
			.clone()
		.dialog({
			autoOpen: false,
			resizable: false,
			modal: true,
			width: 'auto',
			height: 'auto',
			title: MetkaJS.L10N.get(title)
		});

		alertDlg.find('#alertContent').text(message);
		alertDlg.find('#alertCloseBtn').click(function () {
			alertDlg.dialog('close');
		});
		alertDlg.dialog('open');
	};

	/**
	 * Replace native confirm with jQuery dialog.
	 * This gives better styling options as well as better control.
	 * @param message - Message to be shown to user
	 * @param title - Title for this dialog
	 * @param execute - Callback to be executed if user confirms action
	 */
	window.confirm = function confirmation(message, title, execute) {
		if (!title) {
			title = 'general.confirmation.title.confirm';
		}
		var confirm = $('#confirmationDialog').clone()
			.dialog({
				autoOpen: false,
				resizable: false,
				modal: true,
				width: 'auto',
				height: 'auto',
				title: MetkaJS.L10N.get(title)
			});

		confirm.find('#confirmationContent').text(message);
		if (execute) {
			confirm.find('#confirmationYesBtn').click(execute);
		} else {
			confirm.find('#confirmationYesBtn').click(function () {
				confirm.dialog('close');
			});
		}
		confirm.find('#confirmationNoBtn').click(function () {
			confirm.dialog('close');
		});

		confirm.dialog('open');
	};
});