define(function (require) {
    'use strict';

    //return {};
    return {
        create: function create(options) {
            this.append(require('./../../treeView')([{
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
                children: [{
                    text: 'g'
                }, {
                    text: 'h'
                }, {
                    text: 'i'
                }]
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
                onClick: function () {
                    log('todo: show data', 'id???');
                    return 'activateOne';
                }
            }));
        }
    };
});
