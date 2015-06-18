/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

(function() {
    'use strict';

    /**
     * Localisation service for Metka client.
     */
    MetkaJS.L10N = (function() {
        var strings = {};
        var locale = "default";

        return {
            locale: locale,
            strings: strings,
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
             * Checks to see if given object contains text (either as string or translation object) in property with given name.
             * @param obj
             * @param name
             */
            containsText: function(obj, name) {
                if(!obj) {
                    return false;
                }
                return obj[name] && MetkaJS.isString(obj[name]) || MetkaJS.L10N.hasTranslation(obj, name);
            },
            /**
             * Checks to see if given object has a translation text object for given parameter name.
             * Object has to have a property with &-version of the property name and that property has to contain
             * non-empty string property with name default.
             *
             * @param obj Object to be checked for translation property
             * @param name Name of the property being checked, should not contain & as the first letter
             * @returns {boolean} True if there is a translation version of given property
             */
            hasTranslation: function(obj, name) {
                if (!obj) {
                    return false;
                }
                return MetkaJS.L10N.isTranslation(obj['&'+name]);
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

                return MetkaJS.isString(obj.default);
            }
        }
    })();
}());