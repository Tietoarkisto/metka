define(function (require) {
    'use strict';

    var components = 0;
    var modals = 0;
    return function (type) {
        if(type == "M") {
            return "MODAL_"+(modals++);
        }
        return 'METKA_UI_' + (components++);
    };
});
