(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers['APPROVE'] = (function() {
        function renderApproveButton(root, button) {
            var input = GUI.Components.viewButton(button);
            input.click(function () {
                MetkaJS.SingleObject.formAction(MetkaJS.E.Form.APPROVE);
            });
            root.append(input);
        }

        return {
            render: renderApproveButton
        }
    }());
}());