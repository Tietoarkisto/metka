define(function (require) {
    'use strict';

    return function (permissions) {
        if (permissions) {
            // true, if every permission is given
            return permissions.every(function (permission) {
                return MetkaJS.User.role.permissions[permission];
            });
        }
        return true;
    };
});
