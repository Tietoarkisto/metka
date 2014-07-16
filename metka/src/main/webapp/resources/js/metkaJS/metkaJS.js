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
            draft: false,
            /**
             * Moves browser to editing current revisionable.
             */
            edit: function () {
                MetkaJS.PathBuilder().
                    add(MetkaJS.Globals.page).
                    add('edit').
                    add(MetkaJS.SingleObject.id).
                    navigate();
            },
            /**
             * Moves browser to adjacent revisionable.
             * @param next If true moves to next revisionable, if false moves to previous. Default is true.
             */
            adjacent: function (next) {
                if (next === null) {
                    next = true;
                }
                MetkaJS.PathBuilder()
                    .add(next ? 'next' : 'prev')
                    .add(MetkaJS.Globals.page)
                    .add(MetkaJS.SingleObject.id)
                    .navigate();
            },
            /**
             * Submits revision modification form to given action
             * @param action MetkaJS.E.Form enumeration value
             */
            formAction: function (action) {
                $('#revisionModifyForm').attr('action', MetkaJS.PathBuilder().add(MetkaJS.Globals.page).add(action).build());
                $('#revisionModifyForm').submit();
            }
        },

        // Shorthand function for viewing certain revision of certain revisionable. Forms the correct URL and navigates straight to it.
        view: function (id, revision) {
            MetkaJS.PathBuilder()
                .add(MetkaJS.Globals.page)
                .add('view')
                .add(id)
                .add(revision)
                .navigate();
        },

        /**
         * General close function for dialogs.
         * Closes the dialog with provided id.
         * @param id - Id of the dialog
         */
        dialogClose: function (id) {
            $('#' + id).dialog('close');
        },

        // Provides a new PathBuilder instance every time this is called. PathBuilder takes care of certain repeating elements in service URLs and can navigate straight to the built URL.
        PathBuilder: function () {
            var path = MetkaJS.Globals.contextPath;
            return {
                add: function (part) {
                    if (MetkaJS.isString(part) || MetkaJS.isNumber(part)) {
                        path += '/' + part;
                    }
                    return this;
                },
                build: function () {
                    return path;
                },
                navigate: function () {
                    location.href = path;
                }
            };
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
                var data = MetkaJS.objectGetPropertyFromNS(MetkaJS, 'data.fields', key);

                var current = MetkaJS.objectGetPropertyFromNS(data, 'currentValue');
                if (current !== null && typeof current !== 'undefined') {
                    return current;
                }

                var modified = MetkaJS.objectGetPropertyFromNS(data, 'modifiedValue.value.value');
                if (modified !== null && typeof modified !== 'undefined') {
                    return modified;
                }

                return MetkaJS.objectGetPropertyFromNS(data, 'originalValue.value.value');
            },
            set: function (key, value) {
                return MetkaJS.objectSetPropertyFromNS(MetkaJS, 'data.fields', key, 'currentValue', value);
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
            if (!MetkaJS.SingleObject.draft) {
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
        exists: function(variable) {
            if(variable === null) {
                return false;
            }
            if(typeof variable === 'undefined') {
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

        // TODO: move to Object.getPropertyFromNS or Object.prototype.getPropertyFromNS
        /**
         * @param o Get property from this object
         * @param [ns] Namespace. Can be string, '.' (dot) separated string, or array of strings
         */
        objectGetPropertyFromNS: function (o/*[, ns]*/) {
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
         */
        objectSetPropertyFromNS: function (o/*[, ns]*/,  value) {
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
        },
        expertSearch: function() {
            var query = $("#expertSearchQuery").val();
            var request = {
                operation: "QUERY",
                data: query
            }
            $.ajax({
                method: 'POST',
                url: MetkaJS.PathBuilder().add("expertSearch").add("query").build(),
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify(request),
                dataType: 'json'
            }).done(function(data) {
                alert("Expert search complete");
            });
        }
    };
}());
