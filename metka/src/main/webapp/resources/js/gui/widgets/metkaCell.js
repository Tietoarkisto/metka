(function () {
    'use strict';

    $.widget('metka.metkaCell', $.metka.metka, {
        _create: function () {
            this._super();

            this.togglable(true);

            // In conf, columns is set for section. Parent of cell is row and grand-parent is section.
            this.gridItem(this.options.parent.parent.columns, this.options.colspan);
            this.element.metkaField(this.options);
        }
    });
})();
