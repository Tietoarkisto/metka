define(function (require) {
    var i = 0;
    return function () {
        return 'METKA_UI_' + (i++);
    };
});
