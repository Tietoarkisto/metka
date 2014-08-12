define(function (require) {
    'use strict';

    if (location.pathname.split('/').indexOf('search') !== -1) {
        var options = {
            header: MetkaJS.L10N.get('type.PUBLICATION.search'),
            content: [
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
                                        type: 'PUBLICATION'
                                    }),
                                    success: function (response) {
                                        if (response.result === 'REVISION_CREATED') {
                                            require('./../assignUrl')('view', {
                                                id: response.data.key.id,
                                                revision: response.data.key.no,
                                                page: response.data.configuration.type.toLowerCase()
                                            });
                                        }
                                    }
                                });
                            });
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