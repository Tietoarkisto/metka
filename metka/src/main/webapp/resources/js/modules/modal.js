define(function (require) {
    'use strict';

    return function (options) {
        function getTitle(options) {
            if(options.dialogTitle && MetkaJS.L10N.containsText(options.dialogTitle, options.type)) {
                return MetkaJS.L10N.localize(options.dialogTitle, options.type);
            } else if(options.dialogTitles
                    && options.dialogTitles[options.containerKey]
                    && MetkaJS.L10N.containsText(options.dialogTitles[options.containerKey], options.type)) {
                return MetkaJS.L10N.localize(options.dialogTitles[options.containerKey], options.type);
            } else if(options.title) {
                return MetkaJS.L10N.get(options.title);
            } else {
                return MetkaJS.L10N.localize(options, 'title');
            }
        }

        options.modalTarget = require('./autoId')("M");
        var $modal = $('<div class="modal fade" tabindex="-1" role="dialog" id="'+options.modalTarget+'">')
            .append($('<div class="modal-dialog">')
                .on('refresh.metka', function () {
                    options.readOnly = require('./isDataReadOnly')(options.data, options.isRelatedStudyDraftForCurrentUser);
                    if (!options.buttons) {
                        options.buttons = [];
                    } else {
                        if (typeof options.buttons === 'string') {
                            options.buttons = [options.buttons];
                        }
                    }

                    options.buttons.forEach(function(button, i, buttons) {
                        if(typeof button === 'string') {
                            buttons[i] = {
                                type: button
                            }
                        }
                    });

                    var $body = $('<div class="modal-body">');
                    if (options.content) {
                        require('./container').call($body, options);
                    } else {
                        $body.append(options.body);
                    }

                    var $header = $('<div class="modal-header">')
                        .append('<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>')
                        .append($('<h4 class="modal-title">')
                            .text(getTitle(options)));
                    $(this)
                        .empty()
                        .toggleClass('modal-lg', !!options.large)
                        .append($('<div class="modal-content">')
                            .append($header)
                            .append($body)
                            .append($('<div class="modal-footer">')
                                .append((options.buttons || []).map(function (buttonOptions) {
                                    $.extend(true, buttonOptions, {
                                        modalTarget: options.modalTarget
                                    });
                                    return require('./button')(options)(buttonOptions)
                                        .if(!buttonOptions.preventDismiss, function () {
                                            // although some bootstrap features are accessible via .data method, this wont work
                                            // this.data('dismiss', 'modal');

                                            // default behaviour dismisses modal
                                            this.attr('data-dismiss', 'modal');
                                        });
                                }))));

                    if (options.translatableCurrentLang) {
                        $header.append(require('./languageRadioInputGroup')(options, 'dialog-translation-lang', options.translatableCurrentLang));
                    }
                    return false;
                })
                .trigger('refresh.metka'))
            .on('hidden.bs.modal', function () {
                $(this).remove();

                // workaround to keep scrolling enabled for nested modals

                var $body = $('body');
                var $otherModal = $body.children('.modal');

                // if there are other modals on screen
                if ($otherModal.length) {
                    // get instance of Bootstrap Modal object
                    var bsModal = $otherModal.data('bs.modal');

                    // do same things that Bootstrap does internally, when dialog is opened
                    $body.addClass('modal-open');
                    bsModal.setScrollbar();
                }
            })
            .modal({
                backdrop: 'static'
            });

        if(options.modalEvents) {
            $.each(options.modalEvents, function(prop, callback) {
                log(prop, callback, $modal);
                $modal.on(prop, callback);
            });
        }

        return $modal;
    };
});
