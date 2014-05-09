(function () {
	'use strict';
	/**
	 * Insert default general handler
	 * Handlers should be found from a data-attribute called 'handler'.
	 * All handlers should provide at least basic interface functions 'show' and 'process'.
	 * show: function(key[, handlerId, context])
	 *      - key: Field key of the table containing data for the dialog). Required
	 *      - handlerId: Handler id of the requested row. This is used to fetch information for dialog. Not required. If it's not provided then it's assumed this is a new object.
	 *      - context: Configuration context. Under which key should field configuration be found. This can be ignored in the actual handler.
	 *      show should return boolean telling whether the operation was successful or not.
	 * process: function([key, context])
	 *      - key: Field key of the container that should contain the data after it's collected from dialog. This can be ignored by the handler (for example if the data is automatically send to server)
	 *      - context: Configuration context. Under which key should field configuration be found. This can be ignored in the actual handler.
	 *      process should return boolean telling whether the operation was successful or not.
	 */
	MetkaJS.DialogHandlers.generalContainerHandler = {
		/**
		 * Displays a dialog matching given key and initialises it matching given parameters.
		 * @param key For what container should the dialog be opened.
		 * @param isNew Is the dialog supposed to add or modify.
		 * @param row Existing row for which data should be shown in the dialog.
		 */
		show: function (key, handlerId, context) {
			var field = MetkaJS.JSConfigUtil.getField(key, context);
			if (field.type !== MetkaJS.E.Field.CONTAINER) {
				// Sanity check, this function is meant only for CONTAINERS
				return;
			}
			// Reset dialog, these are only changed if a row was found so until then we can assume new row.
			$('#' + key + 'ContainerDialogRowId').val(null);
			$('#' + key + 'ContainerDialogRowId').data('handlerId', null);
			$('#' + key + 'ContainerDialogTable .dialogValue').val(null);
			$('#' + key + 'ContainerDialogTable .dialogValue').prop('readonly', false);
			// Get content so we can find the actual data.
			var content = MetkaJS.TableHandler.readContent(key);
			if ((typeof content === 'undefined' || content === null) && (typeof handlerId !== 'undefined' && handlerId !== null)) {
				// No content was found but this is supposed to be an existing row. Return false since we can't be sure about anything at this point.
				return false;
			}
			var row = null;
			var found = false;
			var i, length;
			if (content !== null && typeof handlerId !== 'undefined' && handlerId !== null) {
				for (i = 0; i < content.rows.length; i++) {
					if (content.rows[i].handlerId === handlerId) {
						row = content.rows[i];
						break;
					}
				}
			}
			var newRow = row === null || row.handlerId === null;
			// If row was given then use it, otherwise clear dialog and change to adding new row
			if (!newRow) {
				$('#' + key + 'ContainerDialogRowId').val(row.rowId);
				// rowId field can double as handler id data storage since this field is always present and doesn't relate to actual data
				$('#' + key + 'ContainerDialogRowId').data('handlerId', row.handlerId);
			}
			field.subfields.forEach(function (val) {
				var subfield = MetkaJS.JSConfigUtil.getField(val);
				if (subfield === null) {
					// Sanity check, although this means that something is very wrong
					return;
				} else if (subfield.type === MetkaJS.E.Field.CONTAINER) {
					// TODO: Handle recursive CONTAINERS
					return;
				} else if (subfield.type === MetkaJS.E.Field.REFERENCECONTAINER) {
					// TODO: Handle recursive REFERENCECONTAINERS
					return;
				}
				// Get input for handling
				var input = $('#' + key + 'Field' + subfield.key);
				if (!newRow) {
					if (typeof row.fields[subfield.key] === 'undefined' || row.fields[subfield.key] === null) {
						row.fields[subfield.key] = {
							type: 'value',
							value: ''
						};
					}
					// For now assumes input into val(), add exceptions if needed
					input.val(row.fields[subfield.key].value);
				}
				if (MetkaJS.isReadOnly(subfield)) {
					input.prop('readonly', true);
				} else {
					input.prop('readonly', false);
				}
				MetkaJS.ReferenceHandler.handleReference(subfield.key, context, null, key);
			});
			// Open dialog
			$('#' + key + 'ContainerDialog').dialog('open');
		},
		/**
		 * Forms a row from general dialog and saves it to content using MetkaJS.TableHandler.saveRow function
		 * @param key Key of target CONTAINER field
		 * @param context Configuration context
		 */
		process: function (key, context) {
			var body = $('#' + key + 'ContainerDialogTable tbody');
			var row = {};
			row.type = 'row';
			row.rowId = $('#' + key + 'ContainerDialogRowId').val();
			// Handler id is important since it tells if this row was existing or not.
			row.handlerId = $('#' + key + 'ContainerDialogRowId').data('handlerId');
			row.key = key;
			row.fields = {};
			var field = MetkaJS.JSConfigUtil.getField(key, context);
			// RowId is set when row is added to container
			field.subfields.forEach(function (val) {
				var subfield = MetkaJS.JSConfigUtil.getField(val, context);
				if (subfield === null) {
					// Sanity check, although this means that something is very wrong
					return;
				} else if (subfield.type === MetkaJS.E.Field.CONTAINER) {
					// TODO: Handle recursive CONTAINERS
					return;
				}
				var input = $('#' + key + 'Field' + subfield.key);
				var value = {};
				value.type = 'value';
				// TODO: Handle possible value extraction exception
				value.value = input.val();
				row.fields[subfield.key] = value;
			});
			MetkaJS.TableHandler.saveRow(row, context);
			// TODO: check for missing required information
			$('#' + key + 'ContainerDialog').dialog('close');
		}
	};

	/**
	 * This provides a fallback handler for reference tables.
	 * Since all reference tables require their own custom handlers this will just
	 * throw an alert with suitable error message informing that the operation is not supported yet.
	 */
	MetkaJS.DialogHandlers.generalReferenceHandler = {
		show: function (key, handlerId, context) {
			var message = MetkaJS.L10N.get('general.errors.container.dialog.noImplementation');
			message = message.replace('{0}', key);
			message = message.replace('{1}', MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase() + '.field.' + key));
			alert(message, 'general.errors.title.noImplementation');
		},
		process: function (key, context) {
			var message = MetkaJS.L10N.get('general.errors.container.dialog.noImplementation');
			message = message.replace('{0}', key);
			message = message.replace('{1}', MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase() + '.field.' + key));
			alert(message, 'general.errors.title.noImplementation');
		}
	};
}());