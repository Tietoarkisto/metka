(function() {
    'use strict';

    /**
     * Localisation service for Metka client.
     */
    MetkaJS.L10N = (function() {
        var strings = {};
        var locale = "fi";

        return {
            /**
             * Insert a localisation to the pool.
             * @param key Localisation key
             * @param value Actual localised text or localisation object
             */
            put: function (key, value) {
                // Sanity check
                if(!MetkaJS.exists(key) || !MetkaJS.exists(value)) {
                    return;
                }
                var toKey = '&'+key;
                delete strings[key];
                delete strings[toKey];

                if(MetkaJS.isString(value)) {
                    strings[key] = value;
                    return;
                }

                if(typeof value === 'object') {
                    if(MetkaJS.isString(value['default'])) {
                        strings[toKey] = value;
                        return;
                    }
                }
            },
            /**
             * Get localised text from the pool.
             * If no localisation is found for given key then the key is returned.
             *
             * @param key Key for localised text
             * @returns Localised text from pool, or key if no text found
             */
            get: function (key) {
                var loc = strings["&"+key];
                if(MetkaJS.exists(loc)) {
                    if(MetkaJS.isString(loc[locale])) {
                        return loc[locale];
                    } else {
                        return loc.default;
                    }
                }
                loc = strings[key];
                if(MetkaJS.isString(loc)) {
                    return loc;
                }

                return key;
            }
        }
    })();
}());