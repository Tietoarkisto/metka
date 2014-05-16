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

                if(MetkaJS.isString(value)) {
                    strings[key] = value;
                    return;
                }

                if(typeof value === 'object') {
                    if(MetkaJS.isString(value['default'])) {
                        strings[key] = value;
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
                var loc = strings[key];
                if(!MetkaJS.exists(loc)) {
                    return key;
                }

                if(MetkaJS.isString(loc)) {
                    return loc;
                }

                if(MetkaJS.isString(loc[locale])) {
                    return loc[locale];
                } else {
                    return loc.default;
                }
            },

            localize: function(obj, name) {
                //Sanity checks
                if(!MetkaJS.exists(obj) || !MetkaJS.isString(name)) {
                    return '['+name+']';
                }

                if(MetkaJS.L10N.hasTranslation(obj, name)) {
                    var loc = obj['&'+name];
                    if(MetkaJS.isString(loc[locale])) {
                        return loc[locale];
                    } else {
                        return loc.default;
                    }
                } else {
                    if(MetkaJS.isString(obj[name])) {
                        return obj[name];
                    } else {
                        return '['+name+']';
                    }
                }
            },
            /**
             * Checks to see if given object has a translation text object for given parameter name.
             * Object has to have a property with &-version of the property name and that property has to contain
             * non-empty string property with name default.
             *
             * @param object Object to be checked for translation property
             * @param name Name of the property being checked, should not contain & as the first letter
             * @returns {boolean} True if there is a translation version of given property
             */
            hasTranslation: function(obj, name) {
                if(MetkaJS.L10N.isTranslation(obj['&'+name])) {
                    return true;
                } else {
                    return false;
                }
            },

            /**
             * Checks to see if given object is a translation object.
             * This is true if following checks are passed (not an absolute confirmation but good enough for our use):
             *   Obj exists i.e. is not null or undefined
             *   Obj is of type object
             *   Obj has existing non null string property named default
             *
             * @param obj Object to be checked
             * @returns {boolean} True if given object is a translation object, false otherwise
             */
            isTranslation: function(obj) {
                if(!MetkaJS.exists(obj)) {
                    return false;
                }

                if(typeof obj !== 'object') {
                    return false;
                }

                if(!MetkaJS.isString(obj.default)) {
                    return false;
                }

                return true;
            }
        }
    })();
}());