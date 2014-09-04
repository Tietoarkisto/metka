define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        return function (options, onLoad) {
            $.extend(options, {
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
                                                dataarrivaldate: moment(Date.now()).format('YYYY-MM-DDThh:mm:ss.s')
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
            });
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});