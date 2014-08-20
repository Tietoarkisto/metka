define(function (require) {
    'use strict';

    return {
        create: function create(options) {
            this.append(require('./../../treeView')([{
                text: 'a'
            }, {
                text: 'b'
            }, {
                text: 'c'
            }], {
                onClick: function () {
                    log('todo: show data', 'id???');
                    return 'activateOne';
                }
            }));
        }
    };
});
