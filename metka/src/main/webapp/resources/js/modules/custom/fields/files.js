define(function (require) {
    'use strict';

    return {
        buttons: [{
            "&title": {
                "default": MetkaJS.L10N.get('general.table.add') + ' 2'
            },
            create: function () {
                this
                    .click(function () {
                        require('./../../server')('create', {
                            data: JSON.stringify({
                                type: 'STUDY_ATTACHMENT',
                                parameters: {
                                    study: require('./../../../metka').id
                                }
                            }),
                            success: function (response) {
                                if (response.result === 'REVISION_CREATED') {
                                    require('./../../server')('viewAjax', {
                                        id: response.data.key.id,
                                        no: response.data.key.no,
                                        page: 'study_attachment'
                                    }, {
                                        method: 'GET',
                                        success: function (data) {
                                            /*metka.revision = metka.no = data.transferData.key.no;
                                             options.readOnly = !data.transferData.state.draft || !(data.transferData.state.handler === MetkaJS.User.userName);
                                             options.dataConf = data.configuration;
                                             options.data = data.transferData;
                                             options.header = function header($header) {
                                             }*/
                                            var ;
                                            var options = $.extend(data.gui, {
                                                title: 'Muokkaa tiedostoa',
                                                data: data.transferData,
                                                dataConf: data.configuration,
                                                $events: $({}),
                                                defaultLang: 'DEFAULT',
                                                large: true,
                                                content: [
                                                    {
                                                        "type": "COLUMN",
                                                        "columns": 1,
                                                        "rows": [
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Tiedoston polku",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "file"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Virallinen selite",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filedescription",
                                                                            "multiline": true
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Epävirallinen selite",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filenotes",
                                                                            "multiline": true
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Kommentti",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filecomment",
                                                                            "multiline": true
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Tyyppi",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filecategory"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "PAS",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "fileaip"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Kieli",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filelanguage"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Alkuperäinen kieli",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "fileoriginal"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "WWW",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filepublication"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Ulosluovutus",
                                                                        "horizontal": true,
                                                                        "field": {
                                                                            "key": "filedip"
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "type": "ROW",
                                                                "cells": [
                                                                    {
                                                                        "type": "CELL",
                                                                        "title": "Tiedostohistoria",
                                                                        "field": {
                                                                            "key": "custom_fileHistory"
                                                                        }
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ],
                                                buttons: [{
                                                    create: function () {
                                                        this
                                                            .text(MetkaJS.L10N.get('general.buttons.ok'))
                                                            .click(function () {
                                                                (function clearErrors(fields) {
                                                                    $.each(fields, function (key, field) {
                                                                        if (field.errors) {
                                                                            field.errors.length = 0
                                                                        }
                                                                        if (field.values) {
                                                                            $.each(field.values, function (lang) {
                                                                                if (lang.errors) {
                                                                                    lang.errors.length = 0
                                                                                }
                                                                            });
                                                                        }
                                                                        if (field.rows) {
                                                                            $.each(field.rows, function (lang, rows) {
                                                                                rows.forEach(function (row) {
                                                                                    if (row.errors) {
                                                                                        row.errors.length = 0
                                                                                    }
                                                                                    clearErrors(row.fields);
                                                                                });
                                                                            });
                                                                        }
                                                                    });
                                                                })(options.data.fields);

                                                                require('./../../server')('save', {
                                                                    data: JSON.stringify(options.data),
                                                                    success: function (response) {
                                                                        require('./../../modal')({
                                                                            title: response.result === 'SAVE_SUCCESSFUL' ? MetkaJS.L10N.get('alert.notice.title') : MetkaJS.L10N.get('alert.error.title'),
                                                                            body: ''/*data.errors.map(function (error) {
                                                                             return MetkaJS.L10N.get(error.msg);
                                                                             })*/,
                                                                            buttons: [{
                                                                                type: 'DISMISS'
                                                                            }]
                                                                        });

                                                                        // TODO: referenceRow -haulla lisää rivi

                                                                        $.extend(options.data, response.data);
                                                                        options.$events.trigger('dataChanged');
                                                                    }
                                                                });
                                                            });
                                                    }
                                                }, {
                                                    type: 'DISMISS'
                                                }]
                                            });

                                            require('./../../modal')(options);
                                        }
                                    });
                                }
                            }
                        });
                    })
            }
        }]
    };
});
