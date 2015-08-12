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

        function tr(transferRow, resultFields, columns) {

            if (options.fieldOptions.type === 'REFERENCECONTAINER') {
                if (options.field.showReferenceValue) {
                }

                if (options.field.showReferenceType) {
                }
            }

            (columns.map(function (columnWithLang) {
                var columnLang = columnWithLang.lang;
                var column = columnWithLang.key;

                if (options.fieldOptions.type === 'REFERENCECONTAINER') {

                    asyncRequestsMade++;

                    require('./reference').optionByPath(column, options, options.defaultLang, function (option) {
                        var columnOptions = $.extend(
                            true
                            , {}
                            , getPropertyNS(options, 'dataConf.fields', column)
                            , options.subfieldConfiguration && options.subfieldConfiguration[column] ? options.subfieldConfiguration[column].field : {});

                        if (columnOptions && columnOptions.displayType === 'LINK') {
                            require('./inherit')(function (options) {
                                require('./linkField')($td, options, "DEFAULT");
                            })(options)({
                                fieldOptions: getPropertyNS(options, 'dataConf.fields', column),
                                field: columnOptions,
                                data: $.extend({}, options.data, {fields: transferRow.fields})
                            });
                        } else {
                            var value = require('./selectInputOptionText')(option);

                            var tmpResult = {};

                            tmpResult.key = column;
                            tmpResult.value = (!!value && value.length ? value : EMPTY);
                            resultFields.push(tmpResult);

                            asyncRequestsReceived++;

                        }
                    })(null, null, transferRow.value);

                    return;
                }

                // This fetches the correct display text for each table cell. It would make sense to split and generalize this better but the amount of work makes it
                // not a priority at the moment.
                (function () {

                    var tmpResult = {};

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
                        log('not implemented', column);
                        return;
                    }

                    var transferField = getPropertyNS(transferRow, 'fields', column);
                    var value = (function () {
                        if (!transferField) {
                            return;
                        }

                        if (!transferField.type) {
                            log('transferField type not set (column: {column})'.supplant({
                                column: column
                            }));
                            return;
                        }

                        if (transferField.type !== 'VALUE') {
                            log('not implemented (type: {type}, column: {column})'.supplant({
                                type: transferField.type,
                                column: column
                            }));
                            return;
                        }

                        return require('./data').latestValue(transferField, columnLang);
                    })();

                    if (!value && type !== 'REFERENCE') {

                        tmpResult = {};
                        if (columnLang === "DEFAULT") {
                            tmpResult.key = column;
                        } else {
                            tmpResult.key = column + "[" + columnLang + "]";
                        }
                        tmpResult.value = (!!value && value.length ? value : EMPTY);
                        resultFields.push(tmpResult);

                        return;
                    }
                    switch (type) {
                        case 'REFERENCE':
                        {
                            var refKey = getPropertyNS(options, 'dataConf.fields', column, 'reference');
                            var reference = getPropertyNS(options, 'dataConf.references', refKey);

                            asyncRequestsMade++;
                            require('./reference').optionByPath(column, options, columnLang, function (option) {
                                asyncRequestsReceived++;

                                value = setText(option);

                                var tmpResult = {};
                                if (columnLang === "DEFAULT") {
                                    tmpResult.key = column;
                                } else {
                                    tmpResult.key = column + "[" + columnLang + "]";
                                }
                                tmpResult.value = (!!value && value.length ? value : EMPTY);
                                resultFields.push(tmpResult);

                            })(transferRow.fields, reference);

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

                                    value = setOptionText(listOptions);

                                    var tmpResult = {};

                                    if (columnLang === "DEFAULT") {
                                        tmpResult.key = column;
                                    } else {
                                        tmpResult.key = column + "[" + columnLang + "]";
                                    }

                                    tmpResult.value = (!!value && value.length ? value : EMPTY);
                                    resultFields.push(tmpResult);

                                })(transferRow.fields, getPropertyNS(options, 'dataConf.references', list.reference));

                                return;

                            } else {

                                value = setOptionText(list.options);
                            }
                            break;
                        }
                    }

                    tmpResult = {};
                    if (columnLang === "DEFAULT") {
                        tmpResult.key = column;
                    } else {
                        tmpResult.key = column + "[" + columnLang + "]";
                    }
                    tmpResult.value = (!!value && value.length ? value : EMPTY);
                    resultFields.push(tmpResult);

                })();
            }));

            if (options.field.showSaveInfo) {
            }

            if (options.fieldOptions.type === 'REFERENCECONTAINER') {

                if (options.field.showReferenceSaveInfo) {
                }
                if (options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0) {
                    $.each(options.field.showReferenceApproveInfo, function (index, lang) {
                    });
                }
                if (options.field.showReferenceState) {
                }
                if (options.field.showReferenceType
                    || options.field.showReferenceSaveInfo
                    || (options.field.showReferenceApproveInfo && options.field.showReferenceApproveInfo.length > 0)
                    || options.field.showReferenceState) {

                    asyncRequestsMade++;

                    require('./server')('/references/referenceStatus/{value}', transferRow, {
                        method: 'GET',
                        success: function (response) {

                            var tmpResult = {};

                            if (resultParser(response.result).getResult() !== 'REVISION_FOUND') {
                                require('./resultViewer')(response.result);
                                return false;
                            }

                            if (response.type && options.field.showReferenceType) {
                                tmpResult = {};
                                tmpResult.key = "ref-type";
                                tmpResult.value = (!!response.type && response.type.length ? response.type : EMPTY);
                                resultFields.push(tmpResult);
                            }

                            if (response.saved && options.field.showReferenceSaveInfo) {
                                tmpResult = {};
                                tmpResult.key = "ref-saved-user";
                                tmpResult.value = (!!response.saved.user && response.saved.user.length ? response.saved.user : EMPTY);
                                resultFields.push(tmpResult);

                                tmpResult = {};
                                tmpResult.key = "ref-saved-time";
                                tmpResult.value = (!!response.saved.time && response.saved.time.length ? response.saved.time : EMPTY);
                                resultFields.push(tmpResult);

                            }

                            if (options.field.showReferenceApproveInfo.length > 0) {
                                $.each(response.approved, function (key, value) {
                                    console.log(key);
                                    console.log(value);
                                    console.log(value.approved);

                                    tmpResult = {};
                                    if (key === "DEFAULT") {
                                        tmpResult.key = "ref-approved-user";
                                    } else {
                                        tmpResult.key = "ref-approved-user[" + key + "]";
                                    }
                                    tmpResult.value = (!!value.approved.user && value.approved.user.length ? value.approved.user : EMPTY);
                                    resultFields.push(tmpResult);

                                    tmpResult = {};
                                    if (key === "DEFAULT") {
                                        tmpResult.key = "ref-approved-time";
                                    } else {
                                        tmpResult.key = "ref-approved-time[" + key + "]";
                                    }
                                    tmpResult.value = (!!value.approved.time && value.approved.time.length ? value.approved.time : EMPTY);
                                    resultFields.push(tmpResult);

                                });
                            }

                            tmpResult = {};
                            tmpResult.key = "ref-state";

                            if (response.state) {
                                tmpResult.value = response.state;
                                resultFields.push(tmpResult);

                            } else {
                                tmpResult.value = EMPTY;
                                resultFields.push(tmpResult);
                            }

                            asyncRequestsReceived++;
                        }
                    });
                }
            }
        }

        function appendRow(resultFields, transferRow, columns) {
            function append() {
                tr(transferRow, resultFields, columns);
            }

            if (!transferRow.removed) {
                return append();
            } else {
            }
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
                        ['DEFAULT', 'EN', 'SV'].forEach(function (lang) {
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

                var resultFields = [];
                var resultRow = {};

                if (options.fieldOptions.type === 'REFERENCECONTAINER' && options.field.showReferenceType) {
                    //add reference to our row
                    var resultFieldReference = {};
                    resultFieldReference.key = "reference";
                    resultFieldReference.value = rows[i].value;
                    resultFields.push(resultFieldReference);
                }

                if (options.field.showSaveInfo) {
                    var saveInfo = {};
                    saveInfo.key = "saveinfo-user";
                    saveInfo.value = rows[i].saved.user;
                    resultFields.push(saveInfo);

                    saveInfo = {};
                    saveInfo.key = "saveinfo-time";
                    saveInfo.value = rows[i].saved.time;
                    resultFields.push(saveInfo);
                }

                //get other values by references
                appendRow(resultFields, rows[i], columns);

                resultRow.fields = resultFields;

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
                    /*var $tmp = $('<div>');
                     $tmp.text(MetkaJS.L10N.get('general.result.amount').supplant(rows));

                     $pullRight.append($tmp);*/

                    $pullRight.text(MetkaJS.L10N.get('general.result.amount').supplant(rows))
                        .append('&nbsp;');
                }
                if (options.field.allowDownload) {
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