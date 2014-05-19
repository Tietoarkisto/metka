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

        /**
         * Takes a gui configuration with given context and builds a gui based on it.
         * The gui is added as a child to given root element.
         *
         * @param root Where gui is build
         * @param context Configuration context for gui
         */
        function buildGui(root, context) {
            if(!MetkaJS.exists(context)) {
                context = MetkaJS.Globals.page.toUpperCase();
            }
            var config = MetkaJS.JSGUIConfig[context];

            // Sanity check to see that the configuration actually exists
            if(!MetkaJS.exists(config)) {
                return;
            }

            // NOTICE: bootstrap test, remove
            testBootstrap(root);

            buildContainers(root, config);

            buildButtons(root, config);

        }

        function testBootstrap(root) {
            var testCon = $("<div>", {class: "container"});

            var row = $("<div>", {class: "row"});
            var col1 = $("<div>", {class: GUI.Grid.getColumnClass(3, 2)});
            col1.append("Sarake 1");
            row.append(col1);
            /*var col2 = $("<div>", {class: GUI.Grid.getColumnClass(3, 1)});
            col2.append("Sarake 2");
            row.append(col2);*/
            var col3 = $("<div>", {class: GUI.Grid.getColumnClass(3, 1)});
            col3.append("Sarake 3");
            row.append(col3);

            testCon.append(row);

            root.append(testCon);
        }

        function buildContainers(root, config) {
            var i, length, currentContainer;
            // Get the highest priority type in content so that containers can be processed in right order
            var highestType = null;
            for(i = 0, length = config.content.length; i < length; i++) {
                currentContainer = config.content[i];
                if(highestType === null) {
                    highestType = currentContainer.type;
                    continue;
                }

                if(containerPriorityComparator(currentContainer.type, highestType) < 0) {
                    highestType = currentContainer.type;
                }

                // Break the loop since highest type can not grow.
                if(highestType === MetkaJS.E.Container.TAB) {
                    break;
                }
            }

            // Sanity check, we can't really do anything at this point.
            if(highestType === null) {
                return;
            }

            // We have highest type in content, lets collect all container of the highest type and let everything else wait
            var highest = new Array(); // Contains indexes for the highest type
            var rest = new Array(); // Contains indexes for the rest

            for(i = 0; i < length; i++) {
                // If current container is of highest type push current index to highest-array, otherwise push index to rest-array.
                if(config.content[i].type === highestType) {
                    highest.push(i);
                } else {
                    rest.push(i);
                }
            }

            // Handle all containers of the highest type
            for(i = 0, length = highest.length; i < length; i++) {

            }

            // Handle rest of the containers
            for(i = 0, length = rest.length; i < length; i++) {

            }

            // Add all top containers in order to root
            // Add highest containers
            // Add rest containers
        }

        function buildButtons(root, config) {
            var holder = null;
            if(config.buttons.length > 0) {
                holder = $("<div>", {class: "buttonsHolder"});
            }
            // Render buttons to button area
            var i, length;
            for(i = 0, length = config.buttons.length; i < length; i++) {
                GUI.ButtonParser.parse(holder, config.buttons[i]);
            }

            // Add buttons to root
            if(MetkaJS.exists(holder)) {
                root.append(holder);
            }
        }

        return {
            build: buildGui,
            containerHandlers: {},
            Components: {},
            Grid: null,
            ButtonParser: null
        };
    }());
}());