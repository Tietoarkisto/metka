define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            create: function (options) {
                var $elem = this;

                var curVal = require('./../../data')(options)("otherauthortype").getByLang("DEFAULT");
                $elem.toggle(curVal && curVal === '3');

                options.$events.on('data-changed-{key}-{lang}'.supplant({
                                key: "otherauthortype",
                                lang: "DEFAULT"
                            }), function(e, value) {
                    $elem.toggle(value && value === '3');
                });
            }
        }
    };
});
