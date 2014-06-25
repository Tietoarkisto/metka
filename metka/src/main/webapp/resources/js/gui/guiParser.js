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

        /**
         * Takes a gui configuration with given context and builds a gui based on it.
         * The gui is added as a child to given root element.
         */
        $.widget('metka.metkaUI', $.metka.metka, {
            options: MetkaJS.JSGUIConfig[MetkaJS.Globals.page.toUpperCase()],
            _create: function () {
                console.log('create ui', this.options);
                console.log(JSON.stringify(this.options.content, null, 4));
                this._super();
                this.container();
                this.addButtons();
            },
            addButtons: function () {
                var $buttons = $.metka.buttons(this.options).element;
                if ($buttons.children().length > 0) {
                    this.element.append($buttons);
                }
            }
        });

        $.widget('metka.buttons', {
            defaultElement: '<div class="buttonsHolder pull-right">',
            _create: function () {

                /**
                 * Checks to see if user fulfills buttons userGroups restriction
                 * @param button Button configuration
                 * @returns {boolean} Is user groups restriction filled
                 */
                function checkButtonGroupRestriction(button) {
                    if(MetkaJS.hasContent(button.userGroups)) {
                        // TODO: Check users groups against this and return false if user doesn't fulfill the restriction
                    }

                    return true;
                }

                /**
                 * Checks to see if user fulfills buttons isHandler restriction
                 * @param button Button configuration
                 * @returns {boolean} Is is handler restriction filled
                 */
                function checkButtonHandlerRestriction(button) {
                    if(MetkaJS.exists(button.isHandler)) {
                        // TODO: Check if user fulfills buttons isHandler restriction
                    }

                    return true;
                }

                function checkButtonStateRestriction(button) {
                    var show = false;
                    if(MetkaJS.hasContent(button.states)) {
                        var i, length;
                        for(i = 0, length = button.states.length; i < length; i++) {
                            var state = button.states[i];
                            switch(state) {
                                case MetkaJS.E.VisibilityState.DRAFT:
                                    if(MetkaJS.SingleObject.draft) {
                                        show = true;
                                    }
                                    break;
                                case MetkaJS.E.VisibilityState.APPROVED:
                                    if(!MetkaJS.SingleObject.draft) {
                                        show = true;
                                    }
                                    break;
                                case MetkaJS.E.VisibilityState.REMOVED:
                                    // TODO: Check for displaying removed revisionable
                                    break;
                            }
                            if(show) {
                                break;
                            }
                        }
                    } else {
                        show = true;
                    }

                    return show;
                }

                this.element.append(this.options.buttons.map(function (button) {
                    // Check if button should be displayed
                    var display = true;
                    // Check group
                    display = checkButtonGroupRestriction(button);
                    if(display === true) {
                        display = checkButtonHandlerRestriction(button);
                    }
                    if(display === true) {
                        display = checkButtonStateRestriction(button);
                    }

                    if(display === false) {
                        // At least one of the restrictions was not fulfilled, return without displaying the button
                        return;
                    }

                    if (!MetkaJS.exists(GUI.ButtonParser.buttonHandlers[button.type])) {
                        var message = MetkaJS.MessageManager.Message(null, 'alert.gui.missingButtonHandler.text');
                        message.data.push(button.type);
                        message.data.push(MetkaJS.L10N.localize(button, 'title'));
                        MetkaJS.MessageManager.show(message);
                        return;
                    }

                    return GUI.ButtonParser.buttonHandlers[button.type].call(GUI.Components.viewButton(button));
                }, this));
            }
        });

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