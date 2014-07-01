(function () {
    'use strict';

    $.widget('metka.metkaColumn', $.metka.metka, {
        _create: function () {
            this._super();
            this.togglable();
            if (MetkaJS.L10N.hasTranslation(this.options, 'title')) {
                this.element.append($('<h4>')
                    .text(MetkaJS.L10N.localize(this.options, 'title')));
            }
            this.element.append(this.options.rows.map(this.children('metkaRow')));
        }
    });
})();
