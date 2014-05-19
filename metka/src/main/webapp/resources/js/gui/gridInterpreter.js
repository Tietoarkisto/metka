(function() {
    'use strict';
    /**
     * Provides operations related to grid layout such as providing class names for correct size placement
     */
    GUI.Grid = (function() {
        var sizes = {
            large: "col-lg-",
            medium: "col-md-",
            small: "col-sm-",
            xs: "col-xs-"
        };

        // Set currently used prefix, this can be changed for compile time to match presets or it can
        // be changed at run-time if ever needed and if some functionality for changing it is provided.
        var prefix = sizes.xs;

        // Maximum number of columns povided by grid
        var COL_MAX = 12;

        /**
         * Returns css class matching given column amount and colspan combination.
         * Both variables can be left out in which case 1 is used as the default value.
         * If columns is larger than 4 then it's set to 4 and if colspan is larger than columns then it's set to columns abount
         *
         * @param columns Number of columns in the current container
         * @param colspan Number of columns this component is supposed to span
         * @returns {string} CSS class matching given configuration
         */
        function calculateColumnCSSClass(columns, colspan) {
            // Sanity checks
            if(!MetkaJS.isNumber(columns)) {
                columns = 1;
            }

            if(!MetkaJS.isNumber(colspan) || colspan > columns) {
                colspan = columns;
            }

            return prefix+((COL_MAX/columns)*colspan);
        }

        return {
            getColumnClass: calculateColumnCSSClass
        }

    }());
}());