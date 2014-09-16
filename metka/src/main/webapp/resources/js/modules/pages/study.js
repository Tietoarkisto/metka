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
                        "content": [
                            {
                                "type": "COLUMN",
                                "columns": 2,
                                "rows": [
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aineiston numero",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "studyid"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aineiston nimi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "title"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Luovuttajan ym. sukunimi",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "publicationfirstsaved"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "Etunimi",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "savedAt"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Luovuttajan ym. org.",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "studyname"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Luovuttajan ym. laitos",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "studyname"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Tuottajan ym. sukunimi",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "publicationfirstsaved"
                                                }
                                            },
                                            {
                                                "type": "CELL",
                                                "title": "Etunimi",
                                                "horizontal": true,
                                                "field": {
                                                    "key": "savedAt"
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Sarjan nimi",
                                                "horizontal": true,
                                                "colspan": 2,
                                                "field": {
                                                    "key": "studyname"
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        "type": "TAB",
                        "title": "Virheelliset",
                        "content": []
                    }
                ],
                buttons: [
                    require('./../searchButton')('/revision/ajax/search', function () {
                        var response = {
                            type: require('./../../metka').PAGE,
                            values: {}
                        };
                        [
                            'studyid',
                            'title'
                        ].forEach(function (field) {
                                response.values[field] = data(field).getByLang(options.defaultLang);
                            });
                        return response;
                    }, function (data) {
                        return data.rows;
                    }, function (result) {
                        return {
                            id: result.id,
                            no: result.no,
                            seriesabbr: result.values.seriesabbr,
                            seriesname: result.values.seriesname,
                            state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                        };
                    }, {
                    }, [
                        "id",
                        "seriesabbr",
                        "seriesname",
                        "state"
                    ], function (transferRow) {
                        require('./../assignUrl')('view', {
                            id: transferRow.fields.id.value.current,
                            no: transferRow.fields.no.value.current
                        });
                    }),
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
                ],
                data: {},
                dataConf: {
                    fields: {
                        title: {
                            type: 'STRING'
                        },
                        studyid: {
                            type: 'STRING'
                        }
                    }
                }
            });
            var data = require('./../data')(options);
            onLoad();
        };
    } else {
        return require('./defaults');
    }
});