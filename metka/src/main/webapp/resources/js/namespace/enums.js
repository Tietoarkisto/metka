/**
 * Defines enum values used by Metka for different data types.
 */
MetkaJS.E = {
    /**
     * Event enum values
     */
    Event: {
        FIELD_CHANGE: "FIELD_CHANGE",
        REFERENCE_CONTAINER_CHANGE: "REFERENCE_CONTAINER_CHANGE",
        DIALOG_EVENT: "DIALOG_EVENT"
    },
    /**
     * Reference enum values.
     */
    Ref: {
        REVISIONABLE: "REVISIONABLE",
        JSON: "JSON",
        DEPENDENCY: "DEPENDENCY"
    },
    /**
     * Reference title enum values
     */
    RefTitle: {
        VALUE: "VALUE",
        LITERAL: "LITERAL"
    },
    /**
     * Field types
     */
    Field: {
        STRING: "STRING",
        CHOICE: "CHOICE",
        CONTAINER: "CONTAINER",
        REFERENCE: "REFERENCE",
        REFERENCECONTAINER: "REFERENCECONTAINER"
    },
    /**
     * Configuration types
     */
    Conf: {
        STUDY: "STUDY",
        STUDY_ATTACHMENT: "STUDY_ATTACHMENT",
        SERIES: "SERIES"
    },
    /**
     * Choicelist types
     */
    Choice: {
        VALUE: "VALUE",
        SUBLIST: "SUBLIST",
        LITERAL: "LITERAL",
        REFERENCE: "REFERENCE"
    }
};