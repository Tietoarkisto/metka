(function () {
    'use strict';

    $.widget('metka.metkaModal', $.metka.metka, {
        defaultElement: '<div class="modal fade" tabindex="-1" role="dialog">',
        _create: function () {
            //this._super();

            this.element
                .append($('<div class="modal-dialog">')
                    .append($('<div class="modal-content">')
                        .append($('<div class="modal-header">')
                            .append('<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>')
                            .append($('<h4 class="modal-title">')
                                .text(MetkaJS.L10N.localize(this.options, 'title'))))
                        .append($('<div class="modal-body">')
                            .text(this.options.body))
                        .append($('<div class="modal-footer">')
                            .append(this.options.buttons.map(function (button) {
                                return $.metka.metkaButton(button).element;
                            })))))
                .on('hidden.bs.modal', function () {
                    $(this).remove();
                })
                .modal();
        }
    });
})();
