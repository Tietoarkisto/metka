define(function (require) {
    'use strict';

    return function (options, children) {
        (children ? this.children() : this)
            .toggleClass('containerHidden', !!options.hidden || !require('./hasEveryPermission')(options.permissions));
        return this;
    };
});