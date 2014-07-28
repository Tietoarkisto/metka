define(function(require) {
    'use strict';

    MetkaJS.RevisionHistory = {
        compare: function compareRevisions() {
            var beginVal = $('input[name="beginGrp"]:checked').val();
            var endVal = $('input[name="endGrp"]:checked').val();
            if (typeof beginVal === 'undefined' || typeof endVal === 'undefined') {
                return;
            }

            $.ajax({
                type: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                dataType: 'json',
                url: require('./url')('compareRevisions'),
                data: JSON.stringify({
                    id: MetkaJS.SingleObject.id,
                    begin: parseInt(beginVal, 10),
                    end: parseInt(endVal, 10),
                    type: MetkaJS.Globals.page.toUpperCase()
                }),
                success: function (response) {
                    // Fill compare dialog

                    var changesTable = $('#revisionChangesTable');
                    var titleRow = changesTable.children().first();
                    changesTable.empty();
                    changesTable.append(titleRow);

                    var tbody = $('<tbody>');
                    var i;
                    for (i = 0; i < response.changes.length; i++) {
                        var rowData = response.changes[i];
                        var row = $('<tr>', {'class': 'revisionHistoryDialogRow'});
                        var prop = '';
                        if (rowData.section) {
                            prop += MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase() + '.section.' + rowData.section);
                            prop += ': ';
                        }
                        prop += MetkaJS.L10N.get(MetkaJS.Globals.page.toUpperCase() + '.field.' + rowData.property);
                        row.append($('<td>', {'class': 'revisionTableColumn', text: prop}));

                        // TODO: server should only send strings suitable for display and nothing more.
                        if (rowData.type !== 'CONTAINER') {
                            if (rowData.oldValue.length > 0) {
                                row.append($('<td>', {'class': 'revisionTableColumn', text: rowData.oldValue[0]}));
                            }
                            if (rowData.newValue.length > 0) {
                                row.append($('<td>', {'class': 'revisionTableColumn', text: rowData.newValue[0]}));
                            }
                        }

                        tbody.append(row);
                    }

                    changesTable.append(tbody);

                    // close version dialog
                    $('#revisionHistoryDialog').dialog('close');

                    // show compare dialog
                    var str = MetkaJS.L10N.get('general.revision.compare.title');
                    str = str.replace('{0}', response.begin);
                    str = str.replace('{1}', response.end);

                    $('#revisionCompareDialog').dialog('option', 'title', str);
                    $('#revisionCompareDialog').dialog('open');
                },
                error: function (e) {
                    alert('Error: ' + JSON.stringify(e, null, 4));
                }
            });
        }
    };
}());