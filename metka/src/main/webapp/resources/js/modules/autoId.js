define(function (require) {
    'use strict';

    var i = 0;
    return function () {
        return 'METKA_UI_' + (i++);
    };
});
