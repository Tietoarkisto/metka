define(function (require) {
    'use strict';

    return {
        get: function (options, key) {
            var data = require('./utils/getPropertyNS')(options, 'data.fields', key);


            var modified = require('./utils/getPropertyNS')(data, 'value.current');
            if (MetkaJS.exists(modified)) {
                return modified;
            }

            return require('./utils/getPropertyNS')(data, 'value.original');
        },
        set: function (options, key, value) {
            return require('./utils/setPropertyNS')(options, 'data.fields', key, 'value.current', value);
        }
    };
});
