(function () {
    'use strict';
    GUI.ButtonParser.buttonHandlers.REMOVE = (function () {
        function confirmRemove() {
            var type = MetkaJS.SingleObject.draft ? "draft" : "logical";
            var message = MetkaJS.MessageManager.Message("confirmation.remove.revision.title",
                    "confirmation.remove.revision."+type+".text",
                function() {
                    MetkaJS.PathBuilder()
                        .add("remove")
                        .add(MetkaJS.Globals.page)
                        .add(type)
                        .add(MetkaJS.SingleObject.id)
                        .navigate();
                });
            message.pushData(MetkaJS.L10N.get("confirmation.remove.revision."+type+".data."+MetkaJS.Globals.page));
            message.pushData(MetkaJS.SingleObject.id);

            MetkaJS.MessageManager.show(message);
        }

        return function () {
            return this
                .click(confirmRemove);
        };
    }());
}());