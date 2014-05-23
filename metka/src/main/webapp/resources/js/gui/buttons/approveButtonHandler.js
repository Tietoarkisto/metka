(function () {
    'use strict';
    GUI.ButtonParser.buttonHandlers.APPROVE = (function () {
        return function () {
            return this
                .click(function () {
                    MetkaJS.SingleObject.formAction(MetkaJS.E.Form.APPROVE);
                });
        };
    }());
}());