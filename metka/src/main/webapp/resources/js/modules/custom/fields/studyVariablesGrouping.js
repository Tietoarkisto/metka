define(function (require) {
    'use strict';

    //return {};
    return {
        create: function create(options) {
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

            require('./../../data')(options).onChange(function () {
                $variables.empty();
                var rows = (function () {
                    return require('./../../data')(options)(key).getByLang(options.defaultLang);
                })();
                if (rows) {
                    require('./../../server')('options', {
                        data: JSON.stringify({
                            key: key,
                            requests: rows.map(function (transferRow) {
                                return {
                                    key: column,
                                    container: key,
                                    confType: options.dataConf.key.type,
                                    confVersion: options.dataConf.key.version,
                                    dependencyValue: transferRow.value
                                }
                            })
                        }),
                        success: function (data) {
                            // TODO: listaa ainoastaan muuttujat, jotka eivät ole ryhmissä
                            var variables = data.responses.map(function (response) {
                                return {
                                    text: response.options[0].title.value.default,
                                    value: response.dependencyValue
                                };
                            });

                            $variableView = require('./../../treeView')(variables, {
                                onClick: function () {
                                    return 'toggle';
                                },
                                onChange: function (activeItems) {
                                    transferFromVariables = !!activeItems.length;
                                    setButtonStates();
                                }
                            });

                            $variables.append($variableView
                                .addClass('grouping-container'));
                        }
                    });
                }
            });

            var transferFromVariables = false;
            var transferToVariables = true;
            var transferFromGroups = false;
            var transferToGroups = false;

            require('./../../data')(options).onChange(function () {
                $groups.empty();
                var rows = (function () {
                    return require('./../../data')(options)('vargroups').getByLang(options.defaultLang);
                })() || [];

                rows = rows.filter(function (row) {
                    // TODO: set as removed
                    //row.removed = true;
                    return row.fields && row.fields.vargrouptitle;
                });

                $groupView = require('./../../treeView')(rows.map(function (transferRow) {
                    return {
                        text: transferRow.fields.vargrouptitle.values.DEFAULT.current,
                        children: transferRow.fields.vargroupvars ? transferRow.fields.vargroupvars.rows.DEFAULT.map(function (transferRow) {
                            return {
                                text: transferRow.value
                            };
                        }) : [],
                        appendVar: function (transferRow2) {
                            if (!transferRow.fields.vargroupvars) {
                                transferRow.fields.vargroupvars = {
                                    key: 'vargroupvars',
                                    rows: {
                                        DEFAULT: []
                                    },
                                    type: 'REFERENCECONTAINER'
                                };
                            }
                            log('appended', transferRow)
                            transferRow.fields.vargroupvars.rows.DEFAULT.push(transferRow2);
                        }
                    };
                }), {
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
                        nodes.forEach(function (node) {
                            parent.appendVar({
                                key: 'vargroupvars',
                                value: node.value
                            });
                        });
                    },
                    onDragged: function (nodes) {
                        log('drag', nodes)
                        return
                        nodes.forEach(function (node) {
                            node.transferRow.removed = true;
                        });
                    }
                });

                $groups.append($groupView
                    .addClass('grouping-container'));
            });

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
                    .append($groups))
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
                                        var containerOptions = {
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
                                        };
                                        require('./../../modal')({
                                            title: 'Lisää ryhmä',
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

                                                            $groupView.data('add')([{
                                                                text: title,
                                                                children: [],
                                                                active: true,
                                                                vars: transferRow.fields.vargroupvars
                                                            }]);
                                                            transferToGroups = true;
                                                            setButtonStates();
                                                        });
                                                }
                                            }, {
                                                type: 'CANCEL'
                                            }]
                                        });
                                    });
                            }
                        }))));

            setButtonStates();
        }
    };
});
