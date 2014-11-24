define(function (require) {
    'use strict';
    var getPropertyNS = require('./utils/getPropertyNS');
    return function (url, requestConf, getResults, mapResult, fields, columnFields, getViewRequestOptions, options) {
        function trOnClick(transferRow) {
            var viewRequestOptions = {
                id: transferRow.fields.id.values.DEFAULT.current,
                no: transferRow.fields.no.values.DEFAULT.current
            };
            if (getViewRequestOptions) {
                $.extend(viewRequestOptions, getViewRequestOptions(transferRow));
            }
            require('./assignUrl')('view', viewRequestOptions);
        }

        var data = require('./data')(options);
        require('./server')(url, {
            data: JSON.stringify((function () {
                if (typeof requestConf === 'function') {
                    return requestConf();
                }
                if (Array.isArray(requestConf)) {
                    var requestData = require('./commonSearchBooleans').requestData(options, {
                        type: require('./../metka').PAGE,
                        values: {}
                    });
                    requestConf.map(function (field) {
                            // validate input and (in case of string) transform to object
                            switch (typeof field) {
                                case 'string':
                                    return {
                                        key: field
                                    };
                                case 'object':
                                    if (field !== null) {
                                        return field;
                                    }
                            }
                            throw 'Illegal search configuration entry.';
                        }).map(function (searchOptions) {
                            // set defaults
                            return $.extend({
                                useSelectionText: true,
                                addSelectionQuotes: true,
                                addParens: true
                            }, searchOptions);
                        }).forEach(function (searchOptions) {
                            var key = searchOptions.key;
                            var value = data(searchOptions.key).getByLang(options.defaultLang) || '';
                            if (require('./utils/getPropertyNS')(options, 'dataConf.fields', key, 'type') === 'SELECTION') {
                                if (value && searchOptions.useSelectionText) {
                                    value = $('select[data-metka-field-key="{key}"][data-metka-field-lang="{lang}"] option[value="{value}"]'.supplant({
                                        key: key,
                                        lang: options.defaultLang,
                                        value: value
                                    })).text();
                                }
                                if (value && searchOptions.addSelectionQuotes) {
                                    value = '"' + value + '"';
                                }
                            }
                            if (value && searchOptions.addParens) {
                                value = '(' + value + ')';
                            }
                            if (value) {
                                requestData.values[searchOptions.rename || key] = value;
                            }
                        });
                    return requestData;
                }
                throw 'Illegal search request';
            })()),
            success: function (data) {
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

                $('.content').children('.searchResults').remove();

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
                $('.content').append($field);
            }
        });
    };
});
