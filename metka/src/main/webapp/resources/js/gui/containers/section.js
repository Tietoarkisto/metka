(function () {
    'use strict';

    $.metka.addContainerType(MetkaJS.E.Container.SECTION, {
        create: function () {
            return this.children('metkaSection');
        },
        add: function (sections) {
            this.element.append($('<div class="panel-group">')
                .append(sections));
        }
    });
})();
