define(function (require) {
    'use strict';

    return function (url, requestData, getResults, mapResult, fields, columnFields, getViewRequestOptions, options) {
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
        require('./server')(url, {
            data: JSON.stringify(requestData()),
            success: function (data) {
                var fieldOptions = $.extend(true, options, {
                    buttons: null,
                    dataConf: {
                        fields: fields
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
                        displayType: 'CONTAINER',
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
