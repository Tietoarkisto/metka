define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            preCreate: function (options) {
                var $elem = this;

                var curVal = require('./../../data')(options)("datakind").getByLang("DEFAULT");
                $elem.toggle(curVal && curVal === '1');
                options.required = (curVal && curVal === '1');

                options.$events.on('data-changed-{key}-{lang}'.supplant({
                                key: "datakind",
                                lang: "DEFAULT"
                            }), function(e, value) {
                    $elem.toggle(value && value === '1');
                    options.required = (value && value === '1');
                    options.$events.trigger('label-update-{key}-{lang}'.supplant({
                        key: 'weightyesno',
                        lang: 'DEFAULT'
                    }));
                });
            }
        }
    };
});
