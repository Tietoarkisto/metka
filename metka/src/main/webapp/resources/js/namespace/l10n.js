/**
 * Localisation service for Metka client.
 */
MetkaJS.L10N = (function() {
    var strings = new Array();
    return {
        /**
         * Insert a localisation to the pool.
         * @param key Localisation key
         * @param value Actual localised text
         */
        put: function(key, value) {
            strings[key] = value;
        },
        /**
         * Get localised text from the pool.
         * If no localisation is found for given key then the key is returned.
         *
         * @param key Key for localised text
         * @returns Localised text from pool, or key if no text found
         */
        get: function(key) {
            var loc = strings[key];
            if(loc == null || loc === 'undefined') {
                loc = key;
            }
            return loc;
        }
    }
})();