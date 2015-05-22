define(function (require) {
    'use strict';

    return {
        postCreate: function(options) {
            function setButtonStates() {
                $moveToGroup.prop('disabled', !transferFromVariables || !transferToGroups);
                $moveToVariables.prop('disabled', !transferFromGroups || !transferToVariables);
            }

            var $variables = $('<div class="col-xs-5 well well-sm">');
            var $variableView;
            var $groups = $('<div class="col-xs-5 well well-sm">');
            var $groupView;

            var key = 'variables';
            var column = 'varlabel';
            var isFieldDisabled = require('./../../isFieldDisabled')(options, options.defaultLang);
            var hasChanges;

            function onDataChange() {
                require('./../../preloader')($variables);
                require('./../../preloader')($groups);
                hasChanges = false;
                var rows = require('./../../data')(options)(key).getByLang(options.defaultLang);
                if (rows) {
                    require('./../../server')('options', {
                        data: JSON.stringify({
                            key: key,
                            requests: rows.filter(function(transferRow) {
                                // User should not see removed rows while grouping
                                return !transferRow.removed;
                            }).map(function (transferRow) {
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

                            $groupView = require('./../../treeView')((require('./../../data')(options)('vargroups').getByLang(options.defaultLang) || []).filter(function (row) {
                                return !row.removed && row.fields && row.fields.vargrouptitle;
                            }).map(function (transferRow) {
                                return require('./../../treeViewVariableGroup')(
                                    require('./../../data').latestValue(transferRow.fields.vargrouptitle, options.defaultLang),
                                    transferRow.fields.vargroupvars ? transferRow.fields.vargroupvars.rows.DEFAULT.map(function (transferRow) {
                                        if(transferRow.removed) {
                                            return;
                                        }
                                        var groupedVariable = variables.find(function (variable) {
                                            return variable.value === transferRow.value;
                                        });
                                        variables.splice(variables.indexOf(groupedVariable), 1);
                                        return {
                                            transferRow: transferRow,
                                            groupedVariable: groupedVariable
                                        };
                                    }).filter(function (o) {
                                        return o && o.groupedVariable;
                                    }).map(function (o) {
                                        return {
                                            text: o.groupedVariable.text,
                                            transferRow: o.transferRow
                                        };
                                    }) : [],
                                    transferRow
                                );
                            }), isFieldDisabled ? {} : {
                                onClick: function (node) {
                                    return node.children ? 'activateOne' : 'deactivateDirectoriesAndToggle';
                                },
                                onChange: function (activeItems) {
                                    if (activeItems.some(function (item) {
                                        return item.children;
                                    })) {
                                        transferFromGroups = false;
                                        transferToGroups = true;
                                    } else {
                                        transferToGroups = transferFromGroups = !!activeItems.length;
                                    }
                                    setButtonStates();
                                },
                                onDropped: function (parent, nodes) {
                                    hasChanges = true;
                                    nodes.forEach(function (node) {
                                        var transferRow = $.extend(true, {}, node.transferRow, {
                                            key: 'vargroupvars',
                                            value: node.value,
                                            removed: false,
                                            rowId: null
                                        });
                                        node.transferRow = transferRow;
                                        parent.appendVar(transferRow);
                                    });
                                },
                                onDragged: function (nodes) {
                                    hasChanges = true;
                                    nodes.forEach(function (node) {
                                        node.transferRow.removed = true;
                                    });
                                },
                                refresh: function() {
                                    options.$events.trigger('refresh.metka');
                                }
                            });

                            $groups
                                .empty()
                                .append($groupView
                                    .addClass('grouping-container'));

                            $variableView = require('./../../treeView')(variables, isFieldDisabled ? {} : {
                                onClick: function () {
                                    return 'toggle';
                                },
                                onChange: function (activeItems) {
                                    transferFromVariables = !!activeItems.length;
                                    setButtonStates();
                                },
                                refresh: function() {
                                    options.$events.trigger('refresh.metka');
                                }
                            });

                            $variables
                                .empty()
                                .append($variableView
                                    .addClass('grouping-container'));

                            transferFromVariables = false;
                            transferToVariables = true;
                            transferFromGroups = false;
                            transferToGroups = false;
                            setButtonStates();
                        }
                    });
                }
            }
            onDataChange();

            var transferFromVariables;
            var transferToVariables;
            var transferFromGroups;
            var transferToGroups;

            var $moveToGroup = require('./../../button')()({
                create: function () {
                    this
                        .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                        .click(function () {
                            $variableView.data('move')($groupView);
                        });
                }
            });
            var $moveToVariables = require('./../../button')()({
                create: function () {
                    this
                        .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                        .click(function () {
                            $groupView.data('move')($variableView);
                        });
                }
            });
            this
                .append($('<div class="row">')
                    .append($variables)
                    .append($('<div class="col-xs-2 text-center">')
                        .css({
                            'padding-top': '196px'
                        })
                        .append($('<div class="btn-group-vertical">')
                            .append($moveToGroup)
                            .append($moveToVariables)))
                    .append($groups));
            var $pane = this.closest('.tab-pane');
            $pane.parent().parent().prev('.nav-tabs').find('a[data-target="#' + $pane.attr('id') + '"]')
                .on('hide.bs.tab', function () {
                    if (hasChanges) {
                        onDataChange();
                    }
                });
            if (!isFieldDisabled) {
                this
                    .append($('<div class="row">')
                        .append($('<div class="col-xs-offset-7">')
                            .append(require('./../../button')()({
                            style: 'default',
                            "&title": {
                                "default": "Lisää ryhmä"
                            },
                            create: function () {
                                this
                                    .addClass('btn-sm')
                                    .click(function () {
                                        // TODO this is mostly same as saving expert search queries. group shared code together
                                        var containerOptions = $.extend(true, require('./../../optionsBase')(), {
                                            data: {},
                                            dataConf: {},
                                            $events: $({}),
                                            defaultLang: options.defaultLang,
                                            content: [{
                                                type: 'COLUMN',
                                                columns: 1,
                                                rows: [
                                                    {
                                                        "type": "ROW",
                                                        "cells": [
                                                            {
                                                                "type": "CELL",
                                                                "title": "Nimi",
                                                                "colspan": 1,
                                                                "field": {
                                                                    "displayType": "STRING",
                                                                    "key": "title"
                                                                }
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }]
                                        });
                                        require('./../../modal')($.extend(true, require('./../../optionsBase')(), {
                                            //title: 'Lisää ryhmä',
                                            type: "ADD",
                                            dialogTitle: {
                                                ADD: "Lisää ryhmä"
                                            },
                                            body: require('./../../container').call($('<div>'), containerOptions),
                                            buttons: [{
                                                "&title": {
                                                    "default": 'OK'
                                                },
                                                create: function () {
                                                    this
                                                        .click(function () {
                                                            var title = require('./../../data')(containerOptions)('title').getByLang(options.defaultLang);

                                                            var transferRow = require('./../../map/object/transferRow')({
                                                                vargrouptitle: title
                                                            }, options.defaultLang);

                                                            transferRow.fields.vargroupvars = {
                                                                key: 'vargroupvars',
                                                                rows: {
                                                                    DEFAULT: []
                                                                },
                                                                type: 'REFERENCECONTAINER'
                                                            };

                                                            require('./../../data')(options)('vargroups').appendByLang(options.defaultLang, transferRow);

                                                            // always add to root
                                                            $groupView.data('deactivateAll')();

                                                            var group = require('./../../treeViewVariableGroup')(title, [], transferRow);
                                                            group.active = true;
                                                            $groupView.data('add')([group]);
                                                            transferToGroups = true;
                                                            hasChanges = true;
                                                            setButtonStates();
                                                        });
                                                }
                                            }, {
                                                type: 'CANCEL'
                                            }]
                                        }));
                                    });
                            }
                        }))));
            }
        }
    };
});
