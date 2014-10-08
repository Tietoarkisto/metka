define(function (require) {
    'use strict';

    return require('./inherit')(function (options) {
        var $div = $('<div>')
                    // In conf, columns is set for section. Parent of cell is row and grand-parent is section.
                    .addClass('col-xs-' + (12 * (options.colspan || 1) / (options.parent.parent.columns || 1)));
        require('./field').call($div, options);
        return require('./togglable')
                .call($div, options, true);
    });
});
