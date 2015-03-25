define(function (require) {
    'use strict';

    return function (childConstructor) {
        return function (parentOptions) {
            parentOptions = parentOptions || {};
            return function (options) {
                options = options || {};
                options.parent = parentOptions;
                options.isReadOnly = parentOptions.isReadOnly;

                // use parent's values, if nothing else is available
                options.$events = options.$events || parentOptions.$events;
                options.data = options.data || parentOptions.data;
                options.dataConf = options.dataConf || parentOptions.dataConf;
                options.defaultLang = options.defaultLang || parentOptions.defaultLang;
                options.fieldTitles = (parentOptions.fieldTitles || {});
                options.dialogTitles = (parentOptions.dialogTitles || {});
                options.ignoreTranslate = parentOptions.ignoreTranslate;

                return childConstructor.call(this, options);
            };
        };
    };
});
