define(function (require) {
    'use strict';

    return function (options) {
        return this.toggleClass('alert-warning', !!options.important);
    };
});
