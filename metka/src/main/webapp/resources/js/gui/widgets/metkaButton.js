(function() {
    'use strict';

    /**
     * Component builder that returns a button based on button-configuration as specified in the
     * GUI-configuration. These buttons should be used on view page to provide user controls related to
     * the whole revision or revisionable object.
     *
     */

    $.widget('metka.metkaButton', $.metka.metka, {
        defaultElement: '<button type="button" class="btn">',
        _create: function () {
            //this._super();
            //this.togglable();

            if (this.options.type) {
                if (!$.metka.metkaButton.prototype[this.options.type]) {
                    var message = MetkaJS.MessageManager.Message(null, 'alert.gui.missingButtonHandler.text');
                    message.data.push(this.options.type);
                    message.data.push(MetkaJS.L10N.localize(this.options, 'title'));
                    MetkaJS.MessageManager.show(message);
                    return;
                }
                this[this.options.type]();
            }

            this.element.addClass('btn-' + (this.options.style || 'primary'));

            if (MetkaJS.L10N.hasTranslation(this.options, 'title')) {
                this.element
                    .text(MetkaJS.L10N.localize(this.options, 'title'));
            }
        },
        APPROVE: function () {
            this.element
                .click(function () {
                    MetkaJS.SingleObject.formAction(MetkaJS.E.Form.APPROVE);
                });
        },
        COMPARE: function () {
            this.element
                .text(MetkaJS.L10N.get('general.revision.compare'));
        },
        DISMISS: function () {
            this.element
                .text(MetkaJS.L10N.get('general.buttons.close'));
        },
        EDIT: function () {
            this.element
                .click(function () {
                    MetkaJS.SingleObject.edit();
                });
        },
        HISTORY: function () {
            this.element
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
                        if (beginVal || endVal) {
                            $('#compareRevisions').attr('disabled', true);
                        } else {
                            $('#compareRevisions').attr('disabled', false);
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

                                    if (MetkaJS.SingleObject.draft) {
                                        arr.push('general.revision.replace');
                                    }

                                    return arr.map(function (entry) {
                                        return $('<th>')
                                            .text(MetkaJS.L10N.get(entry));
                                    });
                                })())));

                    $.metka.metkaModal({
                        title: MetkaJS.L10N.get('general.revision.revisions'),
                        body: $table,
                        buttons: [{
                            type: 'COMPARE'
                        }, {
                            type: 'DISMISS'
                        }]
                    });

                    $.ajax({
                        type: 'GET',
                        url: MetkaJS.PathBuilder().add('history').add('revisions').add(MetkaJS.SingleObject.id).build(),
                        success: function (response) {
                            $table
                                .append($('<tbody>')
                                    .append(response.map(function (row) {
                                        return $('<tr>')
                                            .append((function () {
                                                var items = [
                                                    $('<a>', {
                                                        href: MetkaJS.PathBuilder().add(MetkaJS.Globals.page).add('view').add(MetkaJS.SingleObject.id).add(row.revision).build(),
                                                        text: row.revision
                                                    }),
                                                        row.state === 'DRAFT' ? MetkaJS.L10N.get('general.DRAFT') : row.approvalDate,
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'beginGrp',
                                                        value: row.revision,
                                                        change: checkRadioGroups
                                                    }),
                                                    $('<input>', {
                                                        type: 'radio',
                                                        name: 'endGrp',
                                                        value: row.revision,
                                                        change: checkRadioGroups
                                                    })
                                                ];

                                                if (MetkaJS.SingleObject.draft) {
                                                    items.push($.metka.metkaButton({
                                                        type: 'REPLACE',
                                                        create: function () {
                                                            $(this).addClass('btn-xs');
                                                        }
                                                    }).element);
                                                }

                                                return items.map(function (entry) {
                                                    return $('<td>')
                                                        .append(entry);
                                                });
                                            })());
                                    })));

                            checkRadioGroups();
                        },
                        error: function (e) {
                            alert('Error: ' + JSON.stringify(e, null, 4));
                        }
                    });
                });
        },
        NO: function () {
            this.element
                .text(MetkaJS.L10N.get('general.buttons.no'));
        },
        REMOVE: function () {
            this.element
                .click(function () {
                    var type = MetkaJS.SingleObject.draft ? 'draft' : 'logical';
                    var modal = $.metka.metkaModal({
                        title: MetkaJS.L10N.get('confirmation.remove.revision.title'),
                        body: MetkaJS.L10N.get('confirmation.remove.revision.{type}.text'.supplant({
                            type: type
                        })).supplant({
                            '0': MetkaJS.L10N.get('confirmation.remove.revision.' + type + '.data.' + MetkaJS.Globals.page),
                            '1': MetkaJS.SingleObject.id
                        }),
                        buttons: [{
                            type: 'YES',
                            preventDismiss: true,
                            create: function () {
                                $(this)
                                    .click(function () {
                                        log('yes');
                                        setTimeout(function () {
                                            modal.element.modal('hide');
                                        }, 1000);
                                        return;
                                        MetkaJS.PathBuilder()
                                            .add("remove")
                                            .add(MetkaJS.Globals.page)
                                            .add(type)
                                            .add(MetkaJS.SingleObject.id)
                                            .navigate();
                                    });
                            }
                        }, {
                            type: 'NO'
                        }]
                    });
                });
        },
        REPLACE: function () {
            this.element
                .text(MetkaJS.L10N.get('general.revision.replace'));
        },
        SAVE: function () {
            this.element
                //.data('loading-text', 'Tallennetaan...')
                .click(function () {
                    //MetkaJS.SingleObject.formAction(MetkaJS.E.Form.SAVE);
                    //return;
                    //var $button = $(this);
                    //$button.button('loading');
                    var data = {
                        id: MetkaJS.SingleObject.id,
                        revision: MetkaJS.SingleObject.revision
                    };
                    $.each(MetkaJS.data.fields, function (key, value) {
                        data["values['" + key + "']"] = MetkaJS.Data.get(key);
                    });
                    $.ajax({
                        method: 'POST',
                        url: MetkaJS.Globals.contextPath + '/series/ajaxSave',
                        data: data,
                        dataType: 'json',
                        success: function (data) {
                            log('success', this, arguments, data.success)
                        },
                        complete: function (xhr, status) {
                            //$button.button('reset');
                            log(this, arguments)
                            $.metka.metkaModal({
                                title: 'Saved',
                                buttons: [{
                                    type: 'DISMISS'
                                }]
                            });
                        }
                    });
                });
        },
        isVisible: function () {
            // Check if button should be displayed
            return this.checkButtonGroupRestriction()
                && this.checkButtonHandlerRestriction()
                && this.checkButtonStateRestriction();
        },
        /**
         * Checks to see if user fulfills buttons userGroups restriction
         * @param button Button configuration
         * @returns {boolean} Is user groups restriction filled
         */
        checkButtonGroupRestriction: function () {
            if(MetkaJS.hasContent(this.options.userGroups)) {
                // TODO: Check users groups against this and return false if user doesn't fulfill the restriction
            }

            return true;
        },
        /**
         * Checks to see if user fulfills buttons isHandler restriction
         * @param button Button configuration
         * @returns {boolean} Is is handler restriction filled
         */
        checkButtonHandlerRestriction: function () {
            if(MetkaJS.exists(this.options.isHandler)) {
                // TODO: Check if user fulfills buttons isHandler restriction
            }

            return true;
        },
        checkButtonStateRestriction: function () {
            var show = false;
            if(MetkaJS.hasContent(this.options.states)) {
                var i, length;
                for(i = 0, length = this.options.states.length; i < length; i++) {
                    var state = this.options.states[i];
                    switch(state) {
                        case MetkaJS.E.VisibilityState.DRAFT:
                            if(MetkaJS.SingleObject.draft) {
                                show = true;
                            }
                            break;
                        case MetkaJS.E.VisibilityState.APPROVED:
                            if(!MetkaJS.SingleObject.draft) {
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
    });
}());