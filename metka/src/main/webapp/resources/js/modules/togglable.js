define(function (require) {
    'use strict';

    return function (options, children) {
        var hide = false;

        if (options.permissions && options.permissions.length) {
            // if some permission is not given
            hide = !options.permissions.every(function (permission) {
                return MetkaJS.User.role.permissions[permission];
            });
        }

        hide = hide || !!options.hidden;
        (children ? this.children() : this)
            .toggleClass('containerHidden', hide);
        return this;
    };
});