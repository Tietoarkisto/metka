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
                },
                {
                    "&title": {
                        "default": "Lisää uusi"
                    },
                    create: function () {
                        this
                            .click(function () {
                                require('./../server')('create', {
                                    data: JSON.stringify({
                                        type: 'STUDY',
                                        parameters: {
                                            submissionid: Date.now() % 1000,
                                            dataarrivaldate: Date.now()
                                        }
                                    }),
                                    success: function (response) {
                                        if (response.result === 'REVISION_CREATED') {
                                            require('./../assignUrl')('view', {
                                                id: response.data.key.id,
                                                no: response.data.key.no,
                                                page: response.data.configuration.type.toLowerCase()
                                            });
                                        }
                                    }
                                });
                            });
                    }
                }
            ]
        };
        return function (defaultOptions, onLoad) {
            $.extend(defaultOptions, options);
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});