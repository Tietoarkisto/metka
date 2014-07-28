define(function (require) {
    'use strict';

    var sizes = {
        large: "col-lg-",
        medium: "col-md-",
        small: "col-sm-",
        xs: "col-xs-"
    };

    // Maximum number of columns provided by grid
    var COL_MAX = 12;

    // Set currently used prefix, this can be changed for compile time to match presets or it can
    // be changed at run-time if ever needed and if some functionality for changing it is provided.
    var prefix = sizes.xs;

        /**
         * Adds css class matching given column amount and colspan combination.
         * Both variables can be left out in which case 1 is used as the default value.
         * If columns is larger than 4 then it's set to 4 and if colspan is larger than columns then it's set to columns amount
         *
         * @param columns Number of columns in the current container
         * @param colspan Number of columns this component is supposed to span
         */
    return function (columns, colspan) {
        // Sanity checks
        if (!MetkaJS.isNumber(columns)) {
            columns = 1;
        }

        if (!MetkaJS.isNumber(colspan) || colspan > columns) {
            colspan = columns;
        }

        return this.addClass(prefix + (COL_MAX / columns * colspan));
    };
});
