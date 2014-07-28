define([
    './gridItem',
    './field',
    './inherit',
    './togglable'
], function (gridItem, field, inherit, togglable) {
    'use strict';

    return inherit(function (options) {

        // In conf, columns is set for section. Parent of cell is row and grand-parent is section.
        //this.gridItem(this.options.parent.parent.columns, this.options.colspan);
        return field.call(gridItem.call(togglable.call($('<div>'), options, true), options.parent.parent.columns, options.colspan), options);
    });
});
