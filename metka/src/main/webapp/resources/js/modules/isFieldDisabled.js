define(function (require) {
    'use strict';

    return function (options, lang) {
        if (options.field.type == 'REFERENCE') {
            return true;
        }

        var key = options.field.key;

        // if data should be immutable and original value is set, field is disabled
        if (options.fieldOptions && options.fieldOptions.immutable && typeof require('./utils/getPropertyNS')(options.data.fields, key, 'values', lang, 'original') === 'string') {
            return true;
        }

        var editable = options.fieldOptions ? options.fieldOptions.editable : true;
        return options.readOnly || (typeof editable !== 'undefined' ? !editable : false) || (function r(parent) {
            if (parent) {
                return parent.readOnly || r(parent.parent);
            }
            return false;
        })(options.parent);
    };
});
