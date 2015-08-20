/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function(require) {
    'use strict';

    var fieldTitles = {
        selectionLists_key: {
            title: "Avain"
        },
        selectionLists_type: {
            title: "Tyyppi"
        },
        selectionLists_default: {
            title: "Vakioarvo"
        },
        selectionLists_includeEmpty: {
            title: "Lisää tyhjä valinta"
        },
        selectionLists_freeText_values: {
            title: "Vapaatekstivalinnat"
        },
        selectionLists_freeText: {
            title: "Vapaatekstiarvo"
        },
        selectionLists_freeTextKey: {
            title: "Vapaatekstikenttä"
        },
        selectionLists_sublistKey: {
            title: "Alilistan avain"
        },
        selectionLists_reference: {
            title: "Viittaus"
        },
        selectionLists_options: {
            title: "Arvot"
        },
        selectionLists_option_value: {
            title: "Arvo"
        },
        selectionLists_option_title_default: {
            title: "Teksti, Suomi"
        },
        selectionLists_option_title_en: {
            title: "Teksti, Englanti"
        },
        selectionLists_option_title_sv: {
            title: "Teksti, Ruotsi"
        },

        reference_key: {
            title: "Viittausavain"
        },
        reference_type: {
            title: "Viittaustyyppi"
        },
        reference_target: {
            title: "Viittauksen kohde"
        },
        reference_valuePath: {
            title: "Polku arvoon"
        },
        reference_titlePath: {
            title: "Polku tekstiin"
        },
        reference_approveOnly: {
            title: "Vain hyväksytyt"
        },
        reference_ignoreRemoved: {
            title: "Ohita poistetut"
        },

        field_key: {
            title: "Kenttäavain"
        },
        field_type: {
            title: "Kenttätyyppi"
        },
        field_translatable: {
            title: "Käännettävyys"
        },
        field_immutable: {
            title: "Muuttumaton"
        },
        field_selectionList: {
            title: "Valintalista"
        },
        field_subfields: {
            title: "Alikentät"
        },
        field_subfield_key: {
            title: "Alikentän avain"
        },
        field_subfield: {
            title: "On alikenttä"
        },
        field_reference: {
            title: "Viitteen avain"
        },
        field_editable: {
            title: "Muokattavissa"
        },
        field_writable: {
            title: "Tallennettavissa"
        },
        field_indexed: {
            title: "Indeksoidaan"
        },
        field_generalSearch: {
            title: "Yleiseen hakuun"
        },
        field_exact: {
            title: "Tarkka haku"
        },
        field_bidirectional: {
            title: "Kaksisuuntainen"
        },
        field_indexName: {
            title: "Indeksinimi"
        },
        field_fixedOrder: {
            title: "Muuttumaton järjestys"
        },
        field_removePermissions: {
            title: "Poisto-oikeudet"
        },
        field_removePermissions_permission: {
            title: "Oikeus"
        },

        operation_type: {
            title: "Operaation tyyppi"
        },
        namedTarget_key: {
            title: "Nimetyn kohteen nimi"
        },
        target_type: {
            title: "Kohteen tyyppi"
        },
        target_content: {
            title: "Kohteen sisältö"
        },
        targets: {
            title: "Kohdelista"
        },
        checks: {
            title: "Kohteen tarkistukset"
        },

        target_check_condition_type: {
            title: "Vaatimuksen tyyppi"
        },
        target_check_condition_target_type: {
            title: "Vaatimuksen kohteen tyyppi"
        },
        target_check_condition_target_content: {
            title: "Vaatimuksen kohteen sisältö"
        }
    };

    var data = require('./data');
    var lang = 'DEFAULT';
    var converter = require('./dataConfigurationEditorConverter');

    function getField(fields, key) {
        if(!fields || !fields[key]) {
            return null;
        }
        return fields[key];
    }

    function getValue(fields, key) {
        var field = getField(fields, key);
        if(!field || field.type !== 'VALUE' || !field.values || !field.values.DEFAULT || !field.values.DEFAULT.current) {
            return;
        }
        return field.values.DEFAULT.current;
    }

    function redraw(options, key) {
        options.$events.trigger('redraw-{key}'.supplant({
            key: key || options.field.key
        }));
    }

    function empty(options, key) {
        options.$events.trigger('data-empty-{key}-{lang}'.supplant({
            key: key || options.field.key,
            lang: 'DEFAULT'
        }))
    }

    function onChange(options, callback, key) {
        options.$events.on('data-changed-{key}-{lang}'.supplant({
            key: key || options.field.key,
            lang: 'DEFAULT'
        }), function(e, value) {
            callback(value);
        });
    }

    function emptyContainer(options) {
        var container = options.data.fields[options.field.key];
        if(!container || !container.rows || !container.rows.DEFAULT) {
            return;
        }

        container.rows.DEFAULT.forEach(function(row) {
            row.removed = true;
        });
    }

    return function(configuration, callback) {

        var modalOptions = require('./optionsBase')();
        modalOptions.large = true;
        modalOptions.dataConf = require('./dataConfigurationEditorDataConfig');
        modalOptions.fieldTitles = fieldTitles;
        modalOptions.buttons = [{
            type: "OK",
            create: function() {
                this.click(function() {
                    callback(converter.toConfiguration(modalOptions));
                })
            }
        }, {
            type: "CANCEL"
        }];
        modalOptions.content = [{
            type: "TAB",
            title: "Perustiedot",
            content: [{
                type: "SECTION",
                defaultState: "OPEN",
                title: "Avain",
                content: [{
                    type: "COLUMN",
                    columns: 2,
                    rows: [{
                        type: "ROW",
                        cells: [{
                            type: "CELL",
                            title: "Type",
                            field: {
                                key: "key_type"
                            }
                        }, {
                            type: "CELL",
                            title: "Version",
                            field: {
                                key: "key_version"
                            }
                        }]
                    }]
                }]
            }, {
                type: "COLUMN",
                columns: 2,
                rows: [{
                    type: "ROW",
                    cells: [{
                        type: "CELL",
                        title: "ID-kenttä",
                        field: {
                            key: "displayId"
                        },
                        preCreate: function(options) {
                            function update() {
                                var list = [];
                                var fields = options.data.fields.fields;
                                if(!fields || !fields.rows || !fields.rows.DEFAULT) {
                                    return;
                                }
                                fields.rows.DEFAULT.forEach(function(row) {
                                    var type = getValue(row.fields, "field_type");
                                    var subfield = getValue(row.fields, "field_subfield");
                                    if(subfield || type === 'CONTAINER' || type === 'REFERENCECONTAINER' || type === 'BOOLEAN') {
                                        return;
                                    }

                                    list.push({value: getValue(row.fields, "field_key")});
                                });

                                options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                redraw(options);
                            }

                            onChange(options, update, 'fields');
                            update();
                        }
                    }]
                }]
            }]
        }, {
            type: "TAB",
            title: "Valintalistat",
            content: [{
                type: "COLUMN",
                rows: [{
                    type: "ROW",
                    cells: [{
                        type: "CELL",
                        title: "Listat",
                        field: {
                            key: "selectionLists",
                            columnFields: [
                                "selectionLists_key",
                                "selectionLists_type"
                            ]
                        },
                        subfieldConfiguration: {
                            selectionLists_freeText_values: {
                                field: {
                                    columnFields: [
                                        "selectionLists_freeText"
                                    ]
                                }
                            },
                            selectionLists_freeTextKey: {
                                preCreate: function(options) {
                                    function update() {
                                        var amnt = data(options)("selectionLists_freeText_values").validRows(lang);
                                        $elem.toggle(amnt > 0);
                                        if(amnt < 1) {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;
                                    update();
                                    onChange(options, update, "selectionLists_freeText_values");

                                    var list = [];
                                    var fields = require('./root')(options).data.fields.fields;
                                    if(!fields || !fields.rows || !fields.rows.DEFAULT) {
                                        return;
                                    }
                                    fields.rows.DEFAULT.forEach(function(row) {
                                        var type = getValue(row.fields, "field_type");
                                        if(type !== 'CONTAINER' && type !== 'REFERENCECONTAINER') {
                                            list.push({value: getValue(row.fields, "field_key")});
                                        }
                                    });

                                    options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                }
                            },
                            selectionLists_sublistKey: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && value === 'SUBLIST');
                                        if(value && value !== 'SUBLIST') {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("selectionLists_type").getByLang("DEFAULT"));

                                    onChange(options, update, "selectionLists_type");

                                    var list = [];
                                    var lists = require('./root')(options).data.fields.selectionLists;
                                    if(!lists || !lists.rows || !lists.rows.DEFAULT) {
                                        return;
                                    }
                                    lists.rows.DEFAULT.forEach(function(row) {
                                        var key = getValue(row.fields, "selectionLists_key");
                                        if(key != getValue(options.data.fields, "selectionLists_key")) {
                                            list.push({value: key});
                                        }
                                    });

                                    options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                }
                            },
                            selectionLists_reference: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && value === 'REFERENCE');
                                        if(value && value !== 'REFERENCE') {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("selectionLists_type").getByLang("DEFAULT"));

                                    onChange(options, update, "selectionLists_type");

                                    var list = [];
                                    var references = require('./root')(options).data.fields.references;
                                    if(!references || !references.rows || !references.rows.DEFAULT) {
                                        return;
                                    }
                                    references.rows.DEFAULT.forEach(function(row) {
                                        list.push({value: getValue(row.fields, "reference_key")});
                                    });

                                    options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                }
                            },
                            selectionLists_options: {
                                field: {
                                    columnFields: [
                                        "selectionLists_option_value",
                                        "selectionLists_option_title_default"
                                    ]
                                },
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && (value === 'VALUE' || value === 'LITERAL'));
                                        if(value && !(value === 'VALUE' || value === 'LITERAL')) {
                                            emptyContainer(options);
                                            redraw(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("selectionLists_type").getByLang("DEFAULT"));

                                    onChange(options, update, "selectionLists_type")
                                }
                            }
                        }
                    }]
                }]
            }]
        }, {
            type: "TAB",
            title: "Viittaukset",
            content: [{
                type: "COLUMN",
                rows: [{
                    type: "ROW",
                    cells: [{
                        type: "CELL",
                        title: "Viittaukset",
                        field: {
                            key: "references",
                            columnFields: [
                                "reference_key",
                                "reference_type",
                                "reference_target"
                            ]
                        },
                        subfieldConfiguration: {
                            reference_target: {
                                preCreate: function(options) {
                                    function update(value) {
                                        // Always visible but content changes based on type, clear the selection when type changes
                                        var list = [];
                                        switch(value) {
                                            case "REVISIONABLE":
                                            case "REVISION": {
                                                require('./server')("/settings/getJsonList/DATA_CONF", {
                                                    type: "GET",
                                                    async: false,
                                                    success: function (response) {
                                                        var types = {};
                                                        response.map(function(entry) {
                                                            types[entry.title.split(".")[0]] = null;
                                                        });
                                                        Object.keys(types).map(function(key) {
                                                            list.push({value: key});
                                                        });
                                                    }
                                                });
                                                break;
                                            }
                                            case "JSON": {
                                                require('./server')("/settings/getJsonList/MISC", {
                                                    type: "GET",
                                                    async: false,
                                                    success: function (response) {
                                                        response.map(function(entry) {
                                                            list.push({value: entry.title});
                                                        });
                                                    }
                                                });
                                                break;
                                            }
                                            case "DEPENDENCY": {

                                                var fields = require('./root')(options).data.fields.fields;
                                                if(!fields || !fields.rows || !fields.rows.DEFAULT) {
                                                    return;
                                                }
                                                fields.rows.DEFAULT.forEach(function(row) {
                                                    if(getValue(row.fields, "field_type") !== 'CONTAINER') {
                                                        list.push({value: getValue(row.fields, "field_key")});
                                                    }
                                                });
                                                break;
                                            }
                                        }

                                        options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;

                                        empty(options);
                                        redraw(options);
                                    }
                                    var $elem = this;

                                    update(data(options)("reference_type").getByLang("DEFAULT"));

                                    onChange(options, update, "reference_type");
                                }
                            },
                            reference_valuePath: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && !(value === 'REVISIONABLE' || value === 'REVISION'));
                                        if(value && (value === 'REVISIONABLE' || value === 'REVISION')) {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("reference_type").getByLang("DEFAULT"));

                                    onChange(options, update, "reference_type");
                                }
                            },
                            reference_approveOnly: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && (value === 'REVISIONABLE' || value === 'REVISION'));
                                        if(value && !(value === 'REVISIONABLE' || value === 'REVISION')) {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("reference_type").getByLang("DEFAULT"));

                                    onChange(options, update, "reference_type");
                                }
                            },
                            reference_ignoreRemoved: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && (value === 'REVISIONABLE' || value === 'REVISION'));
                                        if(value && !(value === 'REVISIONABLE' || value === 'REVISION')) {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("reference_type").getByLang("DEFAULT"));

                                    onChange(options, update, "reference_type");
                                }
                            }
                        }
                    }]
                }]
            }]
        }, {
            type: "TAB",
            title: "Kentät",
            content: [{
                type: "COLUMN",
                rows: [{
                    type: "ROW",
                    cells: [{
                        type: "CELL",
                        title: "Kentät",
                        field: {
                            key: "fields",
                            columnFields: [
                                "field_key",
                                "field_type"
                            ]
                        },
                        subfieldConfiguration: {
                            field_removePermissions: {
                                field: {
                                    columnFields: [
                                        "field_removePermissions_permission"
                                    ]
                                }
                            },
                            field_translatable: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && value !== 'REFERENCECONTAINER');
                                        if(value && value !== 'REFERENCECONTAINER') {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("field_type").getByLang("DEFAULT"));

                                    onChange(options, update, "field_type");
                                }
                            },
                            field_selectionList: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && value === 'SELECTION');
                                        if(value && value !== 'SELECTION') {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("field_type").getByLang("DEFAULT"));

                                    onChange(options, update, "field_type");

                                    var list = [];
                                    var lists = require('./root')(options).data.fields.selectionLists;
                                    if(!lists || !lists.rows || !lists.rows.DEFAULT) {
                                        return;
                                    }
                                    lists.rows.DEFAULT.forEach(function(row) {
                                        list.push({value: getValue(row.fields, "selectionLists_key")});
                                    });

                                    options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                }
                            },
                            field_subfields: {
                                field: {
                                    columnFields: [
                                        "field_subfield_key"
                                    ]
                                },
                                preCreate: function(options) {
                                    var list = [];
                                    var fields = require('./root')(options).data.fields.fields;
                                    if(!fields || !fields.rows || !fields.rows.DEFAULT) {
                                        return;
                                    }
                                    fields.rows.DEFAULT.forEach(function(row) {
                                        if(getValue(row.fields, "field_subfield")) {
                                            list.push({value: getValue(row.fields, "field_key")});
                                        }
                                    });

                                    options.dataConf.selectionLists.field_subfield_key_list.options = list;

                                    function update(value) {
                                        $elem.toggle(value && (value === 'CONTAINER' || value === 'REFERENCECONTAINER'));
                                        if(value && !(value === 'CONTAINER' || value === 'REFERENCECONTAINER')) {
                                            emptyContainer(options);
                                            redraw(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("field_type").getByLang("DEFAULT"));

                                    onChange(options, update, "field_type");
                                }
                            },
                            field_reference: {
                                preCreate: function(options) {
                                    function update(value) {
                                        $elem.toggle(value && (value === 'REFERENCE' || value === 'REFERENCECONTAINER'));
                                        if(value && !(value === 'REFERENCE' || value === 'REFERENCECONTAINER')) {
                                            empty(options);
                                        }
                                    }
                                    var $elem = this;

                                    update(data(options)("field_type").getByLang("DEFAULT"));

                                    onChange(options, update, "field_type");

                                    var list = [];
                                    var references = require('./root')(options).data.fields.references;
                                    if(!references || !references.rows || !references.rows.DEFAULT) {
                                        return;
                                    }
                                    references.rows.DEFAULT.forEach(function(row) {
                                        list.push({value: getValue(row.fields, "reference_key")});
                                    });

                                    options.dataConf.selectionLists[options.fieldOptions.selectionList].options = list;
                                }
                            }
                        }
                    }]
                }]
            }]
        }, {
            type: "TAB",
            title: "Rajoitteet",
            content: [{
                type: "SECTION",
                title: "Nimetyt rajoitteet",
                defaultState: "OPEN",
                content: [{
                    type: "COLUMN",
                    rows: [{
                        type: "ROW",
                        cells: [{
                            type: "CELL",
                            title: "Nimetyt rajoitteet",
                            field: {
                                key: "namedTargets",
                                columnFields: [
                                    "namedTarget_key",
                                    "target_type",
                                    "target_content"
                                ]
                            }
                        }]
                    }]
                }]
            }, {
                type: "SECTION",
                title: "Rajoitteet",
                defaultState: "OPEN",
                content: [{
                    type: "COLUMN",
                    rows: [{
                        type: "ROW",
                        cells: [{
                            type: "CELL",
                            title: "Operaatiot",
                            field: {
                                key: "restrictions",
                                columnFields: [
                                    "operation_type"
                                ]
                            }
                        }]
                    }]
                }]
            }]
        }, {
            type: "TAB",
            title: "Sarjoitukset",
            content: [{
                type: "COLUMN",
                rows: [{
                    type: "ROW",
                    cells: [{
                        type: "CELL",
                        title: "Operaatiot",
                        field: {
                            key: "cascade",
                            columnFields: [
                                "operation_type"
                            ]
                        }
                    }]
                }]
            }]
        }];

        modalOptions.subfieldConfiguration = {
            targets: {
                field: {
                    columnFields: [
                        "target_type",
                        "target_content"
                    ]
                }
            },
            checks: {
                field: {
                    columnFields: [
                        "target_check_condition_type",
                        "target_check_condition_target_type",
                        "target_check_condition_target_content"
                    ]
                },
                subfieldConfiguration: {
                    targets: {
                        title: "Vaatimusta rajoittavat kohteet"
                    }
                }
            },

            selectionLists_key: {
                horizontal: true
            },
            selectionLists_type: {
                horizontal: true
            },
            selectionLists_default: {
                horizontal: true
            },
            selectionLists_includeEmpty: {
                horizontal: true
            },
            selectionLists_freeTextKey: {
                horizontal: true
            },
            selectionLists_sublistKey: {
                horizontal: true
            },
            selectionLists_reference: {
                horizontal: true
            },
            selectionLists_option_value: {
                horizontal: true
            },
            selectionLists_option_title_default: {
                horizontal: true
            },
            selectionLists_option_title_en: {
                horizontal: true
            },
            selectionLists_option_title_sv: {
                horizontal: true
            },
            reference_key: {
                horizontal: true
            },
            reference_type: {
                horizontal: true
            },
            reference_target: {
                horizontal: true
            },
            reference_valuePath: {
                horizontal: true
            },
            reference_titlePath: {
                horizontal: true
            },
            reference_approveOnly: {
                horizontal: true
            },
            reference_ignoreRemoved: {
                horizontal: true
            },
            field_key: {
                horizontal: true
            },
            field_type: {
                horizontal: true
            },
            field_translatable: {
                horizontal: true
            },
            field_immutable: {
                horizontal: true
            },
            //"field_maxValues", // maxValues is not implemented at the moment
            field_selectionList: {
                horizontal: true
            },
            field_subfield: {
                horizontal: true
            },
            field_reference: {
                horizontal: true
            },
            field_editable: {
                horizontal: true
            },
            field_writable: {
                horizontal: true
            },
            field_indexed: {
                horizontal: true
            },
            field_generalSearch: {
                horizontal: true
            },
            field_exact: {
                horizontal: true
            },
            field_bidirectional: {
                horizontal: true
            },
            field_indexName: {
                horizontal: true
            },
            field_fixedOrder: {
                horizontal: true
            },
            field_subfield_key: {
                horizontal: true
            },
            field_removePermissions_permission: {
                horizontal: true
            },
            target_type: {
                horizontal: true
            },
            target_content: {
                horizontal: true
            },
            target_check_condition_type: {
                horizontal: true
            },
            target_check_condition_target_type: {
                horizontal: true
            },
            target_check_condition_target_content: {
                horizontal: true
            },
            namedTarget_key: {
                horizontal: true
            },
            operation_type: {
                horizontal: true
            },
            /*reference_key: {
                horizontal: true
            }*/
        };

        converter.toEditor(configuration, modalOptions);

        require('./modal')(modalOptions);
    }
});