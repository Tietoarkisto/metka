define(function (require) {
    'use strict';

    return function (data, lang) {
        return $.extend(require('./../object/transferRow')({
            name: data.title,
            query: data.query,
            id: data.id
        }, lang), {
            saved: {
                time: data.savedAt,
                user: data.savedBy
            }
        });
    };
});
