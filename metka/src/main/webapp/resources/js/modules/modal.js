define(function (require) {
    'use strict';

    return function (options) {
        if (!options.buttons) {
            options.buttons = [];
        } else {
            if (typeof options.buttons === 'string') {
                options.buttons = [options.buttons];
            }
        }

        return $('<div class="modal fade" tabindex="-1" role="dialog">')
            .append($('<div class="modal-dialog">')
                .append($('<div class="modal-content">')
                    .append($('<div class="modal-header">')
                        .append('<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>')
                        .append($('<h4 class="modal-title">')
                            .text(MetkaJS.L10N.localize(options, 'title'))))
                    .append($('<div class="modal-body">')
                        .append(options.body))
                    .append($('<div class="modal-footer">')
                        .append((options.buttons || []).map(function (buttonOptions) {
                            return require('./button')(options)(buttonOptions)
                                .if(!buttonOptions.preventDismiss, function () {
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
    };
});
