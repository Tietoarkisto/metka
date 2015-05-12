define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            preCreate: function (options) {
                var $elem = this;

                var curVal = require('./../../data')(options)("datakind").getByLang("DEFAULT");
                $elem.toggle(curVal && curVal === '2');

                options.$events.on('data-changed-{key}-{lang}'.supplant({
                                key: "datakind",
                                lang: "DEFAULT"
                            }), function(e, value) {
                    $elem.toggle(!!value && value === '2');
                });
            }
        }
    };
});
