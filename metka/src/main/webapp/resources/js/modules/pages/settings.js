define(function (require) {
    'use strict';

    return function (options, onLoad) {

        var commonSearchBooleans = require('./../commonSearchBooleans');

        var $editor;

        $.extend(options, {
            data: commonSearchBooleans.initialData({}),
            dataConf: $.extend(true, {}, {
                selectionLists: {
                    dataConfigType_list: {
                        key: "dataConfigType_list",
                        type: "VALUE",
                        includeEmpty: true,
                        values: [

                        ]
                    },
                    guiConfigType_list: {
                        key: "guiConfigType_list",
                        type: "VALUE",
                        includeEmpty: true,
                        values: [

                        ]
                    },
                    jsonKeys_list: {
                        key: "jsonKeys_list",
                        type: "VALUE",
                        includeEmpty: true,
                        values: [

                        ]
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
                    }
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
                                        /*{
                                            type: "CELL",
                                            title: "Indekserit",
                                            readOnly: true,
                                            field: {
                                                key: "indexers",

                                                columnFields: [
                                                    "indexPath",
                                                    "indexIsRunning"
                                                ]
                                            }

                                        }*/
                                        {
                                            type: "CELL",
                                            field: {
                                                displayType: "CUSTOM_JS"
                                            },
                                            create: function(options) {
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
                                }
                            ]
                        }
                    ]
                },
                {
                    type: "TAB",
                    title: "Konfiguraatiot",
                    permissions: [
                        "canUploadConfigurations",
                        "canUploadJson"
                    ],
                    content: [
                        {
                            type: "SECTION",
                            title: "Data konfiguraatiot",
                            defaultState: "OPEN",
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
                                                        key: "dataConfigTypes"
                                                    },
                                                    create: function() {
                                                        var $elem = this.find("select").first().attr("id", "dataConfigTypes");
                                                        require('./../server')("/settings/getJsonList/DATA_CONF", {
                                                            type: "GET",
                                                            success: function (response) {
                                                                $elem.append(response.map(function(entry) {
                                                                    return $("<option>").data(entry).text(entry.title);
                                                                }));
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
                                                                var data = $("#dataConfigTypes").children(":selected").data();
                                                                if (data.configKey) {
                                                                    require('./../preloader')($editor);
                                                                    require('./../server')("/settings/getJsonContent", {
                                                                        data: JSON.stringify(data),
                                                                        success: function (response) {
                                                                            JSONEditor.defaults.editors.object.options.collapsed = true;
                                                                            var editor = $editor.empty().data('jsoneditor');
                                                                            if (editor && editor.destroy) {
                                                                                editor.destroy();
                                                                            }
                                                                            $editor.jsoneditor({
                                                                                "schema": {
                                                                                    "title": "Datan konfiguraatio",
                                                                                    "options": {
                                                                                        "disable_properties": true,
                                                                                        "disable_collapse": false
                                                                                    },
                                                                                    "type": "object",
                                                                                    "properties": {
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
                                                                                            "type": "object",
                                                                                            "patternProperties": {
                                                                                                ".*": {
                                                                                                    "$ref": "#/definitions/reference",
                                                                                                    "oneOf": [{
                                                                                                        "title": "DEPENDENCY",
                                                                                                        "properties": {
                                                                                                            "type": {
                                                                                                                "template": "DEPENDENCY"
                                                                                                            },
                                                                                                            "valuePath": {
                                                                                                                "type": "string"
                                                                                                            }
                                                                                                        }
                                                                                                    }, {
                                                                                                        "title": "REVISIONABLE",
                                                                                                        "properties": {
                                                                                                            "type": {
                                                                                                                "template": "REVISIONABLE"
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
                                                                                                        "title": "JSON",
                                                                                                        "properties": {
                                                                                                            "type": {
                                                                                                                "template": "JSON"
                                                                                                            },
                                                                                                            "valuePath": {
                                                                                                                "type": "string"
                                                                                                            }
                                                                                                        }
                                                                                                    }]
                                                                                                }
                                                                                            }
                                                                                        },
                                                                                        "selectionLists": {
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
                                                                                                                            "title": "Teksti",
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
                                                                                                                "description": "Listan avain"
                                                                                                            }
                                                                                                        }
                                                                                                    }]
                                                                                                }
                                                                                            }
                                                                                        },
                                                                                        "fields": {
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
                                                                                                                type: "string"
                                                                                                            }
                                                                                                        }
                                                                                                    }, {
                                                                                                        title: "CONTAINER",
                                                                                                        properties: {
                                                                                                            type: {
                                                                                                                "enum": ["CONTAINER"]
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
                                                                                                                type: "string"
                                                                                                            },
                                                                                                            bidirectional: {
                                                                                                                type: "string"
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
                                                                                                            "DELETE"
                                                                                                        ]
                                                                                                    },
                                                                                                    "targets": {
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
                                                                                            "$ref": "#/definitions/namedTargets"
                                                                                        },
                                                                                        "displayId": {
                                                                                            "type": "string"
                                                                                        }
                                                                                    },
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
                                        },
                                        {
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
                                                    create: function() {
                                                       $editor = this.children().first();
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
                                                    colspan: 2,
                                                    button: {
                                                        title: "Tallenna",
                                                        create: function() {
                                                            this.click(function() {
                                                                var request = {
                                                                  type: 'DATA_CONF',
                                                                  json: JSON.stringify($editor.data('jsoneditor').getValue())
                                                                };
                                                                require('./../server')("/settings/uploadJson", {
                                                                    data: JSON.stringify(request),
                                                                    success: function (response) {
                                                                        require('./../modal')({
                                                                            title: MetkaJS.L10N.get(response === "OPERATION_SUCCESSFUL" ? 'alert.notice.title' : 'alert.error.title'),
                                                                            body: response,
                                                                            buttons: ["DISMISS"]
                                                                        });
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
                            type: "SECTION",
                            title: "GUI konfiguraatiot",
                            defaultState: "OPEN",
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
                                                        key: "guiConfigTypes"
                                                    },
                                                    create: function() {
                                                        var $elem = this.find("select").first().attr("id", "guiConfigTypes");


                                                        require('./../server')("/settings/getJsonList/GUI_CONF", {
                                                            type: "GET",
                                                            success: function (response) {
                                                                $elem.append(response.map(function(entry) {
                                                                    return $("<option>").data(entry).text(entry.title);
                                                                }));
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
                                                                var data = $("#guiConfigTypes").children(":selected").data();
                                                                if(data) {
                                                                    require('./../server')("/settings/getJsonContent", {
                                                                        data: JSON.stringify(data),
                                                                        success: function (response) {
                                                                            $("#guiConfigTextField").val(JSON.stringify(JSON.parse(response), null, 4));
                                                                        }
                                                                    });
                                                                }
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
                                                    title: "Konfiguraatio",
                                                    horizontal: true,
                                                    colspan: 2,
                                                    field: {
                                                        key: "guiConfigText",
                                                        multiline: true
                                                    },
                                                    create: function() {
                                                        this.find("textarea").first().attr("id", "guiConfigTextField").prop("rows", 20);
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
                                                    colspan: 2,
                                                    button: {
                                                        title: "Tallenna",
                                                        create: function() {
                                                            this.click(function() {
                                                                var request = {
                                                                    type: "GUI_CONF",
                                                                    json: $("#guiConfigTextField").val()
                                                                };
                                                                require('./../server')("/settings/uploadJson", {
                                                                    data: JSON.stringify(request),
                                                                    success: function (response) {
                                                                        require('./../modal')({
                                                                            title: MetkaJS.L10N.get(response === "OPERATION_SUCCESSFUL" ? 'alert.notice.title' : 'alert.error.title'),
                                                                            body: response,
                                                                            buttons: ["DISMISS"]
                                                                        });
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
                            type: "SECTION",
                            title: "JSON",
                            defaultState: "OPEN",
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
                                                    title: "Avaimet",
                                                    horizontal: true,
                                                    field: {
                                                        key: "jsonKeys"
                                                    },
                                                    create: function() {
                                                        var $elem = this.find("select").first().attr("id", "jsonKeys");
                                                        require('./../server')("/settings/getJsonList/MISC", {
                                                            type: "GET",
                                                            success: function (response) {
                                                                $elem.append(response.map(function(entry) {
                                                                    return $("<option>").data(entry).text(entry.title);
                                                                }));
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
                                                                var data = $("#jsonKeys").children(":selected").data();
                                                                if(data) {
                                                                    require('./../server')("/settings/getJsonContent", {
                                                                        data: JSON.stringify(data),
                                                                        success: function (response) {
                                                                            $("#jsonTextField").val(JSON.stringify(JSON.parse(response), null, 4));
                                                                        }
                                                                    });
                                                                }
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
                                                    title: "JSON",
                                                    horizontal: true,
                                                    colspan: 2,
                                                    field: {
                                                        key: "jsonText",
                                                        multiline: true
                                                    },
                                                    create: function() {
                                                        this.find("textarea").first().attr("id", "jsonTextField").prop("rows", 20);
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
                                                    colspan: 2,
                                                    button: {
                                                        title: "Tallenna",
                                                        create: function() {
                                                            this.click(function() {
                                                                var request = {
                                                                    type: "MISC",
                                                                    json: $("#jsonTextField").val()
                                                                };
                                                                require('./../server')("/settings/uploadJson", {
                                                                    data: JSON.stringify(request),
                                                                    success: function (response) {
                                                                        require('./../modal')({
                                                                            title: MetkaJS.L10N.get(response === "OPERATION_SUCCESSFUL" ? 'alert.notice.title' : 'alert.error.title'),
                                                                            body: response,
                                                                            buttons: ["DISMISS"]
                                                                        });
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
                        }
                    ]
                }
            ]
        });

        onLoad();
    };
});