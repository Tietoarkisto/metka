define(function (require) {
    'use strict';

    return function (options) {
        return (require('./utils/getPropertyNS')(options, 'dataConf.fields', options.field.key, 'subfields') || []).some(function (field) {
            return options.dataConf.fields[field].translatable;
        });
    };
});
