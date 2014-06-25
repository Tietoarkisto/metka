(function () {
    'use strict';

    $.widget('metka.metkaCell', $.metka.metkaField, {
        _create: function () {
            this._super();

            this.element
                // Columns is set for section. Parent is row and grand-parent is section.
                .addClass(GUI.Grid.getColumnClass(this.options.parent.parent.columns, this.options.colspan))
            this.togglable(true);
        }
    });
})();
