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

    var resultParser = require('./../resultParser');

    var setContent;
    var dataConf = {
        references: {
            binder_page_ref: {
                key: "binder_page_ref",
                type: "REVISION",
                target: "BINDER_PAGE"
            },
            study_id_ref: {
                key: "study_id_ref",
                type: "DEPENDENCY",
                target: "pages",
                valuePath: "studyid"
            },
            study_title_ref: {
                key: "study_title_ref",
                type: "DEPENDENCY",
                target: "pages",
                valuePath: "studytitle"
            },
            binder_id_ref: {
                key: "study_id_ref",
                type: "DEPENDENCY",
                target: "pages",
                valuePath: "binderid"
            },
            description_ref: {
                key: "description_ref",
                type: "DEPENDENCY",
                target: "pages",
                valuePath: "description"
            }
        },
        fields: {
            findBinderId: {
                key: "findBinderId",
                type: "INTEGER"
            },
            pages: {
                key: "pages",
                type: "REFERENCECONTAINER",
                reference: "binder_page_ref",
                fixedOrder: true,
                subfields: [
                    "studyId",
                    "studyTitle",
                    "binderId",
                    "description"
                ]
            },
            studyId: {
                key: "studyId",
                type: 'REFERENCE',
                subfield: true,
                reference: "study_id_ref"
            },
            studyTitle: {
                key: "studyTitle",
                type: 'REFERENCE',
                subfield: true,
                reference: "study_title_ref"
            },
            binderId: {
                key: "binderId",
                type: 'REFERENCE',
                subfield: true,
                reference: "binder_id_ref"
            },
            description: {
                key: "description",
                type: 'REFERENCE',
                subfield: true,
                reference: "description_ref"
            }
        }
    };
    var fieldTitles = {
        "studyId": {
            "title": "Aineistonro"
        },
        "studyTitle": {
            "title": "Aineiston nimi"
        },
        "binderId": {
            "title": "Mappinro"
        },
        "description": {
            "title": "Mapitettu aineisto"
        }
    };

    var dialogTitles = {
        "pages": {
            "key": "pages",
            "ADD": "Mapitus",
            "MODIFY": "Muokkaa mapitusta",
            "VIEW": "Mapitus"
        }
    };

    return function (options, onLoad) {
        function performSearch() {
            var binderSearch = {
                searchApproved: true,
                searchDraft: true,
                searchRemoved: true,
                values: {
                    'key.configuration.type': "BINDER_PAGE"
                }
            };
            var fbId = require('./../data')(options)("findBinderId").getByLang('DEFAULT');
            if(fbId) {
                binderSearch.values.binderid = fbId;
            }
            require('./../server')('searchAjax', {
                data: JSON.stringify(binderSearch),
                success: function(response) {
                    var rowId = 0;
                    require('./../data')(options)("pages").removeRows('DEFAULT');
                    response.rows.map(function(row) {
                        require('./../data')(options)("pages").appendByLang('DEFAULT', {
                            key: 'pages',
                            rowId: ++rowId,
                            value: row.id+"-"+row.no,
                            removed: false,
                            unapproved: true
                        })
                    });
                    options.$events.trigger('redraw-pages');
                }
            });
        }

        function view(requestOptions) {
            require('./../server')('viewAjax', $.extend({
                PAGE: 'BINDER_PAGE'
            }, requestOptions), {
                method: 'GET',
                success: function (response) {

                    // TODO: check status
                    if (resultParser(response.result).getResult() === 'VIEW_SUCCESSFUL') {
                    }
                    var modalOptions = $.extend({}, response.gui, {
                        //title: 'Muokkaa tiedostoa',
                        data: response.data,
                        dataConf: response.configuration,
                        //$events: options.$events,
                        // TODO: Events need better management so that some events can be inherited and others can be overwritten
                        $events: $({}),
                        defaultLang: 'DEFAULT',
                        large: false,
                        dialogTitles: options.dialogTitles || {}
                    });
                    modalOptions.$events.on('modal.refresh', performSearch);
                    // We need the isReadOnly function at this point so we need to add it before calling modal
                    modalOptions = $.extend(true, require('./../optionsBase')(), modalOptions);
                    modalOptions.type = modalOptions.isReadOnly(modalOptions) ? 'VIEW' : 'MODIFY';
                    require('./../modal')(modalOptions);
                }
            });
        }

        $.extend(options, {
            header: MetkaJS.L10N.get('type.BINDERS.title'),
            fieldTitles: fieldTitles,
            dialogTitles: dialogTitles,
            dataConf: dataConf,
            content: [{
                "type": "COLUMN",
                "columns": 2,
                "rows": [{
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": "Mappinro",
                        "horizontal": true,
                        "field": {
                            "key": "findBinderId"
                        }
                    }, {
                        "type": "CELL",
                        "contentType": "BUTTON",
                        "button": {
                            "title": "Hae",
                            "create": function() {
                                this.click(performSearch);
                            }
                        }
                    }]
                }]
            }, {
                "type": "COLUMN",
                "columns": 1,
                "rows": [{
                    "type": "ROW",
                    "cells": [{
                        "type": "CELL",
                        "title": "Mapitukset",
                        "colspan": 1,
                        "readOnly": true,
                        "field": {
                            "key": "pages",
                            "disableRemoval": true,
                            //"displayType": "CONTAINER",
                            "showReferenceValue": true,
                            "showReferenceState": true,
                            "columnFields": [
                                "binderId",
                                "studyId",
                                "studyTitle",
                                "description"
                            ],
                            onClick: function(transferRow) {
                                view({
                                    type: 'BINDER_PAGE',
                                    id: transferRow.value.split('-')[0],
                                    no: transferRow.value.split('-')[1]});
                            }
                        }
                    }]
                }]
            }],
            buttons: [{
                "&title": {
                    "default": "Lisää aineisto mappiin"
                },
                permissions: [
                    'canEditBinderPages'
                ],
                create: function () {
                    this.click(function () {
                        require('./../server')('create', {
                            data: JSON.stringify({
                                type: 'BINDER_PAGE'
                            }),
                            success: function(response) {
                                if(resultParser(response.result).getResult() === 'REVISION_CREATED') {
                                    view({
                                        type: 'BINDER_PAGE',
                                        id: response.data.key.id,
                                        no: response.data.key.no});
                                } else {
                                    require('./../resultViewer')(response.result);
                                }
                            }
                        });

                        /*var containerOptions = $.extend(true, require('./../optionsBase')(), {
                            title: 'Lisää aineisto mappiin',
                            data: {},
                            dataConf: {},
                            $events: $({}),
                            defaultLang: options.defaultLang,
                            content: [{
                                type: 'COLUMN',
                                columns: 1,
                                rows: [
                                    {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Mappinumero",
                                                "colspan": 1,
                                                "field": {
                                                    "displayType": "STRING",
                                                    "key": "binderId"
                                                }
                                            }
                                        ]
                                    }, {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Aineistonumero",
                                                "colspan": 1,
                                                "field": {
                                                    "displayType": "STRING",
                                                    "key": "studyId"
                                                }
                                            }
                                        ]
                                    }, {
                                        "type": "ROW",
                                        "cells": [
                                            {
                                                "type": "CELL",
                                                "title": "Mapitettu aineisto",
                                                "colspan": 1,
                                                "field": {
                                                    "displayType": "STRING",
                                                    "key": "description",
                                                    multiline: true
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }],
                            buttons: [{
                                create: function () {
                                    this
                                        .text(MetkaJS.L10N.get('general.buttons.ok'))
                                        .click(function () {
                                            require('./../server')('/binder/saveBinderPage', {
                                                data: JSON.stringify({
                                                    pageId: null,
                                                    binderId: require('./../data')(containerOptions)('binderId').getByLang(options.defaultLang),
                                                    studyId: require('./../data')(containerOptions)('studyId').getByLang(options.defaultLang),
                                                    description: require('./../data')(containerOptions)('description').getByLang(options.defaultLang)
                                                }),
                                                success: function(data) {
                                                    require('./../resultViewer')(data.result, "binder", function() {
                                                        if (data.result === 'PAGE_CREATED') {
                                                            setContent(data);
                                                        }
                                                    });
                                                }
                                            });
                                        });
                                }
                            }, {
                                type: 'CANCEL'
                            }]
                        });

                        require('./../modal')(containerOptions);*/
                    });
                }
            }]
        });

        performSearch();

        onLoad();
    };
});