(function () {
    'use strict';

    MetkaJS.TableHandler = (function() {
        /**
         * Returns a function capable of showing a dialog through given handler with given properties
         *
         * @param handler Name of the handler used
         * @param key Field key of the container-field for this dialog
         * @param handlerId Id of the row requesting dialog, can be null
         * @param context Configuration context for the opened dialog, can be null
         * @returns {Function} Callback function used to open the dialog
         * @constructor
         */
        var DialogOpener = function (handler, key, handlerId, context) {
            return function () {
                MetkaJS.DialogHandlers[handler].show(key, handlerId, context);
            };
        };

        /**
         * Reads out top level content (if it exists) with given key into a Javascript object.
         * @param key Key of the field where the content is found
         * @returns Javascript object representing the JSON string in a field, null if no data found
         */
        function readContent(key) {
            var field = MetkaJS.getValuesInput(key);
            if (field !== null && field.val() !== 'undefined' && field.val() !== null && field.val() !== '') {
                return JSON.parse(field.val());
            }
            return null;
        }

        /**
         * Save given content back to a field.
         * Checks to see that the content, as well as the field exists so you can't clear a whole table with this.
         * @param content Content to be saved, must not be null or doesn't do anything at all.
         */
        function saveContent(content) {
            var input = MetkaJS.getValuesInput(content.key);
            if (input === null) {
                return;
            }
            input.val(JSON.stringify(content));
        }

        /**
         * Checks which function is needed to build a table for given content
         * @param content
         */
        function buildTable(key, context) {
            var content = readContent(key);
            if (content !== null) {
                if (content.type === 'container') {
                    buildContainertable(content, context);
                } else if (content.type === 'referencecontainer') {
                    buildReferencetable(content, context);
                }
            }
        }

        /**
         * Builds a table for given container content.
         * Follows configuration parameters for table columns and display of extra columns
         * @param content JSON object, usually from hidden field. Must match type container.
         */
        function buildContainertable(content, context) {
            // Handle only containers
            var key = content.key;
            var field = MetkaJS.JSConfigUtil.getField(key, context);
            var changes = false;
            if (content.type !== 'container' || field.type !== MetkaJS.E.Field.CONTAINER) {
                return;
            }
            var body = $('#' + key + ' tbody');
            body.empty();
            var handler = $('#' + key).data('handler');
            var row, i;
            content.rows.forEach(function (rowContent) {
                var tr = $('<tr>', {'class': 'pointerClass'});
                if (typeof rowContent.handlerId === 'undefined' || rowContent.handlerId === null) {
                    // Add handler id so row can be found after editing, these do not affect actual data on serverside in any way
                    rowContent.handlerId = MetkaJS.Globals.globalId();
                    changes = true;
                }
                var opener = new DialogOpener(handler, key, rowContent.handlerId, context);
                tr.click(opener);
                if (rowContent.temporary) {
                    tr.addClass('temporary');
                } else {
                    tr.removeClass('temporary');
                }

                field.subfields.some(function (val) {
                    var subfield = MetkaJS.JSConfigUtil.getField(val, context);
                    if (!subfield.summaryField) {
                        return; // continue
                    }
                    var value = rowContent.fields[subfield.key];
                    if (typeof value === 'undefined' || value === null) {
                        // TODO: value is missing for some reason, make placeholder value so columns are not missing
                        value = {};
                        value.type = 'value';
                        value.value = '';
                    }
                    if (value.type !== 'value' || subfield.type === MetkaJS.E.Field.CONTAINER || subfield.type === MetkaJS.E.Field.REFERENCECONTAINER) {
                        // TODO: Handle recursive containers somehow. Mostly table should not contain recursive containers
                        return true; // break
                    }
                    var td = $('<td>');
                    switch (subfield.type) {
                        case MetkaJS.E.Field.SELECTION:
                            td.text(MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase() + '.' + subfield.selectionList + '.option.' + value.value));
                            break;
                        default:
                            td.text(value.value);
                            break;
                    }
                    tr.append(td);
                });

                if (field.showSaveInfo) {
                    var value = rowContent.savedAt;
                    var td = $('<td>');
                    if (typeof value !== 'undefined') {
                        td.text(value);
                    }
                    tr.append(td);
                    value = rowContent.savedBy;
                    td = $('<td>');
                    if (typeof value !== 'undefined') {
                        td.text(value);
                    }
                    tr.append(td);
                }
                body.append(tr);
            });

            if (changes) {
                // Save content back to field since there has been new handlerId:s inserted
                // These are not saved to database but are vital to handling dialogs and modifying content
                saveContent(content);
            }
        }

        /**
         * Builds a table for given referencecontainer content.
         * Follows configuration parameters for table columns and display of extra columns
         * @param content JSON object, usually from hidden field. Must match type container.
         */
        function buildReferencetable(content, context) {
            // Handle only reference containers
            var key = content.key;
            var field = MetkaJS.JSConfigUtil.getField(key, context);
            var changes = false;
            if (content.type !== 'referencecontainer' || field.type !== MetkaJS.E.Field.REFERENCECONTAINER) {
                return;
            }
            var body = $('#' + key + ' tbody');
            body.empty();
            var handler = $('#' + key).data('handler');
            content.references.forEach(function (reference) {
                var tr = $('<tr>', {'class': 'pointerClass'});
                tr.data('reference', reference.value);
                if (reference.temporary) {
                    tr.addClass('temporary');
                } else {
                    tr.removeClass('temporary');
                }
                if (typeof reference.handlerId === 'undefined' || reference.handlerId === null) {
                    // Add handler id so reference can be found after editing, these do not affect actual data on serverside in any way
                    reference.handlerId = MetkaJS.Globals.globalId();
                    changes = true;
                }
                var opener = new DialogOpener(handler, key, reference.handlerId, context);
                var value, td;
                tr.click(opener);
                // Display reference id if required
                if (field.showReferenceKey) {
                    value = reference.value;
                    td = $('<td>');
                    if (typeof value !== 'undefined') {
                        td.text(value);
                    }
                    tr.append(td);
                }
                // TODO: display extra fields for referenced information
                field.subfields.forEach(function (subkey) {
                    var subfield = MetkaJS.JSConfigUtil.getField(subkey, context);
                    if (subfield.summaryField) {
                        var td = $('<td>');
                        td.data('key', subkey);
                        tr.append(td);
                    }
                });

                if (field.showSaveInfo) {
                    value = reference.savedAt;
                    td = $('<td>');
                    if (typeof value !== 'undefined') {
                        td.text(value);
                    }
                    tr.append(td);
                    value = reference.savedBy;
                    td = $('<td>');
                    if (typeof value !== 'undefined') {
                        td.text(value);
                    }
                    tr.append(td);
                }
                // Add reference change listener for current row
                MetkaJS.EventManager.listen(MetkaJS.E.Event.REFERENCE_CONTAINER_CHANGE, reference.value, key, new MetkaJS.ReferenceHandler.ReferenceContainerCallback(key, context, reference.value));
                body.append(tr);
                MetkaJS.ReferenceHandler.handleReference(key, context, reference.value);
            });
            if (changes) {
                // Save content back to field since there has been new handlerId:s inserted
                // These are not saved to database but are vital to handling dialogs and modifying content
                saveContent(content);
            }
        }

        function saveRow(row, context) {
            if (row.type === 'row') {
                saveContainerRow(row, context);
            } else if (row.type === 'reference') {
                saveReferenceRow(row, context);
            }
        }

        /**
         * Save a given CONTAINER row to right hidden input.
         * Makes needed changes to JSON and sets it to input.
         * If given row's rowId is null or undefined assumes new row, otherwise updates old row.
         * In case of modified row the row in original content is overwritten.
         * If original row is not found then row is assumed to be new, rowId is overwritten to null
         * and row is pushed to content.
         *
         * After modifications are done table is rebuilt.
         * @param row Row being saved to JSON hidden field
         */
        function saveContainerRow(row, context) {
            if (row.type !== 'row') {
                return;
            }
            // Fetch previous data from hidden field
            var content = readContent(row.key);
            if (content === null) {
                content = {};
                content.type = 'container';
                content.key = row.key;
            }
            if (content.key !== row.key) {
                // Sanity check that row is being saved to correct container
                return;
            }
            row.change = true;
            // Set row to changed so it's easy to find while saving user input on server
            // Make modification
            var found = false;
            if (typeof content.rows === 'undefined') {
                content.rows = [];
            }
            if (row.handlerId !== null && typeof row.handlerId !== 'undefined') {
                // Check for old row
                content.rows.some(function (contentRow, i) {
                    if (contentRow.handlerId === row.handlerId) {
                        found = true;
                        content.rows[i] = row;
                        // Just replace old row with new row. All info should be there already
                        return true;
                    }
                });
            }
            if (!found) {
                // Either a new row or old row was not found.
                row.rowId = null;
                // Make sure that rowId is null when adding a new row.
                content.rows.push(row);
            }
            if (typeof content !== 'undefined' && content !== null) {
                // Put modified data back to hidden field
                saveContent(content);
                // Rebuild table
                buildTable(content.key, context);
            }
        }

        /**
         * Save a given REFERENCECONTAINER reference to right hidden input.
         * Makes needed changes to JSON and sets it to input.
         * If given reference's rowId is null or undefined assumes new reference, otherwise updates old reference.
         * In case of modified reference the reference in original content is overwritten.
         * If original reference is not found then reference is assumed to be new, rowId is overwritten to null
         * and row is pushed to content.
         *
         * After modifications are done table is rebuilt.
         * @param reference Reference being saved to JSON hidden field
         */
        function saveReferenceRow(reference, context) {
            if (reference.type !== 'reference') {
                return;
            }
            // Fetch previous data from hidden field
            var content = readContent(reference.key);
            if (content === null) {
                content = {};
                content.type = 'referencecontainer';
                content.key = reference.key;
            }
            if (content.key !== reference.key) {
                // Sanity check that reference is being saved to correct container
                return;
            }
            reference.change = true;
            // Set reference to changed so it's easy to find while saving user input on server
            // Make modification
            var found = false;
            if (typeof content.references === 'undefined') {
                content.references = [];
            }
            if (reference.handlerId !== null && typeof reference.handlerId !== 'undefined') {
                // Check for old reference
                content.references.some(function (contentReferences, i) {
                    if (contentReferences.handlerId === reference.handlerId) {
                        found = true;
                        content.references[i] = reference;
                        // Just replace old reference with new reference. All info should be there already
                        return true;
                    }
                });
            }
            if (!found) {
                // Either a new reference or old reference was not found.
                reference.rowId = null;
                // Make sure that rowId is null when adding a new reference.
                content.references.push(reference);
            }
            if (typeof content !== 'undefined' && content !== null) {
                // Put modified data back to hidden field
                saveContent(content);
                // Rebuild table
                buildTable(content.key, context);
            }
        }

        function handleReferenceOptions(data, context) {
            var tbody = $('#' + data.key + ' tbody');
            var currentDependencyValue = null;
            var currentRow = null;
            var field = MetkaJS.JSConfigUtil.getField(data.key);
            var reference = MetkaJS.JSConfigUtil.getReference(field.reference);
            var target = reference.target;
            field = null;
            reference = null;
            data.responses.forEach(function (response) {
                if (response.options === null || response.options.length === 0) {
                    // No text to insert
                    return;
                }
                if (currentRow === null || currentDependencyValue !== response.dependencyValue) {
                    currentRow = MetkaJS.getElementWithDataValue(tbody, 'tr', 'reference', response.dependencyValue);
                }
                if (currentRow === null) {
                    // No row, continue.
                    return;
                }
                field = MetkaJS.JSConfigUtil.getField(response.key);
                reference = MetkaJS.JSConfigUtil.getReference(field.reference);
                var td = MetkaJS.getElementWithDataValue(currentRow, 'td', 'key', response.key);
                if (td !== null) {
                    td.empty();
                    var option = response.options[0];
                    if (option.title.type === MetkaJS.E.RefTitle.LITERAL) {
                        td.append(option.title.value);
                    } else if (option.title.type === MetkaJS.E.RefTitle.VALUE) {
                        var targetField = MetkaJS.JSConfigUtil.getField(reference.titlePath, target);
                        if (targetField === null || targetField.type !== MetkaJS.E.Field.SELECTION) {
                            td.append(option.title.value);
                        } else {
                            td.append(MetkaJS.L10N.get(target + '.' + targetField.selectionList + '.option.' + option.title.value));
                        }
                    }
                }
            });
        }

        return {
            readContent: readContent,
            saveContent: saveContent,
            build: buildTable,
            saveRow: saveRow,
            handleReferenceOptions: handleReferenceOptions
        };
    }());
}());