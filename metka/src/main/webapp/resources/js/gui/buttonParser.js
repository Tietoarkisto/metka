(function() {
    'use strict';
    GUI.ButtonParser = (function() {
        function parseButton(root, button) {
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

            // Send button to renderer
            if(MetkaJS.exists(GUI.ButtonParser.buttonHandlers[button.type])) {
                GUI.ButtonParser.buttonHandlers[button.type].render(root, button);
            } else {
                GUI.ButtonParser.buttonHandlers['_GENERAL'].render(root, button);
            }
        }

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

        return {
            parse: parseButton,
            buttonHandlers: {}
        }
    }());
}());