define(function (require) {
    'use strict';

    return function (options, lang) {
        if (options.field.type == 'REFERENCE') {
            return true;
        }

        var key = options.field.key;

        // if data should be immutable and original value is set, field is disabled
        if (require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'immutable') && typeof require('./utils/getPropertyNS')(options.data.fields, key, 'values', lang, 'original') === 'string') {
            return true;
        }

        var editable = require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'editable');
        return options.readOnly || options.field.readOnly || (typeof editable !== 'undefined' ? !editable : false);
    };
});
