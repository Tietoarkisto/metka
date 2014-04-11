/* Define MetkaJS namespace. Should include all global variables as well as all functions and other objects that
should be accessible from anywhere */
MetkaJS = {
    DialogHandlers: {}, // This is used to collect and reference custom dialog handlers used throughout the application
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
    // Returns an jQuery wrapped page element for a given field key. Key is assumed to be for a top level input build by JSP and SpingForms but this is not checked.
    getModelInput: function(key) {
        if(key != null) {
            return $("#values\\'"+key+"\\'");
        }
        return null;
    },
    // Returns an id for top level field input build by JSP and SpringForms
    getModelInputId: function(key) {
        if(key != null) {
            return "values'"+key+"'";
        }
        return null;
    },
    // Returns a name for top level field input build by JSP and SpringForms
    getModelInputName: function(key) {
        if(key != null) {
            return "values['"+key+"']";
        }
        return null;
    }
};