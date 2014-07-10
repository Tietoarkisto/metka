(function () {
    'use strict';

    $.widget('metka.metkaButtonContainer', $.metka.metkaUI, {
        defaultElement: '<div class="buttonsHolder pull-right">',
        _create: function () {
            /*
            this.element.append(this.options.buttons.map(function (button) {
                return $.metka.metkaButton(button);
            }).filter(function (button) {
                return button.isVisible();
            }).map(function (button) {
                return button.element;
            }));
            */

            // for some reason, jQuery can't construct widgets this way
            //var buttons = this.options.buttons.map($.metka.metkaButton);

            var buttons = this.options.buttons.map(function (button) {
                return $.metka.metkaButton(button);
            });
            var visibleButtons = buttons.filter(function (button) {
                return button.isVisible();
            });
            var buttonElements = visibleButtons.map(function (button) {
                return button.element;
            });
            this.element.append(buttonElements);
        }
    });
})();
