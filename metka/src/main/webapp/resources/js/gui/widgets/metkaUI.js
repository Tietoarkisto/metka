(function () {
    'use strict';

    $.widget('metka.metkaUI', $.metka.metka, {
        options: MetkaJS.JSGUIConfig[MetkaJS.Globals.page.toUpperCase()],
        _create: function () {
            console.log('create ui', this.options);
            console.log(JSON.stringify(this.options.content, null, 4));
            this._super();
            this.container();
            this.buttonContainer();
        },
        buttonContainer: function () {
            var $buttons = $.metka.metkaButtonContainer(this.options).element;
            if ($buttons.children().length > 0) {
                this.element.append($buttons);
            }
        }
    });
})();
