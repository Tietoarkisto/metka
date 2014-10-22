define(function (require) {
    'use strict';

    return require('./inherit')(function (options) {
        return require('./togglable').call($('<div class="row">')
            .append(options.cells.map(require('./cell')(options))), options);
    });
});
