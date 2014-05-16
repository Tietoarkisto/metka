(function() {
    'use strict';

    /**
     * Defines enum values used by Metka for different data types.
     * These can include parts of REST-call addresses so that the address can be changed easily since the javascript
     * should adapt just by changing this enumeration value.
     */
    MetkaJS.E = {
        /**
         * Event enum values
         */
        Event: {
            FIELD_CHANGE: 'FIELD_CHANGE',
            REFERENCE_CONTAINER_CHANGE: 'REFERENCE_CONTAINER_CHANGE',
            DIALOG_EVENT: 'DIALOG_EVENT'
        },

        /**
         * Reference enum values.
         */
        Ref: {
            REVISIONABLE: 'REVISIONABLE',
            JSON: 'JSON',
            DEPENDENCY: 'DEPENDENCY'
        },

        /**
         * Reference title enum values
         */
        RefTitle: {
            VALUE: 'VALUE',
            LITERAL: 'LITERAL'
        },

        /**
         * Field types
         */
        Field: {
            STRING: 'STRING',
            SELECTION: 'SELECTION',
            CONTAINER: 'CONTAINER',
            REFERENCE: 'REFERENCE',
            REFERENCECONTAINER: 'REFERENCECONTAINER'
        },

        /**
         * Configuration types
         */
        Conf: {
            STUDY: 'STUDY',
            STUDY_ATTACHMENT: 'STUDY_ATTACHMENT',
            SERIES: 'SERIES'
        },

        /**
         * SelectionList types
         */
        Selection: {
            VALUE: 'VALUE',
            SUBLIST: 'SUBLIST',
            LITERAL: 'LITERAL',
            REFERENCE: 'REFERENCE'
        },

        /**
         * Form actions
         */
        Form: {
            SAVE: 'save',
            APPROVE: 'approve'
        },

        /**
         * GUI-configuration container types
         */
        Container: {
            TAB: "TAB",
            SECTION: "SECTION",
            COLUMN: "COLUMN",
            ROW: "ROW",
            CELL: "CELL",
            EMPTYCELL: "EMPTYCELL"
        },

        /**
         * Visibility state enum
         */
        VisibilityState: {
            DRAFT: "DRAFT",
            APPROVED: "APPROVED",
            REMOVED: "REMOVED"
        }
    };
}());