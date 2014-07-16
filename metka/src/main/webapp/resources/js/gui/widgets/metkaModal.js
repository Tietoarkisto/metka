(function () {
    'use strict';

    $.widget('metka.metkaModal', $.metka.metka, {
        defaultElement: '<div class="modal fade" tabindex="-1" role="dialog">',
        _create: function () {
            //this._super();

            if (!this.options.buttons) {
                this.options.buttons = [];
            } else {
                if (typeof this.options.buttons === 'string') {
                    this.options.buttons = [this.options.buttons];
                }
            }

            this.element
                .append($('<div class="modal-dialog">')
                    .append($('<div class="modal-content">')
                        .append($('<div class="modal-header">')
                            .append('<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>')
                            .append($('<h4 class="modal-title">')
                                .text(MetkaJS.L10N.localize(this.options, 'title'))))
                        .append((function () {
                            var $body = $('<div class="modal-body">');
                            if (this.options.body) {
                                $body.append(this.options.body);
                            }
                            return $body;
                        }).call(this))
                        .append($('<div class="modal-footer">')
                            .append((this.options.buttons || []).map(function (button) {
                                return $.metka.metkaButton(button).element
                                    .if(!button.preventDismiss, function () {
                                        // although some bootstrap features are accessible via .data method, this wont work
                                        // this.element.data('dismiss', 'modal');

                                        // default behaviour dismisses model
                                        this.attr('data-dismiss', 'modal');
                                    });
                            })))))
                .on('hidden.bs.modal', function () {
                    $(this).remove();
                })
                .modal();
        }
    });
})();
