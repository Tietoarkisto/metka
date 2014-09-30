(function () {
	'use strict';

    /* Define MetkaJS namespace. Includes general global variables, objects, handlers and functions related to Metka-client.
     */
    window.MetkaJS = {
        User: {
            userName: null,
            displayName: null,
            role: null
        },
        L10N: null,
        // Globals-object contains global variables and sequences

        /**
         * Checks the existence of given variable.
         * Used to lessen repetitive coding and unifies checking.
         *
         * @param variable Variable to be checked for existence
         * @return {boolean} True if variable exists and false if not
         */
        exists: function (variable) {
            if (variable === null) {
                return false;
            }
            if (typeof variable === 'undefined') {
                return false;
            }

            // If all checks pass return true
            return true;
        },

        /**
         * Checks if given variable is a non-empty string.
         * Uses MetkaJS.exists() function as part of its implementation.
         *
         * @param variable Variable to be checked
         * @returns {boolean} True if given variable is a non-empty string, false otherwise
         */
        isString: function(variable) {
            if(!MetkaJS.exists(variable)) {
                return false;
            }

            if(typeof variable !== 'string') {
                return false;
            }

            if(variable.length <= 0) {
                return false;
            }

            return true;
        },

        /**
         * Checks if given variable exists and is a number
         * @param variable Variable to be checked
         * @returns {boolean} True if given variable exists and is a number, false otherwise
         */
        isNumber: function(variable) {
            if(!MetkaJS.exists(variable)) {
                return false;
            }

            if(typeof variable !== 'number') {
                return false;
            }

            return true;
        },

        // Checks to see if given variable is an array and has any content
        hasContent: function(variable) {
            if(!$.isArray(variable)) {
                return false;
            }

            if(variable.length <= 0) {
                return false;
            }

            return true;
        }
    };
})();
