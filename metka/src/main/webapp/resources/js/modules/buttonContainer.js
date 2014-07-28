define(function (require) {
    'use strict';

    return function (options) {
        this.append($('<div class="modal-footer">')
            .append((options.buttons || []).map(require('./button'))));
    };
});
