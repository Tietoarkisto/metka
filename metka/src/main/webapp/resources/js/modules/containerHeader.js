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

    return function (options, lang) {

        var getPropertyNS = require('./utils/getPropertyNS');
        var resultParser = require('./resultParser');
        var EMPTY = '-';
        var asyncRequestsMade = 0;
        var asyncRequestsReceived = 0;
        var currentlyDownloading = false;
        var rows;
        var result = {
            key: options.field.key,
            rows: []
        };

        function tr(transferRow, columns) {
            function addInfoObject(key, value) {
                resultFields.push({
                    key: key,
                    value: !!value ? value : EMPTY
                })
            }

            var resultFields = [];

            if (options.field.showSaveInfo) {
                addInfoObject("saveinfo-user", transferRow.saved.user);
                addInfoObject("saveinfo-time", transferRow.saved.time);
            }

            if (options.fieldOptions.type === 'REFERENCECONTAINER') {
                if (options.field.showReferenceKey) {
                    addInfoObject("reference", transferRow.value);
                }

                if (options.field.showReferenceType
                    || options.field.showReferenceSaveInfo
                    || (options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0)
                    || options.field.showReferenceState) {

                    asyncRequestsMade++;

                    require('./server')('/references/referenceStatus/{value}', transferRow, {
                        method: 'GET',
                        success: function (response) {
                            if (resultParser(response.result).getResult() !== 'REVISION_FOUND') {
                                asyncRequestsReceived++;
                                require('./resultViewer')(response.result);
                                return false;
                            }

                            if (options.field.showReferenceType) {
                                addInfoObject("ref-type", response.type);
                            }

                            if (options.field.showReferenceSaveInfo) {
                                addInfoObject("ref-saved-user", !!response.saved ? response.saved.user : EMPTY);
                                addInfoObject("ref-saved-time", !!response.saved ? response.saved.time : EMPTY);
                            }

                            if (options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0) {
                                $.each(response.approved, function (key, value) {
                                    addInfoObject("ref-approved-user"+(key !== 'DEFAULT' ? "["+key+"]" : ""), value.approved.user);
                                    addInfoObject("ref-approved-time"+(key !== 'DEFAULT' ? "["+key+"]" : ""), value.approved.time);

                                });
                            }

                            if (options.field.showReferenceState) {
                                addInfoObject("ref-state", response.state);
                            }

                            asyncRequestsReceived++;
                        }
                    });
                }
            }

            (columns.map(function (columnWithLang) {
                function addColumnObject(value) {
                    addInfoObject(column+(columnLang !== 'DEFAULT' ? "["+columnLang+"]" : ""), value);
                }
                var columnLang = columnWithLang.lang;
                var column = columnWithLang.key;

                if (options.fieldOptions.type === 'REFERENCECONTAINER') {

                    asyncRequestsMade++;

                    require('./reference').optionByPath(column, options, options.defaultLang, function (option) {
                        var value = require('./selectInputOptionText')(option);

                        addInfoObject(column, value);

                        asyncRequestsReceived++;
                    })(null, null, transferRow.value, transferRow);

                    return;
                }

                // This fetches the correct display text for each table cell. It would make sense to split and generalize this better but the amount of work makes it
                // not a priority at the moment.
                (function () {

                    var columnOptions = $.extend(
                        true
                        , {}
                        , getPropertyNS(options, 'dataConf.fields', column)
                        , options.subfieldConfiguration && options.subfieldConfiguration[column] ? options.subfieldConfiguration[column].field : {});

                    function setText(option) {
                        return require('./selectInputOptionText')(option);
                    }

                    function setOptionText(listOptions) {
                        return setText(listOptions.find(function (option) {
                            return option.value === value;
                        }));
                    }

                    var dataConf = getPropertyNS(options, 'dataConf.fields', column);
                    var type = columnOptions && columnOptions.displayType ? columnOptions.displayType : columnOptions.type;

                    if (!type) {
                        return;
                    }

                    var transferField = getPropertyNS(transferRow, 'fields', column);
                    var value = (function () {
                        if (!transferField || !transferField.type || transferField.type !== 'VALUE') {
                            return;
                        }

                        return require('./data').latestValue(transferField, columnLang);
                    })();

                    if (!value && type !== 'REFERENCE') {
                        addColumnObject(EMPTY);
                        return;
                    }
                    switch (type) {
                        case 'REFERENCE': {
                            var refKey = getPropertyNS(options, 'dataConf.fields', column, 'reference');
                            var reference = getPropertyNS(options, 'dataConf.references', refKey);

                            asyncRequestsMade++;
                            require('./reference').optionByPath(column, options, columnLang, function (option) {
                                asyncRequestsReceived++;

                                addColumnObject(setText(option));
                            })(transferRow.fields, reference, null, transferRow);

                            return;
                        }
                        case 'SELECTION':
                        {
                            var list = require('./selectionList')(options, column);
                            if (!list) {
                                break;
                            }
                            if (list.type === 'REFERENCE') {
                                asyncRequestsMade++;
                                require('./reference').optionsByPath(column, options, lang, function (listOptions) {
                                    asyncRequestsReceived++;

                                    addColumnObject(setOptionText(listOptions));

                                })(transferRow.fields, getPropertyNS(options, 'dataConf.references', list.reference), null, transferRow);

                                return;

                            } else {
                                addColumnObject(setOptionText(list.options));
                                return;
                            }
                        }
                    }

                    addColumnObject(value);

                })();
            }));

            return resultFields;
        }

        function doDownload() {
            if (currentlyDownloading) {
                alert(MetkaJS.L10N.get('general.downloadInfo.currentlyDownloading'));

                return;
            }

            result.rows = [];
            currentlyDownloading = true;
            asyncRequestsMade = 0;
            asyncRequestsReceived = 0;
            var columns = [];

            (options.field.columnFields || [])
                .forEach(function (fieldKey) {
                    // if container is not translatable && subfield is translatable, add columns
                    if (!options.fieldOptions.translatable && getPropertyNS(options, 'dataConf.fields', fieldKey, 'translatable')) {
                        MetkaJS.Languages.forEach(function (lang) {
                            columns.push({
                                key: fieldKey,
                                lang: lang
                            });
                        });
                    } else {
                        columns.push({
                            key: fieldKey,
                            lang: "DEFAULT"
                        });
                    }
                });

            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if(row.removed) {
                    continue;
                }
                var resultRow = {};

                resultRow.fields = tr(row, columns);

                //push completed row to results
                result.rows.push(resultRow);
            }

            waitAsync();
        }

        //as we are using async we need to wait for all responses
        function waitAsync() {
            if (asyncRequestsMade > asyncRequestsReceived) {

                //console.log("asyncRequestsMade: " + asyncRequestsMade);
                //console.log("asyncRequestsReceived: " + asyncRequestsReceived);

                setTimeout(waitAsync, 500);

            } else {
                currentlyDownloading = false;

                //noinspection JSUnresolvedFunction
                saveAs(new Blob([JSON.stringify(result, null, 4)], {type: "text/json;charset=utf-8"}), "hakutulos.json");
            }
        }

        //this object we return to containerField and use create once when we append containerHeader
        //then we call redraw when we need to update rowAmount
        var $pullRight = $('<div class="pull-right">');
        return {
            create: function () {

                return $('<div class="panel-heading">')
                    .append(require('./label')(options, lang, 'panel-title'))
                    .append($pullRight);

            },
            redraw: function () {

                rows = require('./data')(options).getByLang(lang);

                $pullRight.empty();

                if (options.field.showRowAmount) {
                    $pullRight.text(MetkaJS.L10N.get('general.result.amount').supplant({
                        length: require('./data')(options).validRows(lang)
                    }));
                }
                if (options.field.allowDownload) {
                    if(options.field.showRowAmount) {
                        $pullRight.append('&nbsp;');
                    }
                    $pullRight
                        .append($('<div class="btn-group btn-group-xs pull-right">')
                            .append(require('./button')()({
                                title: 'Lataa',
                                style: 'default',
                                create: function () {
                                    this.click(function () {
                                        doDownload();
                                    });
                                }
                            })));
                }
            }
        };
    };
});