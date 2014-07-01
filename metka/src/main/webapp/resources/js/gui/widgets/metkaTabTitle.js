(function () {
    'use strict';

    $.widget('metka.metkaTabTitle', $.metka.metka, {
        defaultElement: '<li>',
        _create: function () {
            this._super();
            this.togglable();
            this.element
                .append($('<a data-target="#' + this.options.id + '" href="javascript:void 0;" data-toggle="tab">')
                    .text(MetkaJS.L10N.localize(this.options, 'title')));
        }
    });
})();
