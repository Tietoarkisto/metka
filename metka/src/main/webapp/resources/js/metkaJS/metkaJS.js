(function () {
	'use strict';

    // TODO: move to separate file
    // Polyfills and language extensions

    // shorthand for console.log
    window.log = console.log.bind(console);

    if (!String.prototype.supplant) {
        String.prototype.supplant = function (o) {
            return this.replace(
                /\{([^{}]*)\}/g,
                function (a, b) {
                    var r = o[b];
                    return typeof r === 'string' || typeof r === 'number' ? r : a;
                }
            );
        };
    }

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
            MetkaJS.assignUrl('view', {
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

        url: function (key, extend) {
            return MetkaJS.Globals.contextPath + '/' + {
                approve: '{page}/approve',
                compareRevisions: 'history/revisions/compare',
                download: 'download/{id}/{revision}',
                edit: '{page}/edit/{id}',
                fileEdit: 'file/save/{value}',
                fileSave: 'file/save',
                fileUpload: 'file/upload',
                listRevisions: 'history/revisions/{id}',
                next: 'next/{page}/{id}',
                options: 'references/collectOptionsGroup',
                prev: 'prev/{page}/{id}',
                remove: 'remove/{page}/{type}/{id}',
                save: '{page}/save',
                seriesAdd: 'series/add',
                view: '{page}/view/{id}/{revision}'
            }[key].supplant($.extend({
                id: MetkaJS.SingleObject.id,
                page: MetkaJS.Globals.page,
                revision: MetkaJS.SingleObject.revision
            }, extend));
        },

        // same as .url method, except also navigates to the url
        assignUrl: function () {
            location.assign(MetkaJS.url.apply(this, arguments));
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

        Data: {
            get: function (key) {
                //console.log('get data:', key);
                var data = MetkaJS.objectGetPropertyNS(MetkaJS, 'data.fields', key);

                var current = MetkaJS.objectGetPropertyNS(data, 'currentValue');
                if (typeof current !== 'undefined') {
                    if (current === null) {
                        return;
                    }
                    return current;
                }

                var modified = MetkaJS.objectGetPropertyNS(data, 'modifiedValue.value.value');
                if (MetkaJS.exists(modified)) {
                    return modified;
                }

                return MetkaJS.objectGetPropertyNS(data, 'originalValue.value.value');
            },
            set: function (key, value) {
                return MetkaJS.objectSetPropertyNS(MetkaJS, 'data.fields', key, 'currentValue', value);
            }
        },

        /**
         * Checks whether given field should be rendered as a read only field.
         * There are multiple things that have to be considered for this but restrictions are implemented as needed.
         * TODO: Immutability causes problems since we have to have the original value from revision to determine that.
         * TODO: All in all immutability is handled wrong currently since it should depend on original value, not current value.
         *
         * @param field Field configuration used to check if read only is needed
         */
        isReadOnly: function (field) {
            // We are not viewing a revision, never use read only
            if (MetkaJS.SingleObject === null) {
                return false;
            }

            // We are in an approved revision, always use readonly
            if (MetkaJS.SingleObject.state !== 'DRAFT') {
                return true;
            }

            // Field should not be editable by user
            if (!field.editable) {
                return true;
            }

            // Field is a REFERENCE text field, should not be editable since value comes from reference.
            if (field.type === MetkaJS.E.Field.REFERENCE) {
                return true;
            }

            /*if(field.immutable === true && currentValue !== null) {
             return
             }*/
            // By default fields are not read only
            return false;
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
        },

        // TODO: move to Object.getPropertyNS or Object.prototype.getPropertyNS
        /**
         * @param o Get property from this object
         * @param [ns] Namespace. Can be string, '.' (dot) separated string, or array of strings
         */
        objectGetPropertyNS: function (o/*[, ns]*/) {
            var ns = $.makeArray(arguments);
            ns.shift();
            if (!ns.length) {
                return o;
            }
            ns = Array.prototype.concat.apply([], ns.map(function (v) {
                return typeof v === 'string' ? v.split('.') : v;
            }));
            return (function r(o) {
                if (typeof o !== 'object') {
                    return;
                }
                if (o === null) {
                    return;
                }

                var propName = ns.shift();
                var prop = o[propName];
                if (ns.length) {
                    return r(prop);
                } else {
                    return prop;
                }
            })(o);
        },

        /**
         * @param o Set property to this object
         * @param [ns] Can be string, '.' (dot) separated string, or array of strings
         * @param value any value
         * @returns value
         *
         * Example:
         * var o = {};
         * MetkaJS.objectSetPropertyNS(o, 'a.b.c', 'd', ['e', 'f'], 123);
         * JSON.stringify(o); // "{"a":{"b":{"c":{"d":{"e":{"f":123}}}}}}"
         */
        objectSetPropertyNS: function (o, ns/*[, ns]*/,  value) {
            var ns = $.makeArray(arguments);
            ns.shift(); // remove o
            value = ns.pop(); // value is last argument
            if (!ns.length) {
                throw 'Property name was not specified.';
            }
            if (!o) {
                throw 'Object was not specified.';
            }

            ns = Array.prototype.concat.apply([], ns.map(function (v) {
                return typeof v === 'string' ? v.split('.') : v;
            }));
            return (function r(o) {
                var propName = ns.shift();
                if (ns.length) {
                    var prop;
                    if (typeof o[propName] === 'undefined') {
                        prop = o[propName] = {};
                    } else {
                        if (typeof o[propName] !== 'object') {
                            throw 'Typeof property is not object.';
                        }
                        if (o[propName] === null) {
                            prop = o[propName] = {};
                            //throw 'Property is null.';
                        }
                        prop = o[propName];
                    }
                    return r(prop);
                } else {
                    return o[propName] = value;
                }
            })(o);
        }
    };
}());
