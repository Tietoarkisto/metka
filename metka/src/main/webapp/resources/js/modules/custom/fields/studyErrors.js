define(function (require) {
    'use strict';

    function refreshData(options, $field) {
        require('./../../server')('/study/listErrors/{id}', {
            method: 'GET',
            success: function (response) {
                var objectToTransferRow = require('./../../map/object/transferRow');
                $field.find("tbody").empty();

                response.errors.map(function (result) {
                    result.errorscore = result.errorscore + '';
                    var transferRow = objectToTransferRow(result, options.defaultLang);
                    transferRow.saved = {
                        time: result.savedAt,
                        user: result.savedBy
                    };
                    $field.data("addRow")(transferRow);
                });
            }
        });
    }
    return function(options) {
        delete options.field.displayType;

        var ret = {
            dataConf: {
                fields: {
                    studyErrors: {
                        type: "CONTAINER",
                        //fixedOrder: true,
                        removePermissions: [
                            "canRemoveStudyErrors"
                        ],
                        subfields: [
                            'id',
                            'errorscore',
                            'errordatasetpart',
                            'errorpartsection',
                            'errorlanguage',
                            'errorlabel',
                            'errornotes',
                            'errortriggerdate',
                            'errortriggerpro'
                        ]
                    },
                    id: {
                        type: "INTEGER",
                        editable: false
                    },
                    errorscore: {
                        key: 'errorscore',
                        type: 'SELECTION',
                        selectionList: 'errorscore_list'
                    },
                    errordatasetpart: {
                        key: 'errordatasetpart',
                        type: 'SELECTION',
                        selectionList: 'errordatasetpart_list'
                    },
                    errorpartsection: {
                        key: 'errorpartsection',
                        type: 'SELECTION',
                        selectionList: 'errorpartsection_list'

                    },
                    errorlanguage: {
                        key: 'errorlanguage',
                        type: 'SELECTION',
                        selectionList: 'errorlanguage_list'
                    },
                    errorlabel: {
                        key: 'errorlabel',
                        type: 'STRING'
                    },
                    errornotes: {
                        key: 'errornotes',
                        type: 'STRING',
                        "multiline": true
                    },
                    errortriggerdate: {
                        key: 'errortriggerdate',
                        type: 'DATE'
                    },
                    errortriggerpro: {
                        // TODO: This should be changed to selection, but only after USER reference type has been implemented
                        key: 'errortriggerpro',
                        type: 'STRING'
                    }
                },
                "selectionLists": {
                    "errorscore_list": {
                        "key": "errorscore_list",
                        "type": "LITERAL",
                        "options": [
                            {
                                "value": "1"
                            },
                            {
                                "value": "2"
                            },
                            {
                                "value": "3"
                            },
                            {
                                "value": "4"
                            },
                            {
                                "value": "5"
                            }
                        ]
                    },
                    "errordatasetpart_list": {
                        "key": "errordatasetpart_list",
                        "type": "REFERENCE",
                        "reference": "errordatasetpart_ref"
                    },
                    "errorpartsection_list": {
                        "key": "errorpartsection_list",
                        "type": "REFERENCE",
                        "reference": "errorpartsection_ref"
                    },
                    "errorlanguage_list": {
                        "key": "errorlanguage_list",
                        "type": "REFERENCE",
                        "reference": "errorlanguage_ref"
                    }
                },
                "references": {
                    "errorlanguage_ref": {
                        "key": "errorlanguage_ref",
                        "type": "JSON",
                        "target": "errorlanguage",
                        "valuePath": "value",
                        "titlePath": "title"
                    },
                    "errordatasetpart_ref": {
                        "key": "errordatasetpart_ref",
                        "type": "JSON",
                        "target": "errordatasetpart",
                        "valuePath": "value",
                        "titlePath": "title"
                    },
                    "errorpartsection_ref": {
                        "key": "errorpartsection_ref",
                        "type": "JSON",
                        "target": "errorpartsection",
                        "valuePath": "value",
                        "titlePath": "title"
                    }
                }
            },
            field: {
                displayType: 'CONTAINER',
                columnFields: [
                    'errorscore',
                    'errordatasetpart',
                    'errorpartsection',
                    'errorlanguage',
                    'errorlabel',
                    'errortriggerdate',
                    'errortriggerpro'
                ],
                "dialogTitle": {
                    "key": "studyErrors",
                    "ADD": "Lisää aineistovirhe",
                    "MODIFY": "Muokkaa aineistovirhettä",
                    "VIEW": "Aineistovirhe"
                },
                showSaveInfo: false,
                onRowChange: function (options, $tr, transferRow) {
                    log("transferRow", transferRow);
                    var data = require('./../../map/transferRow/object')(transferRow, options.defaultLang);
                    data.studyId = MetkaJS.revisionId;
                    if(data.errortriggerdate) {
                        var date = moment(data.errortriggerdate);
                        data.errortriggerdate = date.isValid() ? date.format('YYYY-MM-DD') : null;
                    } else {
                        data.errortriggerdate = null;
                    }
                    delete data.savedAt;
                    delete data.savedBy;
                    log('data', data);
                    require('./../../server')('/study/updateError/', {
                        data: JSON.stringify(data),
                        success: function (response) {
                            refreshData(options, $tr.closest(".panel").parent());
                        }
                    });
                },
                onRemove: function ($tr) {
                    $tr.find('button').prop('disabled', true);
                    require('./../../server')('/study/removeError/{id}', {
                        id: $tr.data('transferRow').fields.id.values.DEFAULT.current
                    }, {
                        method: 'GET',
                        success: function () {
                            $tr.remove();
                        }
                    });
                }
            },
            extraDialogConfiguration: {
                "id": {
                    "hidden": true
                }
            },
            create: function create(options) {
                refreshData(options, this.children().first());
            }
        };


        options.isReadOnly = function() {
            return !require("../../hasEveryPermission")(["canAddStudyErrors"])
        };
        options.fieldOptions = ret.dataConf.fields.studyErrors;

        return ret;
    };
});
