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
    var getPropertyNS = require('./utils/getPropertyNS');
    var resultParser = require('./resultParser');

    return function (url, requestConf, getResults, mapResult, fields, columnFields, options, showInModal) {
        function trOnClick(transferRow) {
            var viewRequestOptions = {
                PAGE: transferRow.fields.TYPE.values.DEFAULT.current,
                id: transferRow.fields.id.values.DEFAULT.current,
                no: transferRow.fields.no.values.DEFAULT.current
            };
            var useModal = false;
            if(showInModal) {
                switch(typeof showInModal) {
                    case 'function':
                        useModal = showInModal(transferRow);
                        break;
                    default:
                        useModal = showInModal;
                        break;
                }
            }
            if(useModal) {
                require('./revisionModal')(options, viewRequestOptions, null, null, null, true, null);
            } else {
                require('./assignUrl')('view', viewRequestOptions);
            }
        }

        var data = require('./data')(options);
        require('./server')(url, {
            data: JSON.stringify(require('./searchRequest')(options, requestConf)),
            success: function (data) {
                if(resultParser(data.result).getResult() !== 'OPERATION_SUCCESSFUL') {
                    require('./resultViewer')(data.result);
                }
                var fieldOptions = $.extend(true, options, {
                    buttons: null,
                    dataConf: {
                        fields: $.extend(true, fields, {
                            searchResults: {
                                key: "searchResults",
                                type: "CONTAINER"
                            }
                        })
                    },
                    data: {
                        fields: {
                            searchResults: {
                                type: 'CONTAINER',
                                rows: {}
                            }
                        }
                    },
                    style: 'primary',
                    readOnly: true,
                    field: {
                        key: "searchResults",
                        columnFields: columnFields,
                        onClick: trOnClick
                    }
                });
                var results = getResults(data);
                var objectToTransferRow = require('./map/object/transferRow');
                fieldOptions.data.fields.searchResults.rows.DEFAULT = results.map(mapResult).map(function (result) {
                    return objectToTransferRow(result, fieldOptions.defaultLang);
                });

                // if exactly 1 search result, perform the row action
                if (fieldOptions.data.fields.searchResults.rows.DEFAULT.length === 1) {
                    trOnClick(fieldOptions.data.fields.searchResults.rows.DEFAULT[0]);
                    return;
                }
                var $resultContainer;
                if(options.resultContainer) {
                    $resultContainer = $('#'+options.resultContainer);
                } else {
                    $resultContainer = $('.content');
                }

                $resultContainer.children('.searchResults').remove();

                fieldOptions.fieldOptions = getPropertyNS(fieldOptions, "dataConf.fields", "searchResults");
                var $field = require('./field').call($('<div>'), fieldOptions)
                    .addClass('searchResults');

                $field.find('.panel-heading')
                    .text(MetkaJS.L10N.get('search.result.title'))
                    .append($('<div class="pull-right">')
                        .text(MetkaJS.L10N.get('search.result.amount').supplant(results))
                        .append('&nbsp;')
                        .append($('<div class="btn-group btn-group-xs pull-right">')
                            .append(require('./button')()({
                                title: 'Lataa',
                                style: 'default',
                                create: function () {
                                    this.click(function () {
                                        saveAs(new Blob([JSON.stringify(results)], {type: "text/json;charset=utf-8"}), "hakutulos.json");
                                    });
                                }
                            }))));
                $resultContainer.append($field);
            }
        });
    };
});
