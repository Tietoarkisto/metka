define(function (require) {
    'use strict';

    var buttons = {
        APPROVE: function (options) {
            this.click(require('./formAction')('approve')(options, function (response) {
                require('./assignUrl')('view');
            },
            [
                'APPROVE_SUCCESSFUL'
            ]));
        },
        CANCEL: function () {
            this
                .text(MetkaJS.L10N.get('general.buttons.cancel'));
        },
        COMPARE: function () {
            this
                .text(MetkaJS.L10N.get('general.revision.compare'))
                .prop('disabled', true);
        },
        DISMISS: function () {
            this
                .text(MetkaJS.L10N.get('general.buttons.close'));
        },
        EDIT: function (options) {
            this.click(require('./formAction')('edit')(options, function (response) {
                require('./assignUrl')('view', {
                    no: ''
                });
            },
            [
                'REVISION_FOUND',
                'REVISION_CREATED'
            ]));
        },
        HISTORY: function () {
            var metka = require('./../metka');
            this
                .click(function () {
                    function checkRadioGroups() {
                        var beginVal = $('input[name="beginGrp"]:checked').val();
                        var endVal = $('input[name="endGrp"]:checked').val();
                        if (beginVal) {
                            $('input[name="endGrp"]').each(function () {
                                if ($(this).val() <= beginVal) {
                                    $(this).attr('disabled', true);
                                } else {
                                    $(this).attr('disabled', false);
                                }
                            });
                        }
                        if (endVal) {
                            $('input[name="beginGrp"]').each(function () {
                                if ($(this).val() >= endVal) {
                                    $(this).attr('disabled', true);
                                } else {
                                    $(this).attr('disabled', false);
                                }
                            });
                        }
                        if (beginVal >= endVal) {
                            $('#compareRevisions').prop('disabled', true);
                        } else {
                            $('#compareRevisions').prop('disabled', false);
                        }
                    }

                    var $table = $('<table class="table">')
                        .append($('<thead>')
                            .append($('<tr>')
                                .append((function () {
                                    var arr = [
                                        'general.revision',
                                        'general.revision.publishDate',
                                        'general.revision.compare.begin',
                                        'general.revision.compare.end'
                                    ];
/*
                                    if (metka.state === 'DRAFT') {
                                        arr.push('general.revision.replace');
                                    }*/

                                    return arr.map(function (entry) {
                                        return $('<th>')
                                            .text(MetkaJS.L10N.get(entry));
                                    });
                                })())));

                    require('./modal')({
                        title: MetkaJS.L10N.get('general.revision.revisions'),
                        body: $table,
                        buttons: [{
                            type: 'COMPARE',
                            //preventDismiss: true,
                            create: function () {
                                this
                                    .attr('id', 'compareRevisions')
                                    .click(function () {
                                        var $table = $('<table class="table">')
                                            .append($('<thead>')
                                                .append($('<tr>')
                                                    .append([
                                                            'Kenttä[kieli]',
                                                            'Alkuperäinen arvo',
                                                            'Nykyinen arvo'
                                                        ].map(function (entry) {
                                                            return $('<th>')
                                                                .text(entry);
                                                        }))));
                                        require('./modal')({
                                            title: MetkaJS.L10N.get('general.revision.revisions'),
                                            body: $table,
                                            buttons: [{
                                                type: 'DISMISS'
                                            }]
                                        });

                                        require('./server')('/revision/revisionCompare', {
                                            data: JSON.stringify({
                                                id: metka.id,
                                                begin: $('input[name="beginGrp"]:checked').val(),
                                                end: $('input[name="endGrp"]:checked').val()
                                            }),
                                            success: function (response) {
                                                if (response.result === 'OPERATION_SUCCESSFUL') {
                                                    $table
                                                        .append($('<tbody>')
                                                            .append(response.rows.map(function (row) {
                                                                return $('<tr>')
                                                                    .append([row.key, row.original, row.current].map(function (entry) {
                                                                        return $('<td>')
                                                                            .text(entry);
                                                                    }));
                                                            })));
                                                }
                                            }
                                        });
                                    });

                            }
                        }, {
                            type: 'DISMISS'
                        }]
                    });
                    require('./server')('/revision/revisionHistory', {
                        data: JSON.stringify({
                            id: metka.id
                        }),
                        success: function (response) {
                            $table
                                .append($('<tbody>')
                                    .append(response.rows.map(function (row) {
                                        return $('<tr>')
                                            .append((function () {
                                                var items = [
                                                    $('<a>', {
                                                        href: require('./url')('view', row),
                                                        text: row.no
                                                    }),
                                                        row.state === 'DRAFT' ? MetkaJS.L10N.get('general.DRAFT') : row.state,
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'beginGrp',
                                                        value: row.no,
                                                        change: checkRadioGroups
                                                    }),
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'endGrp',
                                                        value: row.no,
                                                        change: checkRadioGroups
                                                    })
                                                ];

                                                if (metka.state === 'DRAFT') {
                                                    items.push(require('./button')()()
                                                        .addClass('btn-xs')
                                                        .prop('disabled', true)
                                                        .text(MetkaJS.L10N.get('general.revision.replace')));
                                                }

                                                return items.map(function (entry) {
                                                    return $('<td>')
                                                        .append(entry);
                                                });
                                            })());
                                    })));

                            checkRadioGroups();
                        }
                    });
                });
        },
        NO: function () {
            this
                .text(MetkaJS.L10N.get('general.buttons.no'));
        },
        REMOVE: function (options) {
            var metka = require('./../metka');
            this
                .click(function () {
                    var type = metka.state === 'DRAFT' ? 'draft' : 'logical';
                    require('./modal')({
                        title: MetkaJS.L10N.get('confirmation.remove.revision.title'),
                        body: MetkaJS.L10N.get('confirmation.remove.revision.{type}.text'.supplant({
                            type: type
                        })).supplant({
                            '0': MetkaJS.L10N.get('confirmation.remove.revision.{type}.data.{page}'.supplant({
                                type: type,
                                page: metka.page
                            })),
                            '1': metka.id
                        }),
                        buttons: [{
                            type: 'YES',
                            create: function () {
                                $(this)
                                    .click(function () {
                                        require('./server')('/revision/ajax/remove', {
                                            data: JSON.stringify(options.data),
                                            success: function (response) {
                                                switch(response.result) {
                                                    case "SUCCESS_LOGICAL":
                                                        require('./assignUrl')('view');
                                                        break;
                                                    case "SUCCESS_DRAFT":
                                                        require('./assignUrl')('view', {no: ''});
                                                        break;
                                                    case "FINAL_REVISION":
                                                        require('./assignUrl')('searchPage');
                                                        break;
                                                }

                                            }
                                        });
                                    });
                            }
                        }, {
                            type: 'NO'
                        }]
                    });
                });
        },
        SAVE: function (options) {
            this.click(require('./formAction')('save')(options, function (response) {
                $.extend(options.data, response.data);
                options.$events.trigger('dataChanged');
            },
            [
                'SAVE_SUCCESSFUL',
                'SAVE_SUCCESSFUL_WITH_ERRORS',
                'NO_CHANGES_TO_SAVE'
            ]));
        },
        YES: function () {
            this.text(MetkaJS.L10N.get('general.buttons.yes'));
        }
    };

    return require('./inherit')(function (options) {
        function isVisible() {
            /**
             * Checks to see if user fulfills buttons userGroups restriction
             * @param button Button configuration
             * @returns {boolean} Is user groups restriction filled
             */
            function checkButtonGroupRestriction() {
                if(MetkaJS.hasContent(options.userGroups)) {
                    // TODO: Check users groups against this and return false if user doesn't fulfill the restriction
                }

                return true;
            }
            /**
             * Checks to see if user fulfills buttons isHandler restriction
             * @param button Button configuration
             * @returns {boolean} Is is handler restriction filled
             */
            function checkButtonHandlerRestriction() {
                if(MetkaJS.exists(options.isHandler)) {
                    // TODO: Check if user fulfills buttons isHandler restriction
                }

                return true;
            }

            function checkButtonStateRestriction() {
                var show = false;
                if(MetkaJS.hasContent(options.states)) {
                    var i, length;
                    for(i = 0, length = options.states.length; i < length; i++) {
                        var state = options.states[i];
                        switch(state) {
                            case MetkaJS.E.VisibilityState.DRAFT:
                                if (options.data.state.draft) {
                                    show = true;
                                }
                                break;
                            case MetkaJS.E.VisibilityState.APPROVED:
                                if(options.data.state.approved) {
                                    show = true;
                                }
                                break;
                            case MetkaJS.E.VisibilityState.REMOVED:
                                // TODO: Check for displaying removed revisionable
                                break;
                        }
                        if(show) {
                            break;
                        }
                    }
                } else {
                    show = true;
                }

                return show;
            }

            // Check if button should be displayed
            return checkButtonGroupRestriction()
                && checkButtonHandlerRestriction()
                && checkButtonStateRestriction();
        }

        var metka = require('./../metka');

        options = options || {};

        var $button = $('<button type="button" class="btn">');

        if (options.type) {
            if (!buttons[options.type]) {
                var message = MetkaJS.MessageManager.Message(null, 'alert.gui.missingButtonHandler.text');
                message.data.push(options.type);
                message.data.push(MetkaJS.L10N.localize(options, 'title'));
                MetkaJS.MessageManager.show(message);
                return;
            }
            buttons[options.type].call($button, options);
        }

        $button
            .addClass('btn-' + (options.style || 'primary'))
            .toggle(isVisible());

        if (MetkaJS.L10N.hasTranslation(options, 'title')) {
            $button
                .text(MetkaJS.L10N.localize(options, 'title'));
        }

        if (options.create) {
            options.create.call($button, options);
        }

        return $button;
    });
});
