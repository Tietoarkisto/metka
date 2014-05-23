(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers.SAVE = (function() {
        return function () {
            return this
                .click(function () {
                    MetkaJS.SingleObject.formAction(MetkaJS.E.Form.SAVE);
                });
        };
    }());
}());