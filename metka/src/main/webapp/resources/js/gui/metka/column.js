(function () {
    'use strict';

    $.widget('metka.metkaColumn', $.metka.metka, {
        _create: function () {
            //TODO: add optional this.options.title

            this._super();
            this.togglable();
            this.element
                .append(this.options.rows.map(this.children('metkaRow')));
        }
    });

    $.metka.metka.prototype.addHandler(MetkaJS.E.Container.COLUMN, {
        create: function () {
            return this.children('metkaColumn');
        },
        add: function (content) {
            this.element.append(content);
        }
    });
})();
