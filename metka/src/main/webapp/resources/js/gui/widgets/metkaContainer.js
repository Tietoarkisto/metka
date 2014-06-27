(function () {
    'use strict';

    var handlers = {};

    // static method for adding container constructors
    $.metka.addContainerType = function (type, handler) {
        handlers[type] = handler;
    };

    $.widget('metka.metkaContainer', $.metka.metka, {
        _create: function () {
            this._super();

            if (this.options.content) {
                // pick content by type
                var contents = this.options.content.reduce(function (picked, container) {
                    var type = container.type;
                    picked[type] = picked[type] || [];
                    picked[type].push(container);
                    return picked;
                }, {});

                // list of container types, highest priority first
                var priority = [
                    MetkaJS.E.Container.TAB,
                    MetkaJS.E.Container.SECTION,
                    MetkaJS.E.Container.COLUMN
                ];

                priority.forEach(function (type) {
                    // add content, if exists
                    var content = contents[type];
                    if (content) {
                        var handler = handlers[type];
                        var items = content.map(handler.create.call(this), this);
                        if (items.length) {
                            handler.add.call(this, items);
                        }
                    }
                }, this);

                // debug
                $.each(contents, function (type) {
                    if (priority.indexOf(type) === -1) {
                        console.error('unknown content type:', type);
                    }
                });
            }
        }
    });

    /*
     * Shorthand method for metka widget's internal use:
     * Usage: this.container();
     */
    $.widget('metka.metka', $.metka.metka, {
        container: function () {
            this.element.metkaContainer(this.options);
        }
    });
})();
