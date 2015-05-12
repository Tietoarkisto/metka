define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            preCreate: function (options) {
                var $elem = this;

                var curVal = require('./../../data')(options)("datakind").getByLang("DEFAULT");
                options.required = (curVal && curVal === '1');

                options.$events.on('data-changed-{key}-{lang}'.supplant({
                                key: "datakind",
                                lang: "DEFAULT"
                            }), function(e, value) {
                    options.required = (value && value === '1');
                    options.$events.trigger('label-update-{key}-{lang}'.supplant({
                        key: 'timemethods',
                        lang: 'DEFAULT'
                    }));
                });
            }
        }
    };
});
