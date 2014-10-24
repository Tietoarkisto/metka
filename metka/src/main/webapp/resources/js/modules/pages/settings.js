define(function (require) {
    'use strict';

    return function (options, onLoad) {

        var commonSearchBooleans = require('./../commonSearchBooleans');

        var dataConfigEditor;

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
                                        {
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
                                                create: function() {
                                                    this.click(function() {
                                                        require('./../assignUrl')('/settings/indexEverything');
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
                                                                if(data) {
                                                                    require('./../server')("/settings/getJsonContent", {
                                                                        data: JSON.stringify(data),
                                                                        success: function (response) {
                                                                           dataConfigEditor.setValue(JSON.parse(response));
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
                                                       dataConfigEditor = this.children().first().data('jsoneditor');
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
                                                                  json: JSON.stringify(dataConfigEditor.getValue())
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