define(function (require) {
    'use strict';

    /**
     * Page footer buttons
     */
    return function (options) {
        this.append($('<div class="modal-footer">')
            .append((options.buttons || []).map(require('./button')(options))));
    };
});
