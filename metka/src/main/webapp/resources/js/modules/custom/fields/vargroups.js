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
                    "extraDialogConfiguration" : {
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
                            onDataChange();
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
                                            text: response.options[0].title.value,
                                            value: response.fieldValues.variables
                                        };
                                    });
                                    var treeViewEvents = {};
                                    if (!require('./../../isFieldDisabled')(options, options.defaultLang)) {
                                        treeViewEvents.onClick = function (node) {
                                            if (!node.children) {
                                                require('./../../variableModal')(options.field.key, {
                                                    id: node.transferRow.value,
                                                    no: ''
                                                }, onChange);
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
                                        return row.fields && row.fields.vargrouptitle;
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
            }
        };
    };
});
