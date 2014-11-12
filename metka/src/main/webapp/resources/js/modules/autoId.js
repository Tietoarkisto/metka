define(function (require) {
    'use strict';

    var i = 0;
    return function (type) {
        if(type == "M") {
            return "MODAL_"+(i++);
        }
        return 'METKA_UI_' + (i++);
    };
});
