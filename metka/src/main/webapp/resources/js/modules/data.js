define(function (require) {
    'use strict';

    return {
        get: function (options, key) {
            var data = require('./utils/getPropertyNS')(options, 'data.fields', key);

            var current = require('./utils/getPropertyNS')(data, 'currentValue');
            if (typeof current !== 'undefined') {
                if (current === null) {
                    return;
                }
                return current;
            }

            var modified = require('./utils/getPropertyNS')(data, 'modifiedValue.value.value');
            if (MetkaJS.exists(modified)) {
                return modified;
            }

            return require('./utils/getPropertyNS')(data, 'originalValue.value.value');
        },
        set: function (options, key, value) {
            return require('./utils/setPropertyNS')(options, 'data.fields', key, 'currentValue', value);
        }
    };
});
