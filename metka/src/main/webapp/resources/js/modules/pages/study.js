define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var options = {
            header: MetkaJS.L10N.get('type.STUDY.search'),
            content: [
                {
                    "type": "TAB",
                    "title": "Aineistohaku",
                    "content": []
                },
                {
                    "type": "TAB",
                    "title": "Virheelliset",
                    "content": []
                }
            ],
            buttons: [
                {
                    "&title": {
                        "default": "Tee haku"
                    },
                    create: function () {
                        this
                            .click(function () {

                            })
                    }
                },
                {
                    "&title": {
                        "default": "Tyhjennä"
                    },
                    create: function () {
                        this.click(function () {
                            log('TODO: tyhjennä lomake')
                        });
                    }
                }
            ],
            data: {},
            dataConf: {}
        };
        return options;
    } else {
        return require('./defaults');
    }
});