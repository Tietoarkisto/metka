(function() {
    'use strict';

    /**
     * General handler for GUI button bar buttons.
     * This is used if no actual handler is found for button type.
     * Displays an alert with message informing about missing handler.
     */
    MetkaJS.GUI.buttonHandlers['_GENERAL'] = (function() {
        function generalButtonRenderer(root, button) {
            var message = MetkaJS.ErrorManager.ErrorMessage(null, MetkaJS.L10N.get("messages.gui.missingButtonHandler"));

            MetkaJS.L10N.put("messages.gui.missingButtonHandler.title")
            message.data.push(button.type);
            message.data.push(MetkaJS.L10N.localize(button, "title"));

            MetkaJS.ErrorManager.show(message);
        }

        return {
            render: generalButtonRenderer
        }
    }());
}());