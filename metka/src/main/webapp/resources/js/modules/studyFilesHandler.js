define(function (require) {
	'use strict';

    return {
		/**
		 * If given key and handlerId match reference in a table then fetch data for that reference from server
		 * and display it in a dialog with id "fileManagementDialog". Since special handlers are mostly table specific
		 * there's no problem with hardcoding the dialog id in these cases, but if it becomes a problem the id
		 * can be moved to a data attribute on the table.
		 * @param key This should be 'files' but it's just assumed that the table contains file references
		 * @param handlerId This should always be provided (this function doesn't support adding through dialog for now)
		 * @param context Configuration context, ignored since this function should only work with STUDY_ATTACHMENT dialogs.
		 */
		show: function (key, handlerId, context) {
			// If adding files through dialog comes possible then remove this check, for now make sure there is a handlerId
			if (typeof handlerId === 'undefined' || handlerId === null) {
				return false;
			}
			// This function is for handling "fileManagementDialog". This information can be moved to a data-attribute at a later time
			var dialogId = 'fileManagementDialog';
			// This function is designed to always work with "STUDY_ATTACHMENT" context.
			context = 'STUDY_ATTACHMENT';
			// Clear values from dialog.
			$('#fileManagementRowId').val(null);
			$('#fileManagementRowId').data('handlerId', null);
			$('#' + dialogId + ' .dialogValue').val(null);
			$('#' + dialogId + ' .dialogValue').prop('readonly', false);
			// Get table content
			var content = MetkaJS.TableHandler.readContent(key);
			if ((typeof content === 'undefined' || content === null) && (typeof handlerId !== 'undefined' && handlerId !== null)) {
				// No content was found but this is supposed to be an existing row. Return false since we can't be sure about anything at this point.
				return false;
			}
			if (content.type !== 'referencecontainer') {
				// Handles only referencecontainers
				return false;
			}
			var reference = null;
			var i;
			if (content !== null && typeof handlerId !== 'undefined' && handlerId !== null) {
				for (i = 0; i < content.references.length; i++) {
					if (content.references[i].handlerId === handlerId) {
						reference = content.references[i];
						break;
					}
				}
			}
			// For now, only handles existing references
			if (reference !== null) {
				$.ajax({
					type: 'POST',
					headers: {
						'Accept': 'application/json',
						'Content-Type': 'application/json'
					},
					dataType: 'json',
					url: require('./url')('fileEdit', reference)
				}).done(function (data) {
					// Sanity check
					if (typeof data === 'undefined' || data === null) {
						return false;
					}
					var to = data.transferObject;
					var config = data.configuration;
					// Insert revision and config to data-attributes on dialog. This makes it easy to collect the values again for sending them back to server
					$('#' + dialogId).data('revision', to);
					$('#' + dialogId).data('config', config);
					$('#fileManagementRowId').val(reference.rowId);
					// Insert rowId to dialog, it might be needed during processing
					$('#fileManagementRowId').data('handlerId', handlerId);
					// Insert handlerId to dialog, it might be needed during processing
					var key;
					for (key in config.fields) {
						if (config.fields.hasOwnProperty(key)) {
							$('#fileManagementField' + key).val(to.values[key]);
							if (require('./../metka').state !== 'DRAFT' || !config.fields[key].editable || (config.fields[key].immutable && (typeof to.values[key] !== 'undefined' && to.values[key] !== null))) {
								$('#fileManagementField' + key).prop('readonly', true);
							}
						}
					}
					$('#' + dialogId).dialog('open');
				});
				return true;
			}
		},
		/**
		 * If given key and handlerId match reference in a table then fetch data for that reference from server
		 * and display it in a dialog with id "fileManagementDialog". Since special handlers are mostly table specific
		 * there's no problem with hardcoding the dialog id in these cases, but if it becomes a problem the id
		 * can be moved to a data attribute on the table.
		 * @param key This should be 'files' but it's just assumed that the table contains file references
		 * @param handlerId This should always be provided (this function doesn't support adding through dialog for now)
		 * @param context Configuration context, ignored since this function should only work with STUDY_ATTACHMENT dialogs.
		 */
		process: function (key, context) {
			var dialogId = 'fileManagementDialog';
			var to = $('#' + dialogId).data('revision');
			var config = $('#' + dialogId).data('config');
			if (to === null || typeof to === 'undefined' || typeof config === 'undefined' || config === null) {
				// Can't continue if save object or config are missing, these are minimum requirements for processing a file and should be set while opening the dialog.
				return false;
			}
			var fieldKey;
			for (fieldKey in config.fields) {
				if (config.fields.hasOwnProperty(fieldKey)) {
					to.values[fieldKey] = $('#fileManagementField' + fieldKey).val();
				}
			}
			$.ajax({
				type: 'POST',
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/json'
				},
				dataType: 'json',
				data: JSON.stringify(to),
				url: require('./url')('fileSave')
			}).done(function (data) {
				if (data !== null && data !== 'undefined') {
					var message = MetkaJS.MessageManager.Message(data.title, data.msg);
					MetkaJS.MessageManager.show(message);
				}
				MetkaJS.EventManager.notify(MetkaJS.E.Event.DIALOG_EVENT, {
					target: dialogId,
					id: to.id
				});
			}).fail(function () {
				// TODO: actual error message
				alert('Virhe tiedoston tallennuksessa', 'Virhe');
			});
			$('#' + dialogId).dialog('close');
			$('#' + dialogId).data('revision', null);
			$('#' + dialogId).data('config', null);
			return true;
		}
	};
});