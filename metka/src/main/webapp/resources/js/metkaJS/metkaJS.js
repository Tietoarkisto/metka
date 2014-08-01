(function () {
	'use strict';

    /* Define MetkaJS namespace. Includes general global variables, objects, handlers and functions related to Metka-client.
     */
    window.MetkaJS = {
        DialogHandlers: {}, // This is used to collect and reference custom dialog handlers used throughout the application
        TableBuilders: {}, // This is used to collect and reference custom table builders used throughout the application
        // Placeholders for functionality added in other files
        E: null,
        JSConfig: null,
        JSConfigUtil: null,
        MessageManager: null,
        EventManager: null,
        L10N: null,
        RevisionHistory: null,
        // Globals-object contains global variables and sequences
        Globals: {
            page: '',
            contextPath: '',
            strings: [],
            globalId: (function () {
                var globalId = 0;
                return function () {
                    return globalId++;
                };
            }())
        },

        /**
         * Contains information of the revisionable object currently being viewed.
         * This is se to null if the object is not found in model.
         * Also provides shorthand functions for navigating related to the object.
         */
        SingleObject: {
            id: null,
            revision: null,
            state: null
        },

        // Shorthand function for viewing certain revision of certain revisionable. Forms the correct URL and navigates straight to it.
        view: function (id, revision) {
            require('./assignUrl')('view', {
                id: id,
                revision: revision
            });
        },

        /**
         * General close function for dialogs.
         * Closes the dialog with provided id.
         * @param id - Id of the dialog
         */
        dialogClose: function (id) {
            $('#' + id).dialog('close');
        },

        // Returns a jQuery wrapped page element for a given field key. Key is assumed to be for a top level input build by JSP and SpingForms but this is not checked.
        getValuesInput: function (key) {
            if (key !== null) {
                return $("#values\\'" + key + "\\'");
            }
            return null;
        },
        // Returns an id for top level field input build by JSP and SpringForms
        getValuesInputId: function (key) {
            if (key !== null) {
                return "values'" + key + "'";
            }
            return null;
        },
        // Returns a name for top level field input build by JSP and SpringForms
        getValuesInputName: function (key) {
            if (key !== null) {
                return "values['" + key + "']";
            }
            return null;
        },

        /**
         * Returns jQuery element with given value in given key found from given root element with provided selector.
         * Returns first element with given value so it multiple values match returns only one.
         *
         * @param root Element used for root in search
         * @param selector Selector string for descendant elements
         * @param key Data value key
         * @param value Value that should be matched.
         * @returns {null}
         */
        getElementWithDataValue: function (root, selector, key, value) {
            var elem = null;
            $(root).find(selector).each(function () {
                if ($(this).data(key) === value) {
                    elem = $(this);
                }
                return elem === null;
            });
            return elem;
        },

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
