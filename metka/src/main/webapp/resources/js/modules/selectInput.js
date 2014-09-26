define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options, key, callback) {
        var listKey = getPropertyNS(options, 'dataConf.fields', key, 'selectionList');
        if (!listKey) {
            callback();
            return;
        }
        (function rec(listKey) {
            var list = getPropertyNS(options, 'dataConf.selectionLists', listKey);

            if (!list) {
                log('list not found', listKey, options);
                callback();
                return;
            }
            if (list.type === 'SUBLIST') {
                rec(list.sublistKey || list.key);
                return;
            }

            callback(list);
            return;
        })(listKey);
    };
});
