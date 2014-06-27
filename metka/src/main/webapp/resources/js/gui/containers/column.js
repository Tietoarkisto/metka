(function () {
    'use strict';

    $.metka.addContainerType(MetkaJS.E.Container.COLUMN, {
        create: function () {
            return this.children('metkaColumn');
        },
        add: function (content) {
            this.element.append(content);
        }
    });
})();
