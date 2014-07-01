(function () {
    'use strict';

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

    $.widget('metka.metkaButtonContainer', $.metka.metkaUI, {
        defaultElement: '<div class="buttonsHolder pull-right">',
        _create: function () {
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
})();
