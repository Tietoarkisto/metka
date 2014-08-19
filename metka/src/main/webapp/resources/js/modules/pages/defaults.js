define(function (require) {
    return function (onLoad) {
        var metka = require('./../../metka');
        require('./../server')('/revision/ajax/view/{page}/{id}/{no}', {
            method: 'GET',
            success: function (data) {
                if (location.pathname.split('/').indexOf('publication') !== -1) {
                    data = {
                        gui: {
                            "key": {
                                "type": "PUBLICATION",
                                "version": 1
                            },
                            "content": [
                                {
                                    "type": "COLUMN",
                                    "columns": 3,
                                    "rows": [
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisuvuosi",
                                                    "field": {
                                                        "key": "publicationyear"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Voiko julkaista",
                                                    "field": {
                                                        "key": "publicationpublic"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisu id-nro",
                                                    "field": {
                                                        "key": "publicationid"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "1. Tallennuspvm",
                                                    "field": {
                                                        "key": "publicationfirstsaved"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun ilmoitustapa",
                                                    "field": {
                                                        "key": "publicationannouncement"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun kieli",
                                                    "field": {
                                                        "key": "publicationlanguage"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Viimeisin muutospvm",
                                                    "field": {
                                                        "key": "savedAt"
                                                    }
                                                },
                                                {
                                                    "type": "CELL",
                                                    "title": "Käsittelijä",
                                                    "field": {
                                                        "key": "savedBy"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    "type": "COLUMN",
                                    "columns": 1,
                                    "rows": [
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Julkaisun otsikko",
                                                    "field": {
                                                        "key": "publicationtitle",
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
                                                    "title": "relPubl-tiedot",
                                                    "field": {
                                                        "key": "publicationrelpubl",
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
                                                    "title": "Julkaisun huomautukset",
                                                    "field": {
                                                        "key": "publicationnotes",
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
                                                    "title": "Liittyvät henkilöt",
                                                    "field": {
                                                        "key": "publicationauthors"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Pysyvät tunnisteet",
                                                    "field": {
                                                        "key": "publicationpids"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Liittyvät aineistot",
                                                    "field": {
                                                        "key": "studies"
                                                    }
                                                }
                                            ]
                                        },
                                        {
                                            "type": "ROW",
                                            "cells": [
                                                {
                                                    "type": "CELL",
                                                    "title": "Liittyvät sarjat",
                                                    "field": {
                                                        "key": "series"
                                                    }
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ],
                            "buttons": [
                                {
                                    "title": "Tallenna",
                                    "type": "SAVE",
                                    "states": [
                                        "DRAFT"
                                    ]
                                },
                                {
                                    "title": "Hyväksy",
                                    "type": "APPROVE",
                                    "states": [
                                        "DRAFT"
                                    ]
                                },
                                {
                                    "title": "Muokkaa",
                                    "type": "EDIT",
                                    "states": [
                                        "APPROVED"
                                    ]
                                },
                                {
                                    "title": "Poista",
                                    "type": "REMOVE",
                                    "states": [
                                        "DRAFT",
                                        "APPROVED"
                                    ]
                                },
                                {
                                    "title": "Revisiohistoria",
                                    "type": "HISTORY"
                                }
                            ]
                        },
                        transferData: {
                            state: {
                                draft: true
                            }
                        },
                        configuration: {
                            "key": {
                                "version": 1,
                                "type": "PUBLICATION"
                            },
                            "selectionLists": {
                                "yes_no": {
                                    "key": "yes_no",
                                    "type": "VALUE",
                                    "options": [
                                        {
                                            "&title": {
                                                "default": "Kyllä"
                                            },
                                            "value": 1
                                        },
                                        {
                                            "&title": {
                                                "default": "Ei"
                                            },
                                            "value": 0
                                        }
                                    ]
                                },
                                "langs": {
                                    "key": "langs",
                                    "type": "VALUE",
                                    "options": [
                                        {
                                            "&title": {
                                                "default": "Suomi"
                                            },
                                            "value": 'fi'
                                        },
                                        {
                                            "&title": {
                                                "default": "Englanti"
                                            },
                                            "value": 'en'
                                        },
                                        {
                                            "&title": {
                                                "default": "Ruotsi"
                                            },
                                            "value": 'sv'
                                        },
                                        {
                                            "&title": {
                                                "default": "Muu"
                                            },
                                            "value": 'other'
                                        }
                                    ]
                                },
                                "publicationannouncement_list": {
                                    "key": "publicationannouncement_list",
                                    "type": "VALUE",
                                    "options": [
                                        {
                                            "&title": {
                                                "default": "Ei tietoa"
                                            },
                                            "value": 0
                                        },
                                        {
                                            "&title": {
                                                "default": "Alkup. tutkija ilmoit."
                                            },
                                            "value": 1
                                        },
                                        {
                                            "&title": {
                                                "default": "Oma paikannus"
                                            },
                                            "value": 2
                                        },
                                        {
                                            "&title": {
                                                "default": "käyttäjä ilmoit."
                                            },
                                            "value": 3
                                        }
                                    ]
                                }
                            },
                            "fields": {
                                "publicationyear": {
                                    "key": "publicationyear",
                                    "translatable": false,
                                    "required": true,
                                    "type": "INTEGER"
                                },
                                "publicationpublic": {
                                    "key": "publicationpublic",
                                    "translatable": false,
                                    "type": "SELECTION",
                                    "required": true,
                                    "selectionList": "yes_no"
                                },
                                "publicationid": {
                                    "key": "publicationid",
                                    "translatable": false,
                                    "required": true,
                                    "type": "INTEGER"
                                },
                                "publicationfirstsaved": {
                                    "_comment": "This can be computed from the revision history and might not be required as part of the datamodel.",
                                    "key": "publicationfirstsaved",
                                    "required": true,
                                    "type": "DATE"
                                },
                                "publicationannouncement": {
                                    "key": "publicationannouncement",
                                    "translatable": false,
                                    "required": true,
                                    "type": "SELECTION",
                                    "selectionList": "publicationannouncement_list"
                                },
                                "publicationlanguage": {
                                    "key": "publicationlanguage",
                                    "translatable": false,
                                    "required": true,
                                    "type": "SELECTION",
                                    "selectionList": "langs"
                                },
                                "publicationtitle": {
                                    "key": "publicationtitle",
                                    "required": true,
                                    "type": "STRING"
                                },
                                "publicationrelpubl": {
                                    "key": "publicationrelpubl",
                                    "required": true,
                                    "type": "STRING"
                                },
                                "publicationnotes": {
                                    "key": "publicationnotes",
                                    "type": "STRING"
                                },
                                "publicationauthors": {
                                    "key": "publicationauthors",
                                    "translatable": false,
                                    "type": "CONTAINER",
                                    "subfields": [
                                        "firstname",
                                        "lastname"
                                    ]
                                },
                                "firstname": {
                                    "key": "firstname",
                                    "type": "STRING",
                                    "subfield": true,
                                    "summaryField": true
                                },
                                "lastname": {
                                    "key": "lastname",
                                    "type": "STRING",
                                    "subfield": true,
                                    "summaryField": true
                                },
                                "publicationpids": {
                                    "key": "publicationpids",
                                    "type": "CONTAINER",
                                    "subfields": [
                                        "pid",
                                        "pidtype"
                                    ]
                                },
                                "pid": {
                                    "key": "pid",
                                    "type": "STRING",
                                    "subfield": true,
                                    "summaryField": true
                                },
                                "pidtype": {
                                    "_comment": "Derived is an old type and not supported, changing to STRING for the moment.",
                                    "key": "pidtype",
                                    "type": "STRING",
                                    "subfield": true,
                                    "summaryField": true
                                },
                                "studies": {
                                    "key": "studies",
                                    "type": "REFERENCE"
                                },
                                "series": {
                                    "key": "series",
                                    "type": "REFERENCE"
                                },
                                "savedAt": {
                                    "_comment": "This can be computed from the revision history and might not be required as part of the datamodel.",
                                    "key": "savedAt",
                                    "required": true,
                                    "type": "DATE"
                                },
                                "savedBy": {
                                    "_comment": "This can be computed from the revision history and might not be required as part of the datamodel.",
                                    "key": "savedBy",
                                    "required": true,
                                    "type": "REFERENCE"
                                }
                            }
                        }
                    };
                }
                var options = data.gui;
                options.readOnly = !data.transferData.state.draft;

                options.dataConf = data.configuration;
                options.data = data.transferData;

                options.header = function header($header) {
                    var supplant = {
                        page: metka.PAGE
                    };

                    var header = {
                        localized: 'type.{page}.title',
                        pattern: '{localized} - {id} - {revision}{state}',
                        buttons: $('<div class="pull-right normalText">')
                            .append((function () {
                                var buttonCreateFunctions = metka.state === 'DRAFT' ? [] : [function () {
                                    $(this)
                                        .prop('disabled', true)
                                        .addClass('btn-xs')
                                        .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                                        .click(function () {
                                            require('./../assignUrl')('prev');
                                        });
                                }, function () {
                                    $(this)
                                        .prop('disabled', true)
                                        .addClass('btn-xs')
                                        .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                                        .click(function () {
                                            require('./../assignUrl')('next');
                                        });
                                }];
                                buttonCreateFunctions.push(function () {
                                    $(this)
                                        .prop('disabled', true)
                                        .addClass('btn-xs')
                                        .text(MetkaJS.L10N.get('general.buttons.download'))
                                        .click(function () {
                                            require('./../assignUrl')('download');
                                        });
                                });
                                return buttonCreateFunctions;
                            })().map(function (create) {
                                return require('./../button')()({
                                    create: create,
                                    style: 'default'
                                });
                            }))
                    };
                    var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
                    supplant.id = labelAndValue({
                        label: MetkaJS.L10N.get('general.id'),
                        value: metka.id
                    });
                    supplant.revision = labelAndValue({
                        label: MetkaJS.L10N.get('general.revision'),
                        value: metka.revision
                    });
                    supplant.state = metka.state === 'DRAFT' ? ' - ' + MetkaJS.L10N.get('general.DRAFT') : '';

                    supplant.localized = MetkaJS.L10N.get(header.localized.supplant(supplant));
                    $header.html(header.pattern.supplant(supplant));

                    if (header.buttons) {
                        $header.append(header.buttons);
                    }

                    return $header;
                };

                onLoad(options);
            }
        });
    };
});
