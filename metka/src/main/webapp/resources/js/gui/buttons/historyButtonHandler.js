(function() {
    'use strict';
    GUI.ButtonParser.buttonHandlers.HISTORY = (function() {
        return function () {
            return this
                .click(MetkaJS.RevisionHistory.revisions);
        };
    }());
}());