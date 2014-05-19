(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers['SAVE'] = (function() {
        function renderSaveButton(root, button) {
            var input = GUI.Components.viewButton(button);
            input.click(function () {
                MetkaJS.SingleObject.formAction(MetkaJS.E.Form.SAVE);
            });
            root.append(input);
        }

        return {
            render: renderSaveButton
        }
    }());
}());