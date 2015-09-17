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

    return function (options) {
        var rowDialog = require('./../../containerRowDialog')(options, options.defaultLang, function () {
            return [{
                type: 'ROW',
                cells: [{
                    "type": "CELL",
                    "title": "Ryhmän nimi",
                    "field": {
                        "key": "vargrouptitle"
                    }
                }]
            }, {
                type: 'ROW',
                cells: [{
                    "type": "CELL",
                    "title": "Ryhmän tekstit",
                    "field": {
                        "key": "vargrouptexts",
                        "displayHeader": false,
                        "columnFields": [
                            "vargrouptext"
                        ],
                        "dialogTitle": {
                            "key": "vargrouptexts",
                            "ADD": "Lisää muuttujaryhmän teksti",
                            "MODIFY": "Muokkaa muuttujaryhmän tekstiä",
                            "VIEW": "Muuttujaryhmän teksti"
                        }
                    },
                    "subfieldConfiguration" : {
                        "vargrouptext": {
                            "field": {
                                "multiline": true
                            }
                        }
                    }
                }]
            }];
        });

        return {
            postCreate: function(options) {
                var key = 'variables';
                var column = 'varlabel';

                var $elem = this;
                var $pane = this.closest('.tab-pane');
                $pane.parent().parent().prev('.nav-tabs').find('a[data-target="#' + $pane.attr('id') + '"]')
                    .on('hide.bs.tab', function () {
                        if (hasChanges) {
                            options.$events.trigger('variableChange');
                        }
                    });

                var hasChanges;
                function onDataChange() {
                    hasChanges = false;
                    return (function onChange() {
                        require('./../../preloader')($elem);
                        var rows = require('./../../data')(options)(key).getByLang(options.defaultLang);
                        if (rows) {
                            require('./../../server')('options', {
                                data: JSON.stringify({
                                    key: key,
                                    requests: rows.map(function (transferRow) {
                                        var fieldValues = {};
                                        fieldValues[key] = transferRow.value;
                                        return {
                                            key: column,
                                            container: key,
                                            confType: options.dataConf.key.type,
                                            confVersion: options.dataConf.key.version,
                                            language: options.defaultLang,
                                            fieldValues: fieldValues
                                        }
                                    })
                                }),
                                success: function (data) {
                                    var variables = data.responses.map(function (response) {
                                        return {
                                            text: (response && response.options[0]) ? response.options[0].title.value : "",
                                            value: response.fieldValues.variables
                                        };
                                    });
                                    var treeViewEvents = {};
                                    if (!require('./../../isFieldDisabled')(options, options.defaultLang)) {
                                        treeViewEvents.onClick = function (node) {
                                            if (!node.children) {
                                                var requestOptions = {
                                                    id: node.transferRow.value.split("-")[0],
                                                    no: node.transferRow.value.split("-")[1]
                                                };
                                                require('./../../revisionModal')(options, requestOptions, 'STUDY_VARIABLE', onChange, options.field.key, true, 'Muokkaa muuttujaa');
                                            } else {
                                                rowDialog('modify', 'ok')(node.transferRow, function () {
                                                    hasChanges = true;
                                                    onChange();
                                                });
                                            }
                                        };
                                        treeViewEvents.refresh = function() {
                                            options.$events.trigger('refresh.metka');
                                        }
                                    }
                                    $elem.empty().append(require('./../../treeView')((require('./../../data')(options)('vargroups').getByLang(options.defaultLang) || []).filter(function (row) {
                                        return !row.removed && row.fields && row.fields.vargrouptitle;
                                    }).map(function (transferRow) {
                                        return require('./../../treeViewVariableGroup')(
                                            require('./../../data').latestValue(transferRow.fields.vargrouptitle, options.defaultLang),
                                            transferRow.fields.vargroupvars ? transferRow.fields.vargroupvars.rows.DEFAULT.map(function (transferRow) {
                                                var groupedVariable = variables.find(function (variable) {
                                                    return variable.value === transferRow.value;
                                                });
                                                if (!transferRow.removed) {
                                                    variables.splice(variables.indexOf(groupedVariable), 1);
                                                }
                                                return {
                                                    transferRow: transferRow,
                                                    groupedVariable: groupedVariable
                                                };
                                            }).filter(function (o) {
                                                if (!o.groupedVariable || o.transferRow.removed) {
                                                    o.transferRow.removed = true;
                                                    return false;
                                                } else {
                                                    return true;
                                                }
                                            }).map(function (o) {
                                                return {
                                                    text: o.groupedVariable.text,
                                                    transferRow: o.transferRow
                                                };
                                            }) : [],
                                            transferRow
                                        );

                                    }), treeViewEvents));
                                }
                            });
                        }
                    })();
                }
                onDataChange();
                options.$events.on('variableChange', onDataChange);
            }
        };
    };
});
