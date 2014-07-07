(function () {
    'use strict';

    $.widget('metka.metkaLabel', $.metka.metka, {
        defaultElement: '<label>',
        _create: function () {
            this.element
                .text(MetkaJS.L10N.localize(this.options, 'title'));
            if (this.options.required) {
                this.element.append('<span class="glyphicon glyphicon-asterisk"></span>');
            }
        }
    });
})();
