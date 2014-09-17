define(function (require) {
    'use strict';

    return function addTranslation(path, root) {
        for(var prop in root) {
            if(root.hasOwnProperty(prop)) {
                var curPath = path;
                if(curPath.length > 0) {
                    curPath += '.';
                }
                if(MetkaJS.isString(root[prop])) {
                    curPath += prop;
                    MetkaJS.L10N.put(curPath, root[prop]);
                } else if(prop.charAt(0) === '&') {
                    curPath += prop.slice(1);
                    MetkaJS.L10N.put(curPath, root[prop]);
                } else {
                    curPath += prop;
                    addTranslation(curPath, root[prop]);
                }
            }
        }
    };
});