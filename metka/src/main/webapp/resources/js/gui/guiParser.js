(function() {
    'use strict';
    /**
     * Main component for parsing the gui configuration and displaying the page based on that configuration.
     * This should be for handling rendering side of the UI. Data handling (setting and fetching of data for revisions)
     * has its own component MetkaJS.Data.
     * This component only handles pages that can be configured, which at this point is Revision views for base types
     * and possibly a couple of types more if they fit in the configuration.
     *
     * @type {{}}
     */
    window.GUI = (function() {
        var Id = (function () {
            var i = 0;
            return function () {
                return 'GUI_' + (i++);
            };
        })();

        /**
         * Compares two container types and returns integer telling if a is of higher or lower priority than b.
         * Assumes priorities are strings equal to MetkaJS.E.Container enum values.
         *
         * @param a MetkaJS.E.Container enum value
         * @param b MetkaJS.E.Container enum value
         * @returns {number} Negative number if a is of higher priority than b, positive if reverse and zero if they have the same priority.
         */
        function containerPriorityComparator(a, b) {
            if(a === b) {
                return 0;
            }

            if((a === MetkaJS.E.Container.CELL && b === MetkaJS.E.Container.EMPTYCELL)
                    || (a === MetkaJS.E.Container.EMPTYCELL && b === MetkaJS.E.Container.CELL)) {
                return 0;
            }

            if(a === MetkaJS.E.Container.CELL || a === MetkaJS.E.Container.EMPTYCELL) {
                return -1;
            }

            if(b === MetkaJS.E.Container.CELL || b === MetkaJS.E.Container.EMPTYCELL) {
                return 1;
            }

            if(a === MetkaJS.E.Container.TAB) {
                return -1;
            }

            if(b === MetkaJS.E.Container.TAB) {
                return 1;
            }

            if(a === MetkaJS.E.Container.SECTION) {
                return -1;
            }

            if(b === MetkaJS.E.Container.SECTION) {
                return 1;
            }

            if(a === MetkaJS.E.Container.COLUMN) {
                return -1;
            }

            if(b === MetkaJS.E.Container.COLUMN) {
                return 1;
            }

            // Failsafe, this should never be reached but we can't really do much about it.
            return 0;
        }

        return {
            containerHandlers: {},
            Components: {},
            Fields: {},
            Grid: null,
            ButtonParser: null,
            id: Id
        };
    }());
}());