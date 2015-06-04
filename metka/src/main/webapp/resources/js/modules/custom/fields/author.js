define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        function initField($elem, key) {
            var curVal = require('./../../data')(options)(key).getByLang("DEFAULT");
            $elem.toggle(!!curVal && curVal === '1');

            options.$events.on('data-changed-{key}-{lang}'.supplant({
                            key: key,
                            lang: "DEFAULT"
                        }), function(e, value) {
                $elem.toggle(!!value && value === '1');
            });
        }

        return {
            preCreate: function (options) {
                var $elem = this;
                // This is a bad way to do this but it works because custom fields are always run after other fields.
                if(options.data.fields.authortype) {
                    initField($elem, "authortype");
                } else if(options.data.fields.otherauthortype) {
                    initField($elem, "otherauthortype");
                }
            }
        }
    };
});
