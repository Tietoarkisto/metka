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

    var $query;
    return function (options, onLoad) {

        $.extend(options, {
            header: MetkaJS.L10N.get('topmenu.expert'),
            fieldTitles: {
                "id": {
                    "title" : "ID"
                },
                "name": {
                    "title" : "Nimi"
                },
                "no": {
                    "title" : "Revisio"
                },
                "state": {
                    "title" : "Tila"
                },
                "title": {
                    "title" : "Otsikko"
                },
                "type": {
                    "title" : "Tyyppi"
                }
            },
            content: [
                {
                    "type": "COLUMN",
                    "columns": 2,
                    "rows": [
                        {
                            "type": "ROW",
                            "cells": [
                                {
                                    "type": "CELL",
                                    "title": "Hakulause",
                                    "colspan": 1,
                                    "field": {
                                        "displayType": "STRING",
                                        "key": "search",
                                        "multiline": true
                                    },
                                    postCreate: function () {
                                        $query = this.find('textarea');
                                    }
                                },
                                {
                                    "type": "CELL",
                                    "title": "Tallennetut haut",
                                    "colspan": 1,
                                    "readOnly": true,
                                    "field": {
                                        "key": "savedsearches",
                                        "rowsPerPage": 10,
                                        "showSaveInfo": true,
                                        "columnFields": [
                                            "name"
                                        ],
                                        onRemove: function ($row) {
                                            require('./../server')('/expert/remove/{id}', require('./../map/transferRow/object')($row.data('transferRow'), options.defaultLang), {
                                                method: 'GET',
                                                success: function () {
                                                    $row.remove();
                                                }
                                            });
                                        },
                                        removeFilter: function(transferRow) {
                                            // NOTE: Change to return true to test denied audit message
                                            return require('./../hasEveryPermission')(['canRemoveNotOwnedExpertSearch']) || MetkaJS.User.userName===transferRow.saved.user;
                                        },
                                        onClick: function () {
                                            $query
                                                .val($(this).data('transferRow').fields.query.values.DEFAULT.current)
                                                .change();
                                        }
                                    },
                                    postCreate: function (options) {
                                        require('./../server')('/expert/list', {
                                            method: 'GET',
                                            success: function (data) {
                                                data.queries.forEach(function(query) {
                                                    require('./../data')(options).appendByLang( options.defaultLang, require('./../map/savedExpertSearchQuery/transferRow')(query, options.defaultLang));
                                                    /*options.$events.trigger('container-{key}-{lang}-push'.supplant({
                                                        key: options.field.key,
                                                        lang: options.defaultLang
                                                    }), [require('./../map/savedExpertSearchQuery/transferRow')(query, options.defaultLang)])*/
                                                });
                                                options.$events.trigger('redraw-{key}'.supplant({key: options.field.key}));
                                            }
                                        });
                                    }
                                }
                            ]
                        }, {
                            type: "ROW",
                            cells: [{
                                type: "CELL",
                                colspan: 2,
                                title: "Hakutulokset",
                                field: {
                                    key: "expertsearchresults",
                                    showReferenceValue: true,
                                    showReferenceState: true
                                }
                            }]
                        }
                    ]
                }
            ],
            buttons: [{
                title: "Hae",
                create: function(options) {
                    this.click(function() {
                        require('./../searchQuerySearch')(options, require('./../data')(options)('search').getByLang(options.defaultLang), "expertsearchresults").search();
                    });
                }
            }/*,
                require('./../searchButton')('/expert/query', function () {
                    return {
                        query: require('./../data')(options)('search').getByLang(options.defaultLang)
                    };
                }, function (data) {
                    return data.results;
                }, function (result) {
                    return {
                        title: result.title,
                        type: MetkaJS.L10N.get('type.{type}.title'.supplant(result)),
                        TYPE: result.type,
                        id: result.id,
                        no: result.no,
                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                    };
                }, {
                    title: {
                        type: 'STRING'
                    },
                    type: {
                        type: 'STRING'
                    },
                    id: {
                        type: 'INTEGER'
                    },
                    no: {
                        type: 'INTEGER'
                    },
                    state: {
                        type: 'STRING'
                    }
                }, [
                    "type",
                    "title",
                    "id",
                    "no",
                    "state"
                ],
                options,
                function(transferRow) {
                    switch(transferRow.fields.TYPE.values.DEFAULT.current) {
                        default:
                            return false;
                        case 'STUDY_VARIABLE':
                        case 'STUDY_ERROR':
                        case 'BINDER_PAGE':
                            return true;
                    }
                }),
                {
                    "&title": {
                        "default": "Tyhjennä"
                    },
                    create: function () {
                        this.click(function () {
                            $query
                                .val('')
                                .change();
                        });
                    }
                }*/, {
                    "&title": {
                        "default": "Tallenna haku"
                    },
                    create: function () {
                        this
                            .click(function () {
                                var modalOptions = ($.extend(true, require('./../optionsBase')(), {
                                    //title: 'Tallenna haku',
                                    type: "ADD",
                                    dialogTitle: {
                                        "ADD": "Tallenna haku"
                                    },
                                    content: [{
                                        type: 'COLUMN',
                                        columns: 1,
                                        rows: [
                                            {
                                                "type": "ROW",
                                                "cells": [
                                                    {
                                                        "type": "CELL",
                                                        "title": "Nimi",
                                                        "colspan": 1,
                                                        "field": {
                                                            "displayType": "STRING",
                                                            "key": "title"
                                                        }
                                                    }
                                                ]
                                            }
                                        ]
                                    }],
                                    buttons: [{
                                        "&title": {
                                            "default": 'Tallenna'
                                        },
                                        create: function () {
                                            this
                                                .click(function () {
                                                    require('./../server')('/expert/save', {
                                                        data: JSON.stringify({
                                                            query: require('./../data')(options)('search').getByLang(options.defaultLang),
                                                            title: require('./../data')(modalOptions)('title').getByLang(options.defaultLang)
                                                        }),
                                                        success: function(query) {
                                                            options.$events.trigger('container-{key}-{lang}-push'.supplant({
                                                                key: 'savedsearches',
                                                                lang: options.defaultLang
                                                            }), [require('./../map/savedExpertSearchQuery/transferRow')(query, options.defaultLang)])
                                                        }
                                                    });
                                                });
                                        }
                                    }, {
                                        type: 'CANCEL'
                                    }]
                                }));

                                require('./../modal')(modalOptions);
                            });
                    }
                }
            ],
            data: {},
            dataConf: {
                references: {
                    expertsearchresult_ref: {
                        type: "REVISION"
                    }
                },
                fields: {
                    name: {
                        type: "STRING",
                        subfield: true
                    },
                    search: {
                        type: "STRING"
                    },
                    savedsearches: {
                        type: "CONTAINER",
                        subfields: [
                            "name"
                        ]
                    },
                    expertsearchresults: {
                        type: "REFERENCECONTAINER",
                        reference: "expertsearchresult_ref"
                    }
                }
            }
        });
        onLoad();
    }
});