/* Define MetkaJS namespace. Should include all global variables as well as all functions and other objects that
should be accessible from anywhere */
MetkaJS = {
    DialogHandlers: {}, // This is used to collect and reference custom dialog handlers used throughout the application
    TableBuilders: {}, // This is used to collect and reference custom table builders used throughout the application
    // Placeholders for functionality added in other files
    E: null,
    JSConfig: null,
    JSConfigUtil: null,
    ErrorManager: null,
    EventManager: null,
    L10N: null,
    // Globals-object contains global variables and sequences
    Globals: (function() {
        var globalId = 0;
        return {
            page: "",
            contextPath: "",
            strings: new Array(),
            globalId: function() {
                globalId++
                return globalId;
            }
        }
    })(),

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
        edit: function() {
            MetkaJS.PathBuilder().
                add(MetkaJS.Globals.page).
                add("edit").
                add(MetkaJS.SingleObject.id).
                navigate();
        },
        /**
         * Moves browser to adjacent revisionable.
         * @param next If true moves to next revisionable, if false moves to previous. Default is true.
         */
        adjacent: function(next) {
            if(next == null) {
                next = true;
            }
            MetkaJS.PathBuilder()
                .add(next?"next":"prev")
                .add(MetkaJS.Globals.page)
                .add(MetkaJS.SingleObject.id)
                .navigate();
        },
        /**
         * Submits revision modification form to given action
         * @param action MetkaJS.E.Form enumeration value
         */
        formAction: function(action) {
            var hash = window.location.hash;
            var hashField = $("#urlHash");
            if(hash != null && hashField != null && hashField.length > 0) {
                hashField.val(hash);
            }
            $("#revisionModifyForm").attr("action", MetkaJS.PathBuilder().add(MetkaJS.Globals.page).add(action).build());
            $("#revisionModifyForm").submit();
        }
    },

    // Shorthand function for viewing certain revision of certain revisionable. Forms the correct URL and navigates straight to it.
    view: function(id, revision) {
        MetkaJS.PathBuilder()
            .add(MetkaJS.Globals.page)
            .add("view")
            .add(id)
            .add(revision)
            .navigate();
    },

    /**
     * General close function for dialogs.
     * Closes the dialog with provided id.
     * @param id - Id of the dialog
     */
    dialogClose: function(id) {
        $("#"+id).dialog("close");
    },

    // Provides a new PathBuilder instance every time this is called. PathBuilder takes care of certain repeating elements in service URLs and can navigate straight to the built URL.
    PathBuilder: function() {return new function() {
        this.path = MetkaJS.Globals.contextPath;

        this.add = function(part) {
            if(part !== 'undefined' && part != null)
                this.path += "/"+part;
            return this;
        }

        this.build = function() {
            return this.path;
        }

        this.navigate = function() {
            location.href = this.path;
        }
    }},
    // Returns a jQuery wrapped page element for a given field key. Key is assumed to be for a top level input build by JSP and SpingForms but this is not checked.
    getValuesInput: function(key) {
        if(key != null) {
            return $("#values\\'"+key+"\\'");
        }
        return null;
    },
    // Returns an id for top level field input build by JSP and SpringForms
    getValuesInputId: function(key) {
        if(key != null) {
            return "values'"+key+"'";
        }
        return null;
    },
    // Returns a name for top level field input build by JSP and SpringForms
    getValuesInputName: function(key) {
        if(key != null) {
            return "values['"+key+"']";
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
    getElementWithDataValue: function(root, selector, key, value) {
        var elem = null;
        $(root).find(selector).each(function() {
            if($(this).data(key) == value) {
                elem = $(this);
            }
            return !(elem != null);
        });
        return elem;
    },

    /**
     * Checks whether given field should be rendered as a read only field.
     * There are multiple things that have to be considered for this but restrictions are implemented as needed.
     * TODO: Immutability causes problems since we have to have the original value from revision to determine that.
     * TODO: All in all immutability is handled wrong currently since it should depend on original value, not current value.
     *
     * @param field Field configuration used to check if read only is needed
     */
    isReadOnly: function(field) {
        // We are not viewing a revision, never use read only
        if(MetkaJS.SingleObject == null) {
            return false;
        }

        // We are in an approved revision, always use readonly
        if(MetkaJS.SingleObject.draft == false) {
            return true;
        }

        // Field should not be editable by user
        if(field.editable == false) {
            return true;
        }

        // Field is a REFERENCE text field, should not be editable since value comes from reference.
        if(field.type == MetkaJS.E.Field.REFERENCE) {
            return true;
        }

        /*if(field.immutable == true && currentValue != null) {
            return
        }*/
        // By default fields are not read only
        return false;
    }
};