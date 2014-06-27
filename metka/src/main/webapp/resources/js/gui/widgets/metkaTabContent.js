(function () {
    'use strict';

    $.widget('metka.metkaTabContent', $.metka.metka, {
        defaultElement: '<div class="tab-pane">',
        _create: function () {
            this._super();
            this.togglable();
            this.container();
            this.element
                .attr('id', this.options.id);
        }
    });
})();
