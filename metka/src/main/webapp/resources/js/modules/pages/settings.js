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

define(function (require) {
    'use strict';

    return function (options, onLoad) {


        var $editor;

        $.extend(options, {
            data: {},
            dataConf: $.extend(true, {}, {
                selectionLists: {
                    dataConfigType_list: {
                        key: "dataConfigType_list",
                        type: "VALUE",
                        includeEmpty: true,
                        options: []
                    },
                    dataConfigEditorType_list: {
                        key: "dataConfigEditorType_list",
                        type: "VALUE",
                        includeEmpty: true,
                        options: []
                    },
                    guiConfigType_list: {
                        key: "guiConfigType_list",
                        type: "VALUE",
                        includeEmpty: true,
                        options: []
                    },
                    jsonKeys_list: {
                        key: "jsonKeys_list",
                        type: "VALUE",
                        includeEmpty: true,
                        options: []
                    },
                    addapiuserrole_list: {
                        key: "addapiuserrole_list",
                        type: "VALUE",
                        options: [{
                            value: "metka:unknown",
                            title: "Tuntematon"
                        }, {
                            value: "metka:reader",
                            title: "Lukija"
                        }, {
                            value: "metka:user",
                            title: "Käyttäjä"
                        }, {
                            value: "metka:translator",
                            title: "Kääntäjä"
                        }, {
                            value: "metka:data-admin",
                            title: "Data admin"
                        }, {
                            value: "metka:admin",
                            title: "Admin"
                        }]
                    }
                },
                references: {
                },
                fields: {
                    indexers: {
                        key: "indexers",
                        type: "CONTAINER",
                        subfields: [
                            "indexPath",
                            "indexIsRunning"
                        ]
                    },
                    indexPath: {
                        key: "indexPath",
                        type: "STRING"
                    },
                    indexIsRunning: {
                        key: "indexIsRunning",
                        type: "STRING"
                    },
                    dataConfigTypes: {
                        key: "dataConfigTypes",
                        type: "SELECTION",
                        selectionList: "dataConfigType_list"
                    },
                    dataConfigEditorTypes: {
                        key: "dataConfigEditorTypes",
                        type: "SELECTION",
                        selectionList: "dataConfigEditorType_list"
                    },
                    dataConfigText: {
                        key: "dataConfigText",
                        type: "STRING"
                    },
                    guiConfigTypes: {
                        key: "guiConfigTypes",
                        type: "SELECTION",
                        selectionList: "guiConfigType_list"
                    },
                    guiConfigText: {
                        key: "guiConfigText",
                        type: "STRING"
                    },
                    jsonKeys: {
                        key: "jsonKeys",
                        type: "SELECTION",
                        selectionList: "jsonKeys_list"
                    },
                    jsonText: {
                        key: "jsonText",
                        type: "STRING"
                    },
                    apiusers: {
                        key: "apiusers",
                        type: "CONTAINER",
                        subfields: [
                            "apiusersname",
                            "apiusersusername",
                            "apiusersrole",
                            "apiuserssecret",
                            "apiusersaccess",
                            "apiuserscreated"
                        ]
                    },
                    apiusersname: {
                        key: "apiusersname",
                        type: "STRING",
                        subfield: true
                    },
                    apiusersusername: {
                        key: "apiuserssuername",
                        type: "STRING",
                        subfield: true
                    },
                    apiusersrole: {
                        key: "apiusersrole",
                        type: "STRING",
                        subfield: true
                    },
                    apiuserssecret: {
                        key: "apiuserssecret",
                        type: "STRING",
                        subfield: true
                    },
                    apiusersaccess: {
                        key: "apiusersaccess",
                        type: "DATETIME",
                        subfield: true
                    },
                    apiuserscreated: {
                        key: "apiuserscreated",
                        type: "STRING",
                        subfield: true
                    },
                    addapiuserusername: {
                        key: "addapiuserusername",
                        type: "STRING"
                    },
                    addapiusername: {
                        key: "addapiusername",
                        type: "STRING"
                    },
                    addapiuserrole: {
                        key: "addapiuserrole",
                        type: "SELECTION",
                        selectionList: "addapiuserrole_list"
                    }/*,
                    addapiuserreadpermission: {
                        key: "addapiuserreadpermission",
                        type: "BOOLEAN"
                    }*/
                }
            }),
            fieldTitles: {
                "indexPath": {
                    key: "indexPath",
                    title: "Indekserin polku"
                },
                "indexIsRunning": {
                    key: "indexIsRunning",
                    title: "Tila"
                },
                apiusersname: {
                    key: "apiusersname",
                    title: "Käyttäjä"
                },
                apiusersusername: {
                    key: "apiusersusername",
                    title: "Käyttäjänimi"
                },
                apiusersrole: {
                    key: "apiusersrole",
                    title: "Rooli"
                },
                apiuserssecret: {
                    key: "apiuserssecret",
                    title: "Avain"
                },
                apiusersaccess: {
                    key: "apiusersaccess",
                    title: "Viimeksi kirjautunut"
                },
                apiuserscreated: {
                    key: "apiuserscreated",
                    title: "Luoja"
                }
            },
            content: [
                {
                    type: "TAB",
                    title: "Raportit",
                    content: [
                        {
                            type: "COLUMN",
                            columns: 1,
                            rows: [
                                {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            button: {
                                                title: "Lataa raportti",
                                                create: function() {
                                                    this.click(function() {
                                                        require('./../assignUrl')('/settings/downloadReport');
                                                    });
                                                }
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    type: "TAB",
                    title: "Indeksointi",
                    // TODO: Fetch index info and show
                    permissions: [
                        "canViewIndexInfo"
                    ],
                    content: [
                        {
                            type: "COLUMN",
                            columns: 1,
                            rows: [
                                {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            field: {
                                                displayType: "CUSTOM_JS"
                                            },
                                            postCreate: function(options) {
                                                var $elem = $(this).children().first();
                                                $elem.text("Indeksikomentoja jonossa: 0");
                                                setInterval(function() {
                                                    require("./../server")("/settings/openIndexCommands", {
                                                        method: "GET",
                                                        success: function(response) {
                                                            $elem.text("Indeksikomentoja jonossa: "+response.openCommands);
                                                        }
                                                    })
                                                }, 1000);
                                            }
                                        }
                                    ]
                                },
                                {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            button: {
                                                title: "Uudelleenindeksoi kaikki",
                                                permissions: [
                                                    "canManuallyIndexContent"
                                                ],
                                                create: function(options) {
                                                    this.click(function() {
                                                        require('./../server')('/settings/indexEverything', {
                                                            method: "GET",
                                                            success: function() {
                                                            }
                                                        });

                                                    });
                                                }
                                            }

                                        }
                                    ]
                                },
                                {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            button: {
                                                title: "Sammuta indekserit",
                                                permissions: [
                                                    "canManuallyIndexContent"
                                                ],
                                                create: function(options) {
                                                    this.click(function() {
                                                        require('./../server')('/settings/stopIndexers', {
                                                            method: "GET",
                                                            success: function() {
                                                            }
                                                        });

                                                    });
                                                }
                                            }

                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    type: "TAB",
                    title: "Konfiguraatiotiedostot",
                    permissions: [
                        "canUploadConfigurations",
                        "canUploadJson"
                    ],
                    content: [{
                        type: "SECTION",
                        title: "Data konfiguraatiot",
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "Tyypit",
                                    horizontal: true,
                                    field: {
                                        key: "dataConfigTypes"
                                    },
                                    preCreate: function(options) {
                                        require('./../server')("/settings/getJsonList/DATA_CONF", {
                                            type: "GET",
                                            async: false,
                                            success: function (response) {
                                                response.map(function(entry) {
                                                    options.dataConf.selectionLists.dataConfigType_list.options.push({
                                                        value: JSON.stringify(entry),
                                                        title: entry.title
                                                    });
                                                });
                                            }
                                        });
                                    }
                                }, {
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    button: {
                                        title: "Lataa",
                                        create: function(options) {
                                            this.click(function() {
                                                var data = require('./../data')(options)("dataConfigTypes").getByLang("DEFAULT");
                                                if(data) {
                                                    require('./../server')("/settings/getJsonContent", {
                                                        data: data,
                                                        success: function (response) {
                                                            require('./../data')(options)("dataConfigText").setByLang("DEFAULT", JSON.stringify(JSON.parse(response), null, 4));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "Konfiguraatio",
                                    horizontal: true,
                                    colspan: 2,
                                    field: {
                                        key: "dataConfigText",
                                        multiline: true
                                    },
                                    postCreate: function() {
                                        this.find("textarea").first().attr("id", "dataConfigTextField").prop("rows", 20);
                                    }

                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    colspan: 2,
                                    button: {
                                        title: "Tallenna",
                                        create: function() {
                                            this.click(function() {
                                                var request = {
                                                    type: "DATA_CONF",
                                                    json: require('./../data')(options)("dataConfigText").getByLang("DEFAULT")
                                                };
                                                require('./../server')("/settings/uploadJson", {
                                                    data: JSON.stringify(request),
                                                    success: function (response) {
                                                        require('./../resultViewer')(response);
                                                    }
                                                });
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }, {
                        type: "SECTION",
                        title: "GUI konfiguraatiot",
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "Tyypit",
                                    horizontal: true,
                                    field: {
                                        key: "guiConfigTypes"
                                    },
                                    preCreate: function(options) {
                                        require('./../server')("/settings/getJsonList/GUI_CONF", {
                                            type: "GET",
                                            async: false,
                                            success: function (response) {
                                                response.map(function(entry) {
                                                    options.dataConf.selectionLists.guiConfigType_list.options.push({
                                                        value: JSON.stringify(entry),
                                                        title: entry.title
                                                    });
                                                });
                                            }
                                        });
                                    }
                                }, {
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    button: {
                                        title: "Lataa",
                                        create: function(options) {
                                            this.click(function() {
                                                var data = require('./../data')(options)("guiConfigTypes").getByLang("DEFAULT");
                                                if(data) {
                                                    require('./../server')("/settings/getJsonContent", {
                                                        data: data,
                                                        success: function (response) {
                                                            require('./../data')(options)("guiConfigText").setByLang("DEFAULT", JSON.stringify(JSON.parse(response), null, 4));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "Konfiguraatio",
                                    horizontal: true,
                                    colspan: 2,
                                    field: {
                                        key: "guiConfigText",
                                        multiline: true
                                    },
                                    postCreate: function() {
                                        this.find("textarea").first().attr("id", "guiConfigTextField").prop("rows", 20);
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    colspan: 2,
                                    button: {
                                        title: "Tallenna",
                                        create: function() {
                                            this.click(function() {
                                                var request = {
                                                    type: "GUI_CONF",
                                                    json: require('./../data')(options)("guiConfigText").getByLang("DEFAULT")
                                                };
                                                require('./../server')("/settings/uploadJson", {
                                                    data: JSON.stringify(request),
                                                    success: function (response) {
                                                        require('./../resultViewer')(response);
                                                    }
                                                });
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }, {
                        type: "SECTION",
                        title: "JSON",
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "Avaimet",
                                    horizontal: true,
                                    field: {
                                        key: "jsonKeys"
                                    },
                                    preCreate: function(options) {
                                        require('./../server')("/settings/getJsonList/MISC", {
                                            type: "GET",
                                            async: false,
                                            success: function (response) {
                                                response.map(function(entry) {
                                                    options.dataConf.selectionLists.jsonKeys_list.options.push({
                                                        value: JSON.stringify(entry),
                                                        title: entry.title
                                                    });
                                                });
                                            }
                                        });
                                    }
                                }, {
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    button: {
                                        title: "Lataa",
                                        create: function(options) {
                                            this.click(function() {
                                                var data = require('./../data')(options)("jsonKeys").getByLang("DEFAULT");
                                                if(data) {
                                                    require('./../server')("/settings/getJsonContent", {
                                                        data: data,
                                                        success: function (response) {
                                                            require('./../data')(options)("jsonText").setByLang("DEFAULT", JSON.stringify(JSON.parse(response), null, 4));
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: "JSON",
                                    horizontal: true,
                                    colspan: 2,
                                    field: {
                                        key: "jsonText",
                                        multiline: true
                                    },
                                    postCreate: function() {
                                        this.find("textarea").first().attr("id", "jsonTextField").prop("rows", 20);
                                    }

                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    colspan: 2,
                                    button: {
                                        title: "Tallenna",
                                        create: function() {
                                            this.click(function() {
                                                var request = {
                                                    type: "MISC",
                                                    json: require('./../data')(options)("jsonText").getByLang("DEFAULT")
                                                };
                                                require('./../server')("/settings/uploadJson", {
                                                    data: JSON.stringify(request),
                                                    success: function (response) {
                                                        require('./../resultViewer')(response);
                                                    }
                                                });
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }]
                }, {
                    type: "TAB",
                    title: "Konfiguraatioeditori",
                    permissions: [
                        "canUploadConfigurations",
                        "canUploadJson"
                    ],
                    content: [
                        {
                            type: "COLUMN",
                            columns: 2,
                            rows: [
                                {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            title: "Tyypit",
                                            horizontal: true,
                                            field: {
                                                key: "dataConfigEditorTypes"
                                            },
                                            preCreate: function(options) {
                                                require('./../server')("/settings/getJsonList/DATA_CONF", {
                                                    type: "GET",
                                                    async: false,
                                                    success: function (response) {
                                                        response.map(function(entry) {
                                                            options.dataConf.selectionLists.dataConfigEditorType_list.options.push({
                                                                value: JSON.stringify(entry),
                                                                title: entry.title
                                                            });
                                                        });
                                                    }
                                                });
                                            }
                                        },
                                        {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            button: {
                                                title: "Lataa",
                                                create: function() {
                                                    this.click(function() {
                                                        var data = require('./../data')(options)("dataConfigEditorTypes").getByLang("DEFAULT");
                                                        if (data) {
                                                            require('./../preloader')($editor);
                                                            require('./../server')("/settings/getJsonContent", {
                                                                data: data,
                                                                success: function (response) {
                                                                    JSONEditor.defaults.editors.object.options.collapsed = true;
                                                                    var editor = $editor.empty().data('jsoneditor');
                                                                    if (editor && editor.destroy) {
                                                                        editor.destroy();
                                                                    }
                                                                    $editor.jsoneditor({
                                                                        "disable_edit_json": true,
                                                                        "schema": {
                                                                            "title": "Datan konfiguraatio",
                                                                            "options": {
                                                                                "disable_properties": true,
                                                                                "disable_collapse": false
                                                                            },
                                                                            "type": "object",
                                                                            "properties": {
                                                                                "_help": {
                                                                                    "title": "Ohjeet",
                                                                                    "readOnly": true,
                                                                                    "type": "object",
                                                                                    "options": {
                                                                                        "disable_edit_json": true,
                                                                                        "disable_properties": true
                                                                                    },
                                                                                    "properties": {
                                                                                        "Kenttäavain": {
                                                                                            "type": "string",
                                                                                            "readOnly": true,
                                                                                            "default": "`fields`-osiosta loytyvän objektin `key`-ominaisuuden arvo."
                                                                                        },
                                                                                        "Rajoitteet/pakotteet": {
                                                                                            "type": "string",
                                                                                            "readOnly": true,
                                                                                            "default": "Tietosisällön vaatimuksia, esim. pakolliset täytettävät kentät."
                                                                                        },
                                                                                        "Tietokenttä": {
                                                                                            "type": "string",
                                                                                            "readOnly": true,
                                                                                            "default": "`fields`-osiosta löytyvän määrityksen perusteella tallennettu tieto."
                                                                                        },
                                                                                        "Käännösteksti": {
                                                                                            "type": "string",
                                                                                            "readOnly": true,
                                                                                            "default": "Objekti joka sisältää tekstit ohjelmiston tukemille kielille, joista suomenkielinen teksti on pakollinen."
                                                                                        }
                                                                                    },
                                                                                    "required": ["Kenttäavain", "Rajoitteet/pakotteet", "Tietokenttä", "Käännösteksti"]
                                                                                },
                                                                                "key": {
                                                                                    "type": "object",
                                                                                    "headerTemplate": "{{key}} (type: {{self.type}}, version: {{self.version}})",
                                                                                    "options": {
                                                                                        "disable_properties": true,
                                                                                        "disable_collapse": false,
                                                                                        "disable_edit_json": true
                                                                                    },
                                                                                    "format": "grid",
                                                                                    "properties": {
                                                                                        "type": {
                                                                                            "type": "string",
                                                                                            "enum": ["STUDY", "SERIES", "PUBLICATION", "STUDY_ATTACHMENT", "STUDY_VARIABLES", "STUDY_VARIABLE"],
                                                                                            "readOnly": true
                                                                                        },
                                                                                        "version": {
                                                                                            "type": "integer"
                                                                                        }
                                                                                    },
                                                                                    "additionalProperties": false
                                                                                },
                                                                                "references": {
                                                                                    "description": "Tietokenttien käyttämien referenssien määritykset. (Katso uml_json_configuration_reference.graphml ja Reference specification.odt -tiedostot.)",
                                                                                    "type": "object",
                                                                                    "patternProperties": {
                                                                                        ".*": {
                                                                                            "$ref": "#/definitions/reference",
                                                                                            "oneOf": [{
                                                                                                "title": "JSON",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "JSON"
                                                                                                    },
                                                                                                    "target": {
                                                                                                        "description": "JSON-tiedosto, johon referenssi kohdennetaan."
                                                                                                    },
                                                                                                    "valuePath": {
                                                                                                        "type": "string"
                                                                                                    }
                                                                                                },
                                                                                                "required": [
                                                                                                    "valuePath"
                                                                                                ]
                                                                                            }, {
                                                                                                "title": "REVISIONABLE",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "REVISIONABLE"
                                                                                                    },
                                                                                                    "target": {
                                                                                                        "description": "Konfiguraatiotyyppi, johon referenssi kohdennetaan."
                                                                                                    },
                                                                                                    "approvedOnly": {
                                                                                                        "type": "boolean",
                                                                                                        "default": false
                                                                                                    },
                                                                                                    "ignoreRemoved": {
                                                                                                        "type": "boolean",
                                                                                                        "default": false
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                "title": "DEPENDENCY",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "DEPENDENCY"
                                                                                                    },
                                                                                                    "target": {
                                                                                                        "description": "Tästä konfiguraatiosta löytyvä kenttäavain."
                                                                                                    },
                                                                                                    "valuePath": {
                                                                                                        "type": "string"
                                                                                                    }
                                                                                                },
                                                                                                "required": [
                                                                                                    "valuePath"
                                                                                                ]
                                                                                            }]
                                                                                        }
                                                                                    }
                                                                                },
                                                                                "selectionLists": {
                                                                                    "description": "`SELECTION`-tyyppisten kenttien valintalistojen määritykset.",
                                                                                    "type": "object",
                                                                                    "patternProperties": {
                                                                                        ".*": {
                                                                                            "$ref": "#/definitions/selectionList",
                                                                                            "oneOf": [{
                                                                                                "title": "VALUE",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "VALUE"
                                                                                                    },
                                                                                                    "options": {
                                                                                                        "$ref": "#/definitions/simpleArray",
                                                                                                        "items": {
                                                                                                            "$ref": "#/definitions/option",
                                                                                                            "properties": {
                                                                                                                "&title": {
                                                                                                                    "title": "Käännösteksti",
                                                                                                                    "format": "grid",
                                                                                                                    "options": {
                                                                                                                        "disable_edit_json": true
                                                                                                                    },
                                                                                                                    "$ref": "#/definitions/translatableText"
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                "title": "LITERAL",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "LITERAL"
                                                                                                    },
                                                                                                    "options": {
                                                                                                        "$ref": "#/definitions/simpleArray",
                                                                                                        "items": {
                                                                                                            "$ref": "#/definitions/option",
                                                                                                            "properties": {
                                                                                                                "&title": {
                                                                                                                    "options": {
                                                                                                                        "hidden": true
                                                                                                                    }
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                "title": "REFERENCE",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "REFERENCE"
                                                                                                    },
                                                                                                    "reference": {
                                                                                                        description: "`references`-objektin ominaisuuden nimi, josta löytyy tämän kentän referenssin määritys.",
                                                                                                        "type": "string"
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                "title": "SUBLIST",
                                                                                                "properties": {
                                                                                                    "type": {
                                                                                                        "template": "SUBLIST"
                                                                                                    },
                                                                                                    "sublistKey": {
                                                                                                        "type": "string",
                                                                                                        "description": "`selectionLists`-objektin ominaisuuden nimi, josta löytyy tarkentava määritys tälle valintalistalle."
                                                                                                    }
                                                                                                }
                                                                                            }]
                                                                                        }
                                                                                    }
                                                                                },
                                                                                "fields": {
                                                                                    "description": "Tietokenttien määritykset. Yksittäisen kentän määritys löytyy uml_json_configuration_field.graphml-tiedostosta.",
                                                                                    "type": "object",
                                                                                    "patternProperties": {
                                                                                        ".*": {
                                                                                            "$ref": "#/definitions/field",
                                                                                            "oneOf": [{
                                                                                                title: "STRING",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["STRING"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "INTEGER",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["INTEGER"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "REAL",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["REAL"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "REFERENCE",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["REFERENCE"]
                                                                                                    },
                                                                                                    reference: {
                                                                                                        description: "`references`-objektin ominaisuuden nimi, josta löytyy tämän kentän referenssin määritys.",
                                                                                                        type: "string"
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "CONTAINER",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["CONTAINER"]
                                                                                                    },
                                                                                                    subfields: {
                                                                                                        description: "Taulukon sarakkeiden tietokenttien kenttäavaimet."
                                                                                                    }
                                                                                                },
                                                                                                $ref: "#/definitions/fieldContainer"
                                                                                            }, {
                                                                                                title: "REFERENCECONTAINER",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["REFERENCECONTAINER"]
                                                                                                    },
                                                                                                    reference: {
                                                                                                        description: "`references`-objektin ominaisuuden nimi, josta löytyy tämän kentän referenssin määritys.",
                                                                                                        type: "string"
                                                                                                    },
                                                                                                    bidirectional: {
                                                                                                        type: "string"
                                                                                                    },
                                                                                                    subfields: {
                                                                                                        description: "Taulukon sarakkeiden tietokenttien kenttäavaimet. Näiden täytyy olla `REFERENCE`-tyyppisiä ja referenssin täytyy olla `DEPENDENCY`-tyyppinen, joka kohdistuu tähän kenttään."
                                                                                                    }
                                                                                                },
                                                                                                $ref: "#/definitions/fieldContainer"
                                                                                            }, {
                                                                                                title: "SELECTION",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["SELECTION"]
                                                                                                    },
                                                                                                    selectionList: {
                                                                                                        description: "`selectionLists`-objektin ominaisuuden nimi, josta löytyy tämän kentän valintalistan määritys.",
                                                                                                        type: "string"
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "CONCAT",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["CONCAT"]
                                                                                                    },
                                                                                                    concatenate: {
                                                                                                        type: "array",
                                                                                                        items: {
                                                                                                            type: "string"
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "DATE",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["DATE"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "DATETIME",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["DATETIME"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "TIME",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["TIME"]
                                                                                                    }
                                                                                                }
                                                                                            }, {
                                                                                                title: "RICHTEXT",
                                                                                                properties: {
                                                                                                    type: {
                                                                                                        "enum": ["RICHTEXT"]
                                                                                                    },
                                                                                                    exact: {
                                                                                                        default: false,
                                                                                                        options: {
                                                                                                            hidden: true
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }]
                                                                                        }
                                                                                    }
                                                                                },
                                                                                "restrictions": {
                                                                                    "type": "array",
                                                                                    "description": "Tämän konfiguraation tietojoukolle tehtävien operaatioiden rajoitteet/pakotteet. (Katso restrictions_design_pseudo.txt)",
                                                                                    "options": {
                                                                                        "collapsed": true
                                                                                    },
                                                                                    "items": {
                                                                                        "type": "object",
                                                                                        "title": "Operation",
                                                                                        "properties": {
                                                                                            "type": {
                                                                                                "type": "string",
                                                                                                "enum": [
                                                                                                    "SAVE",
                                                                                                    "APPROVE",
                                                                                                    "REMOVE",
                                                                                                    "REMOVE_DRAFT",
                                                                                                    "REMOVE_LOGICAL",
                                                                                                    "RESTORE",
                                                                                                    "CLAIM",
                                                                                                    "RELEASE",
                                                                                                    "ALL"
                                                                                                ]
                                                                                            },
                                                                                            "targets": {
                                                                                                "description": "Operaatiolle määritellyt rajoitteet/pakotteet. Näiden on validoiduttava onnistuneesti, jotta operaatio suoritetaan.",
                                                                                                "options": {
                                                                                                    "collapsed": true
                                                                                                },
                                                                                                "$ref": "#/definitions/restrictionTargets"
                                                                                            }
                                                                                        },
                                                                                        "additionalProperties": false
                                                                                    }
                                                                                },
                                                                                "namedTargets": {
                                                                                    "$ref": "#/definitions/namedTargets",
                                                                                    "description": "Rajoitteissa käytetyt valmiiksi määritellyt kokonaisuudet, joihin viitataan NAMED-tyyppisillä `target`-objekteilla."
                                                                                },
                                                                                "displayId": {
                                                                                    "type": "string",
                                                                                    "description": "Käyttäjälle näytettävän ID:n kenttäavain. Muun kuin taulukkotyyppisen kentän avain. Jos ei määritelty, käyttäjälle näytetään `key.id`."
                                                                                }
                                                                            },
                                                                            "required": ["_help", "key", "references", "selectionLists", "fields", "restrictions", "namedTargets", "displayId"],
                                                                            "additionalProperties": false,
                                                                            "definitions": require('./../definitions')
                                                                        }
                                                                    }).data('jsoneditor').setValue(JSON.parse(response));
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    ]
                                }, {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            title: "Konfiguraatio",
                                            colspan: 2,
                                            field: {
                                                key: "dataConfigEditor",
                                                displayType: 'CUSTOM_JS'
                                            },
                                            postCreate: function() {
                                                $editor = this.children().first()
                                                    .addClass('metka-conf-editor');
                                            }
                                        }
                                    ]
                                }, {
                                    type: "ROW",
                                    cells: [
                                        {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            colspan: 2,
                                            button: {
                                                title: "Tallenna",
                                                create: function() {
                                                    this.click(function() {
                                                        require('./../server')("/settings/uploadJson", {
                                                            data: JSON.stringify({
                                                                type: 'DATA_CONF',
                                                                json: JSON.stringify($editor.data('jsoneditor').getValue())
                                                            }),
                                                            success: function (response) {
                                                                require('./../resultViewer')(response);
                                                            }
                                                        });
                                                    });

                                                }
                                            }

                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }, {
                    type: "TAB",
                    title: "API",
                    permissions: [
                        "canViewAPIUsers"
                    ],
                    content: [{
                        type: "SECTION",
                        title: "Lisää käyttäjä",
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    title: "Käyttäjänimi",
                                    type: "CELL",
                                    horizontal: true,
                                    field: {
                                        key: "addapiuserusername"
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    title: "Nimi",
                                    type: "CELL",
                                    horizontal: true,
                                    field: {
                                        key: "addapiusername"
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    title: "Rooli",
                                    type: "CELL",
                                    horizontal: true,
                                    field: {
                                        key: "addapiuserrole"
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    button: {
                                        title: "Lisää käyttäjä",
                                        create: function(options) {
                                            this.click(function() {
                                                require('./../server')("/settings/newAPIUser", {
                                                    type: "POST",
                                                    data: JSON.stringify({
                                                        username: require('./../data')(options)("addapiuserusername").getByLang('DEFAULT'),
                                                        name: require('./../data')(options)("addapiusername").getByLang('DEFAULT'),
                                                        role: require('./../data')(options)("addapiuserrole").getByLang('DEFAULT')
                                                    }),
                                                    async: false,
                                                    success: function (response) {
                                                        reloadAPIUsers();
                                                    }
                                                });
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }, {
                        type: "COLUMN",
                        rows: [{
                            type: "ROW",
                            cells: [{
                                type: "CELL",
                                title: "Käyttäjät",
                                readOnly: true,
                                field: {
                                    key: "apiusers",
                                    columnFields: [
                                        "apiusersname",
                                        "apiusersusername",
                                        "apiusersrole",
                                        "apiusersaccess",
                                        "apiuserscreated"
                                    ],
                                    dialogTitles: {
                                        VIEW: "API-Käyttäjä"
                                    },
                                    onRemove: function($row) {
                                        var transferRow=$row.data('transferRow');
                                        require('./../server')('/settings/removeAPIUser/{userName}'.supplant({
                                            userName: transferRow.fields.apiusersusername.values.DEFAULT.current
                                        }), {
                                            type: 'GET',
                                            async: false,
                                            success: function(response) {
                                                reloadAPIUsers();
                                            }
                                        });
                                    }
                                },
                                subfieldConfiguration: {
                                    apiuserssecret: {
                                        field: {
                                            multiline: true
                                        }
                                    }
                                },
                                postCreate: function() {
                                    reloadAPIUsers();
                                }
                            }]
                        }]
                    }]
                }
            ]
        });

        function reloadAPIUsers() {
            require('./../server')("/settings/listAPIUsers", {
                type: "GET",
                async: false,
                success: function (response) {
                    require('./../data')(options)('apiusers').removeRows('DEFAULT');
                    response.users.map(function(user) {
                        require('./../data')(options)('apiusers').appendByLang('DEFAULT', require('./../map/object/transferRow')({
                            apiusersname: user.name,
                            apiusersusername: user.userName,
                            apiusersrole: user.role,
                            apiuserssecret: user.secret,
                            apiusersaccess: user.lastAccess,
                            apiuserscreated: user.createdBy
                        }, 'DEFAULT'));
                    });
                    options.$events.trigger('redraw-{key}'.supplant({key: 'apiusers'}));
                }
            });
        }

        onLoad();
    };
});
