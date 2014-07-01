(function () {
    'use strict';

    $.widget('metka.metkaRow', $.metka.metka, {
        defaultElement: '<div class="row">',
        _create: function () {
            this._super();
            this.togglable();
            this.element.append(this.options.cells.map(this.children('metkaCell'), this));
        }
    });
})();