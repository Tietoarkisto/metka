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
        var indexCommandNumber;
        var indexRevisionNumber;

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
                            value: "metka:basic-user",
                            title: "Käyttäjä"
                        }, {
                            value: "metka:translator",
                            title: "Kääntäjä"
                        }, {
                            value: "metka:data-administrator",
                            title: "Data admin"
                        }, {
                            value: "metka:administrator",
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
                    addapiusername: {
                        key: "addapiusername",
                        type: "STRING"
                    },
                    addapiuserrole: {
                        key: "addapiuserrole",
                        type: "SELECTION",
                        selectionList: "addapiuserrole_list"
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
                },
                apiusersname: {
                    key: "apiusersname",
                    title: MetkaJS.L10N.get("settings.api.user")
                },
                apiusersrole: {
                    key: "apiusersrole",
                    title: MetkaJS.L10N.get("settings.api.role")
                },
                apiuserssecret: {
                    key: "apiuserssecret",
                    title: "Avain"
                },
                apiusersaccess: {
                    key: "apiusersaccess",
                    title: MetkaJS.L10N.get("settings.api.lastlogin")
                },
                apiuserscreated: {
                    key: "apiuserscreated",
                    title: MetkaJS.L10N.get("settings.api.createdby")
                }
            },
            content: [
                {
                    type: "TAB",
                    title: MetkaJS.L10N.get("settings.reports.title"),
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
                                                title: MetkaJS.L10N.get("settings.reports.download"),
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
                    title: MetkaJS.L10N.get("settings.indexing.title"),
                    // TODO: Fetch index info and show
                    permissions: [
                        "canViewIndexInfo"
                    ],
                    content: [
                        {
                            type: "COLUMN",
                            columns: 3,
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
                                                indexCommandNumber = $(this).children().first();
                                                indexCommandNumber.text(MetkaJS.L10N.get("settings.indexing.commandsqueue")+ "0");
                                                require("./../server")("/settings/openIndexCommands", {
                                                    method: "GET",
                                                    success: function(response) {
                                                        indexCommandNumber.text(MetkaJS.L10N.get("settings.indexing.commandsqueue")+response.openCommands);
                                                    }
                                                });
                                            }
                                        }, {
                                            type: "CELL",
                                            field: {
                                                displayType: "CUSTOM_JS"
                                            },
                                            postCreate: function(options) {
                                                indexRevisionNumber = $(this).children().first();
                                                indexRevisionNumber.text(MetkaJS.L10N.get("settings.indexing.notindexed")+"0");
                                                require("./../server")("/settings/openIndexCommands", {
                                                    method: "GET",
                                                    success: function(response) {
                                                        indexRevisionNumber.text(MetkaJS.L10N.get("settings.indexing.notindexed")+response.openCommands);
                                                    }
                                                });
                                            }
                                        }, {
                                            type: "CELL",
                                            contentType: "BUTTON",
                                            button: {
                                                title: MetkaJS.L10N.get("settings.indexing.refresh"),
                                                create: function(options) {
                                                    this.click(function() {
                                                        require("./../server")("/settings/openIndexCommands", {
                                                            method: "GET",
                                                            success: function(response) {
                                                                indexCommandNumber.text(MetkaJS.L10N.get("settings.indexing.commandsqueue")+response.openCommands);
                                                            }
                                                        });
                                                        require("./../server")("/settings/revisionsWaitingIndexing", {
                                                            method: "GET",
                                                            success: function(response) {
                                                                indexRevisionNumber.text(MetkaJS.L10N.get("settings.indexing.notindexed")+response.openCommands);
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
                                                title: MetkaJS.L10N.get("settings.indexing.indexeverything"),
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
                                                title: MetkaJS.L10N.get("settings.indexing.closeindexers"),
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
                    title: MetkaJS.L10N.get("settings.configuration.title"),
                    permissions: [
                        "canUploadConfigurations",
                        "canUploadJson"
                    ],
                    content: [{
                        type: "SECTION",
                        title: MetkaJS.L10N.get("settings.configuration.dataconf"),
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: MetkaJS.L10N.get("settings.configuration.keys"),
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
                                                options.dataConf.selectionLists.dataConfigType_list.options = options.dataConf.selectionLists.dataConfigType_list.options.sort();
                                            }
                                        });
                                    }
                                }, {
                                    type: "CELL",
                                    contentType: "BUTTON",
                                    button: {
                                        title: MetkaJS.L10N.get('general.buttons.download'),
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
                                    contentType: "BUTTON",
                                    colspan: 2,
                                    button: {
                                        title: MetkaJS.L10N.get("settings.configuration.edit"),
                                        create: function() {
                                            this.click(function() {
                                                var text = require('./../data')(options)("dataConfigText").getByLang("DEFAULT");
                                                require('./../dataConfigurationEditor')(!!text ? JSON.parse(text) : {}, function(configuration) {
                                                    require('./../data')(options)("dataConfigText").setByLang("DEFAULT", JSON.stringify(configuration, null, 4));
                                                })
                                            })
                                        }
                                    }

                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: MetkaJS.L10N.get("settings.configuration.configuration"),
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
                                        title: MetkaJS.L10N.get("general.buttons.save"),
                                        create: function() {
                                            this.click(function() {
                                                require('./../modal')($.extend(true, require('./../optionsBase')(), {
                                                    title: MetkaJS.L10N.get('confirmation.save.configuration.title'),
                                                    body: MetkaJS.L10N.get('confirmation.save.configuration.body'),
                                                    buttons: [{
                                                        create: function () {
                                                            this
                                                                .text(MetkaJS.L10N.get('general.buttons.yes'))
                                                                .click(function () {
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
                                                            // Fire an event so that metka doesn't ask for confirmation if moving from page
                                                            var evt = new CustomEvent('saved');
                                                            window.dispatchEvent(evt);
                                                        },
                                                        type: "YES"
                                                    },{
                                                        create: function () {
                                                            this.text(MetkaJS.L10N.get('general.buttons.no'))
                                                        },
                                                        type: "NO"
                                                    }]
                                                }));
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }, {
                        type: "SECTION",
                        title: MetkaJS.L10N.get("settings.configuration.guiconf"),
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: MetkaJS.L10N.get("settings.configuration.keys"),
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
                                        title: MetkaJS.L10N.get('general.buttons.download'),
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
                                    title: MetkaJS.L10N.get("settings.configuration.configuration"),
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
                                        title: MetkaJS.L10N.get("general.buttons.save"),
                                        create: function() {
                                            this.click(function() {
                                                require('./../modal')($.extend(true, require('./../optionsBase')(), {
                                                    title: MetkaJS.L10N.get('confirmation.save.configuration.title'),
                                                    body: MetkaJS.L10N.get('confirmation.save.configuration.body'),
                                                    buttons: [{
                                                        create: function () {
                                                            this
                                                                .text(MetkaJS.L10N.get('general.buttons.yes'))
                                                                .click(function () {
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
                                                            // Fire an event so that metka doesn't ask for confirmation if moving from page
                                                            var evt = new CustomEvent('saved');
                                                            window.dispatchEvent(evt);
                                                        },
                                                        type: "YES"
                                                    },{
                                                        create: function () {
                                                            this.text(MetkaJS.L10N.get('general.buttons.no'))
                                                        },
                                                        type: "NO"
                                                    }]
                                                }));
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }, {
                        type: "SECTION",
                        title: MetkaJS.L10N.get("settings.configuration.json"),
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            columns: 2,
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    type: "CELL",
                                    title: MetkaJS.L10N.get("settings.configuration.keys"),
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
                                        title: MetkaJS.L10N.get('general.buttons.download'),
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
                                    title: MetkaJS.L10N.get("settings.configuration.configuration"),
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
                                        title: MetkaJS.L10N.get("general.buttons.save"),
                                        create: function() {
                                            this.click(function() {
                                                require('./../modal')($.extend(true, require('./../optionsBase')(), {
                                                    title: MetkaJS.L10N.get('confirmation.save.configuration.title'),
                                                    body: MetkaJS.L10N.get('confirmation.save.configuration.body'),
                                                    buttons: [{
                                                        create: function () {
                                                            this
                                                                .text(MetkaJS.L10N.get('general.buttons.yes'))
                                                                .click(function () {
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
                                                            // Fire an event so that metka doesn't ask for confirmation if moving from page
                                                            var evt = new CustomEvent('saved');
                                                            window.dispatchEvent(evt);
                                                        },
                                                        type: "YES"
                                                    },{
                                                        create: function () {
                                                            this.text(MetkaJS.L10N.get('general.buttons.no'))
                                                        },
                                                        type: "NO"
                                                    }]
                                                }));
                                            });
                                        }
                                    }
                                }]
                            }]
                        }]
                    }]
                }, {
                    type: "TAB",
                    title: MetkaJS.L10N.get("settings.api.title"),
                    permissions: [
                        "canViewAPIUsers"
                    ],
                    content: [{
                        type: "SECTION",
                        title: MetkaJS.L10N.get("settings.api.adduser"),
                        defaultState: "OPEN",
                        content: [{
                            type: "COLUMN",
                            rows: [{
                                type: "ROW",
                                cells: [{
                                    title: MetkaJS.L10N.get("settings.api.name"),
                                    type: "CELL",
                                    horizontal: true,
                                    field: {
                                        key: "addapiusername"
                                    }
                                }]
                            }, {
                                type: "ROW",
                                cells: [{
                                    title: MetkaJS.L10N.get("settings.api.role"),
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
                                        title: MetkaJS.L10N.get("settings.api.adduser"),
                                        create: function(options) {
                                            this.click(function() {
                                                require('./../server')("/settings/newAPIUser", {
                                                    type: "POST",
                                                    data: JSON.stringify({
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
                                title: MetkaJS.L10N.get("settings.api.users"),
                                readOnly: true,
                                field: {
                                    key: "apiusers",
                                    columnFields: [
                                        "apiusersname",
                                        "apiusersrole",
                                        "apiusersaccess",
                                        "apiuserscreated"
                                    ],
                                    dialogTitle: {
                                        VIEW: "API-Käyttäjä"
                                    },
                                    onRemove: function($row) {
                                        var transferRow=$row.data('transferRow');
                                        require('./../server')('/settings/removeAPIUser/{secret}'.supplant({
                                            secret: transferRow.fields.apiuserssecret.values.DEFAULT.current
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
