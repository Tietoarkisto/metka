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

    return {
        postCreate: function(options) {
            function setButtonStates() {
                $moveToGroup.prop('disabled', !transferFromVariables || !transferToGroups);
                $moveToVariables.prop('disabled', !transferFromGroups || !transferToVariables);
                $moveVariableUp.prop('disabled', !arrangeInGroups || arrangeInVariables);
                $moveVariableDown.prop('disabled', !arrangeInGroups || arrangeInVariables);
            }

            var $pane = this.closest('.tab-pane');
            $pane.parent().parent().prev('.nav-tabs').find('a[data-target="#' + $pane.attr('id') + '"]')
                .on('hide.bs.tab', function () {
                    if(hasChanges) {
                        options.$events.trigger('variableChange');
                    }

                });

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
                                    // Listen for shift key press
                                    if(event.shiftKey) {
                                        // Select all nodes left between the 1st and the 2nd clicks
                                        if (startMultiselect === null) {
                                            startMultiselect = node;
                                        }
                                        if (startMultiselect !== null && endMultiselect === null && startMultiselect !== node) {
                                            endMultiselect = node;
                                        }
                                        if (endMultiselect !== null) {
                                            var startIndex = null;
                                            var endIndex = null;
                                            for (var i = 0; i < variables.length; i++) {
                                                if (variables[i] === startMultiselect) {
                                                    startIndex = i;
                                                }
                                                if (variables[i] === endMultiselect) {
                                                    endIndex = i;
                                                }
                                                // Handle a selection that goes from down to up
                                                if (endIndex !== null && endIndex < startIndex) {
                                                    var tmpIndex = startIndex;
                                                    startIndex = endIndex;
                                                    endIndex = tmpIndex;
                                                }
                                            }
                                            if(startIndex !== null && endIndex !== null){
                                                for(var i = startIndex; i <= endIndex; i++){
                                                    var isActive = {active: true};
                                                    $.extend(variables[i], isActive);
                                                }
                                            }
                                            startMultiselect = null;
                                            endMultiselect = null;
                                            return 'multiselect';
                                        }
                                        // Do a normal single select if no shift key pressed
                                    } else {
                                        startMultiselect = null;
                                        endMultiselect = null;
                                        return node.children ? 'activateOne' : 'deactivateDirectoriesAndToggle';
                                    }
                                },
                                onChange: function (activeItems) {
                                    if (activeItems.some(function (item) {
                                        return item.children;
                                    })) {
                                        transferFromGroups = false;
                                        transferToGroups = true;
                                        arrangeInGroups = false;
                                    } else {
                                        transferToGroups = transferFromGroups = arrangeInGroups = !!activeItems.length;
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

                            var startMultiselect = null;
                            var endMultiselect = null;

                            $variableView = require('./../../treeView')(variables, isFieldDisabled ? {} : {
                                onClick: function (node) {
                                    // Listen for shift key press
                                    if(event.shiftKey) {
                                        // Select all nodes left between the 1st and the 2nd clicks
                                        if (startMultiselect === null) {
                                            startMultiselect = node;
                                        }
                                        if (startMultiselect !== null && endMultiselect === null && startMultiselect !== node) {
                                            endMultiselect = node;
                                        }
                                        if (endMultiselect !== null) {
                                            var startIndex = null;
                                            var endIndex = null;
                                            for (var i = 0; i < variables.length; i++) {
                                                if(variables[i].active){
                                                   delete variables[i]['active'] ;
                                                }

                                                if (variables[i] === startMultiselect) {
                                                    startIndex = i;
                                                }
                                                if (variables[i] === endMultiselect) {
                                                    endIndex = i;
                                                }
                                                // Handle a selection that goes from down to up
                                                if (endIndex !== null && endIndex < startIndex) {
                                                    var tmpIndex = startIndex;
                                                    startIndex = endIndex;
                                                    endIndex = tmpIndex;
                                                }
                                            }
                                            if(startIndex !== null && endIndex !== null){
                                                for(var i = startIndex; i <= endIndex; i++){
                                                    var isActive = {active: true};
                                                    $.extend(variables[i], isActive);
                                                }
                                            }
                                            startMultiselect = null;
                                            endMultiselect = null;
                                            return 'multiselect';
                                        }
                                        // Do a normal single select if no shift key pressed
                                    } else {
                                        startMultiselect = null;
                                        endMultiselect = null;
                                        return 'toggle';
                                    }
                                },
                                onChange: function (activeItems) {
                                    transferFromVariables = arrangeInVariables = !!activeItems.length;
                                    setButtonStates();
                                },
                                refresh: function() {
                                    options.$events.trigger('refresh.metka');
                                },

                            });




                            $variables
                                .empty()
                                .append($variableView
                                    .addClass('grouping-container'));

                            transferFromVariables = false;
                            transferToVariables = true;
                            transferFromGroups = false;
                            transferToGroups = false;
                            arrangeInGroups = false;
                            arrangeInVariables = false;
                            setButtonStates();
                        }
                    });
                }
            }
            onDataChange();
            options.$events.on('variableChange', onDataChange);

            var transferFromVariables;
            var transferToVariables;
            var transferFromGroups;
            var transferToGroups;
            var arrangeInGroups;
            var arrangeInVariables;

            var $moveToGroup = require('./../../button')()({
                html: '<span class="glyphicon glyphicon-chevron-right"></span>',
                create: function () {
                    this
                        .click(function () {
                            $variableView.data('move')($groupView);
                        });
                }
            });
            var $moveToVariables = require('./../../button')()({
                html: '<span class="glyphicon glyphicon-chevron-left"></span>',
                create: function () {
                    this
                        .click(function () {
                            $groupView.data('move')($variableView);
                        });
                }
            });
            var $moveVariableUp = require('./../../button')()({
                html: '<span class="glyphicon glyphicon-chevron-up"></span>',
                create: function() {
                    this
                        .click(function () {
                            $groupView.data('moveDir')(-1);
                        });
                }
            });
            var $moveVariableDown = require('./../../button')()({
                html: '<span class="glyphicon glyphicon-chevron-down"></span>',
                create: function() {
                    this
                        .click(function () {
                            $groupView.data('moveDir')(1);
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
                            .append($moveToVariables)
                            .append($moveVariableUp)
                            .append($moveVariableDown)))
                    .append($groups));
            if (!isFieldDisabled) {
                this
                    .append($('<div class="row">')
                        .append($('<div class="col-xs-offset-7">')
                            .append(require('./../../button')()({
                            style: 'default',
                            "title": MetkaJS.L10N.get('general.buttons.addGroup'),
                            create: function () {
                                this
                                    .addClass('btn-sm')
                                    .click(function () {
                                        // TODO this is mostly same as saving expert search queries. group shared code together
                                        var containerOptions = $.extend(true, require('./../../optionsBase')(), {
                                            data: {},
                                            dataConf: {},
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
                                                                "title": MetkaJS.L10N.get("general.name"),
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
                                                    "default": MetkaJS.L10N.get("general.buttons.ok")
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
                                                            options.$events.trigger('refresh.metka');
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
