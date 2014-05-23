(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers.EDIT = (function () {
        return function () {
            return this
                .click(function () {
                    MetkaJS.SingleObject.edit();
                });
        };
    }());
}());