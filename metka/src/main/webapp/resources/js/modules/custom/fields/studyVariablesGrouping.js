define(function (require) {
    'use strict';

    return {};
    return {
        create: function create(options) {
            function setButtonStates() {
                $moveToGroup.prop('disabled', !transferFromVariables || !transferToGroups);
                $moveToVariables.prop('disabled', !transferFromGroups || !transferToVariables);
            }

            var transferFromVariables = false;
            var transferToVariables = true;
            var transferFromGroups = false;
            var transferToGroups = false;
            var $variableView = require('./../../treeView')([{
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }, {
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }], {
                onClick: function () {
                    return 'toggle';
                },
                onChange: function (activeItems) {
                    transferFromVariables = !!activeItems.length;
                    setButtonStates();
                }
            });
            var $groupView = require('./../../treeView')([{
                text: 'a',
                children: [{
                    text: 'd'
                }, {
                    text: 'e'
                }, {
                    text: 'f'
                }]
            }, {
                text: 'b',
                children: []
            }, {
                text: 'c',
                children: [{
                    text: 'j'
                }, {
                    text: 'k'
                }, {
                    text: 'l'
                }]
            }], {
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
                }
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
                    .append($('<div class="col-xs-5 well well-sm">')
                        .append($variableView
                            .addClass('grouping-container')))
                    .append($('<div class="col-xs-2 text-center">')
                        .css({
                            'padding-top': '196px'
                        })
                        .append($('<div class="btn-group-vertical">')
                            .append($moveToGroup)
                            .append($moveToVariables)))
                    .append($('<div class="col-xs-5 well well-sm">')
                        .append($groupView
                            .addClass('grouping-container'))));

            setButtonStates();
        }
    };
});
