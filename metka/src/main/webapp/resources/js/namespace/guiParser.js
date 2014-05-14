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
    MetkaJS.GUI = (function() {
        function buildGui(root) {

        }

        return {
            build: buildGui
        };
    }());
}());