/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

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
                        fixedOrder: true,
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
                        type: 'SELECTION',
                        selectionList: "errortriggerpro_list"
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
                    },
                    "errortriggerpro_list": {
                        "key": "errortriggerpro_list",
                        "type": "REFERENCE",
                        "reference": "errortriggerpro_ref",
                        "includeEmpty": true
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
                    },
                    "errortriggerpro_ref": {
                        "key": "errortriggerpro_ref",
                        "type": "JSON",
                        "target": "user-list",
                        "valuePath": "userName",
                        "titlePath": "displayName"
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
            subfieldConfiguration: {
                "id": {
                    "hidden": true
                }
            },
            postCreate: function(options) {
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
