define(function (require) {
    'use strict';

    return function (options, onLoad) {
        var commonSearchBooleans = require('./../commonSearchBooleans');

        $.extend(options, {
            data: commonSearchBooleans.initialData({}),
            dataConf: $.extend(true, {}, {
                fields: {
                    "indexers": {
                        key: "indexers",
                        type: "CONTAINER",
                        subfields: [
                            "indexPath",
                            "indexIsRunning"
                        ]
                    },
                    "indexPath": {
                        key: "indexPath",
                        type: "STRING"
                    },
                    "indexIsRunning": {
                        key: "indexIsRunning",
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
                                                    })
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
                        //"canViewIndexInfo"
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
                                            field: {
                                                key: "indexers",

                                                columnFields: [
                                                    "indexPath",
                                                    "indexIsRunning"
                                                ]
                                            }

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