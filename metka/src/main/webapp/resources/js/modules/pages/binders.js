define(function (require) {
    'use strict';

    if (true || location.pathname.split('/').indexOf('search') !== -1) {
        var options = {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            content: [
            ],
            buttons: [
                {
                    "&title": {
                        "default": "Lataa CSV"
                    }
                },
                {
                    "&title": {
                        "default": "Lisää aineisto mappiin"
                    }
                }
            ],
            data: {},
            dataConf: {}
        };
        return function (onLoad) {
            onLoad(options);
        };
    } else {
        return require('./defaults');
    }
});