(function() {
    'use strict';

    /**
     * Insert default handler for GUI button bar buttons.
     * This is used if no actual handler is found for button type.
     * Displays an alert with message informing about missing handler.
     * All button handlers should provide at least a basic interface function 'render'
     * render: function(root, button)
     *      - root: html-element (as wrapped in jQuery-array object into which the button will be appended
     *      - button: Button configuration as defined in the GUI-configuration specification.
     *                This is used to display the actual button. Restrictions for display should have been sorted out
     *                earlier and handlers should only be called if the button is actually to be shown
     */
    GUI.ButtonParser.buttonHandlers['_GENERAL'] = (function() {
        function generalButtonRenderer(root, button) {
            var message = MetkaJS.MessageManager.Message(null, "alert.gui.missingButtonHandler.text");
            message.data.push(button.type);
            message.data.push(MetkaJS.L10N.localize(button, "title"));

            MetkaJS.MessageManager.show(message);
        }

        return {
            render: generalButtonRenderer
        }
    }());
}());