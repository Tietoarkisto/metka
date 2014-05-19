(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers['EDIT'] = (function() {
        function renderEditButton(root, button) {
            var input = GUI.Components.viewButton(button);
            input.click(function () {
                MetkaJS.SingleObject.edit();
            });
            root.append(input);
        }

        return {
            render: renderEditButton
        }
    }());
}());