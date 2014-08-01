define(function (require) {
    'use strict';

    return require('./inherit')(function (options) {
        // In conf, columns is set for section. Parent of cell is row and grand-parent is section.
        //this.gridItem(this.options.parent.parent.columns, this.options.colspan);
        return require('./field')
            .call(require('./gridItem')
                .call(require('./togglable')
                    .call($('<div>'), options, true),
                options.parent.parent.columns,
                options.colspan),
            options);
    });
});
