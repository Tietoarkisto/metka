define(function (require) {
    'use strict';

    /**
     * Processes all container type UI elements
     */
    return function (options) {
        var handlers = {
            TAB: require('./tab'),
            SECTION: require('./section'),
            COLUMN: require('./column')
        };

        if (Array.isArray(options.content)) {
            // pick content by type
            var contents = options.content.reduce(function (picked, container) {
                var type = container.type;
                picked[type] = picked[type] || [];
                picked[type].push(container);
                return picked;
            }, {});

            // list of container types, highest priority first
            var priority = ['TAB', 'SECTION', 'COLUMN'];

            priority.forEach(function (type) {
                // add content, if exists
                var content = contents[type];
                if (content) {
                    var handler = handlers[type];
                    handler.add.call(this, content.map(handler.create(options), this));
                }
            }, this);
        }
        return this;
    };
});
