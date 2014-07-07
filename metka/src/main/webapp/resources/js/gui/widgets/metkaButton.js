(function() {
    'use strict';

    /**
     * Component builder that returns a button based on button-configuration as specified in the
     * GUI-configuration. These buttons should be used on view page to provide user controls related to
     * the whole revision or revisionable object.
     *
     */

    $.widget('metka.metkaButton', $.metka.metka, {
        defaultElement: '<button type="button" class="btn btn-primary">',
        _create: function () {
            //this._super();
            //this.togglable();

            if (!$.metka.metkaButton.prototype[this.options.type]) {
                var message = MetkaJS.MessageManager.Message(null, 'alert.gui.missingButtonHandler.text');
                message.data.push(this.options.type);
                message.data.push(MetkaJS.L10N.localize(button, 'title'));
                MetkaJS.MessageManager.show(message);
                return;
            }
            this.element
                .text(MetkaJS.L10N.localize(this.options, 'title'));
            this[this.options.type]();
            return this;
        },
        APPROVE: function () {
            this.element
                .click(function () {
                    MetkaJS.SingleObject.formAction(MetkaJS.E.Form.APPROVE);
                });
        },
        EDIT: function () {
            this.element
                .click(function () {
                    MetkaJS.SingleObject.edit();
                });
        },
        HISTORY: function () {
            this.element
                .click(MetkaJS.RevisionHistory.revisions);
        },
        REMOVE: function () {
            this.element
                .click(function () {
                    var type = MetkaJS.SingleObject.draft ? "draft" : "logical";
                    var message = MetkaJS.MessageManager.Message("confirmation.remove.revision.title",
                            "confirmation.remove.revision."+type+".text",
                        function() {
                            MetkaJS.PathBuilder()
                                .add("remove")
                                .add(MetkaJS.Globals.page)
                                .add(type)
                                .add(MetkaJS.SingleObject.id)
                                .navigate();
                        });
                    message.pushData(MetkaJS.L10N.get("confirmation.remove.revision."+type+".data."+MetkaJS.Globals.page));
                    message.pushData(MetkaJS.SingleObject.id);

                    MetkaJS.MessageManager.show(message);
                });
        },
        SAVE: function () {
            this.element
                .click(function () {
                    MetkaJS.SingleObject.formAction(MetkaJS.E.Form.SAVE);
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