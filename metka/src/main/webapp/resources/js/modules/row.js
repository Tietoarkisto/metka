define([
    './cell',
    './inherit',
    './togglable'
], function (cell, inherit, togglable) {
    'use strict';

    return inherit(function (options) {
        return togglable.call($('<div class="row">')
            .append(options.cells.map(cell(options))), options);
    });
});
