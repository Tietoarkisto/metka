define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (options, key) {
        var listKey = options.fieldOptions.selectionList;
        if (!listKey) {
            return;
        }
        return (function rec(listKey) {
            var list = getPropertyNS(options, 'dataConf.selectionLists', listKey);

            if (!list) {
                log('list not found', listKey, options);
                return;
            }
            if (list.type === 'SUBLIST') {
                return rec(list.sublistKey || list.key);
            }

            return list;
        })(listKey);
    };
});
