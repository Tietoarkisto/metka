(function () {
    'use strict';

    var handlers = {};

    $.widget('metka.metka', $.metka.metka, {
        container: function () {
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
                        var items = content.map(handlers[type].create.call(this), this);
                        if (items.length) {
                            handlers[type].add.call(this, items);
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
        },
        addHandler: function (type, handler) {
            handlers[type] = handler;
        }
    });
})();
