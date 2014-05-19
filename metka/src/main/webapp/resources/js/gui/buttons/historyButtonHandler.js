(function() {
    'use strict';

    GUI.ButtonParser.buttonHandlers['HISTORY'] = (function() {
        function renderRevisionHistoryButton(root, button) {
            var input = GUI.Components.viewButton(button);
            input.click(MetkaJS.RevisionHistory.revisions);
            root.append(input);
        }

        return {
            render: renderRevisionHistoryButton
        }
    }());
}());