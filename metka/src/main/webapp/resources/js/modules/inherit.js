define(function (require) {
    'use strict';

    return function (childConstructor) {
        return function (parentOptions) {
            parentOptions = parentOptions || {};
            return function (options) {
                options = options || {};
                options.parent = parentOptions;

                // use parent's values, if nothing else is available
                options.$events = options.$events || parentOptions.$events;
                options.data = options.data || parentOptions.data;
                options.dataConf = options.dataConf || parentOptions.dataConf;
                options.defaultLang = options.defaultLang || parentOptions.defaultLang;
                options.fieldTitles = parentOptions.fieldTitles;

                return childConstructor.call(this, options);
            };
        };
    };
});
