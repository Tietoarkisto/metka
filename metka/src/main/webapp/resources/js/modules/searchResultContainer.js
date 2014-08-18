define(function (require) {
    'use strict';

    return function (url, requestData, getResults, mapResult, fields, columnFields, trOnClick) {
        require('./server')(url, {
            data: JSON.stringify(requestData()),
            success: function (data) {
                data = {
                    studies: [{
                        id: 1,
                        title: 'mooo'
                    }, {
                        id: 2,
                        title: 'moo2'
                    }]
                };
                var fieldOptions = {
                    dataConf: {
                        fields: fields
                    },
                    data: {
                        fields: {
                            searchResults: {
                                type: 'CONTAINER'
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
                };
                var results = getResults(data);
                log(data)
                fieldOptions.data.fields.searchResults.rows = results.map(mapResult).map(require('./map/object/transferRow'));

                // if exactly 1 search result, perform the row action
                if (fieldOptions.data.fields.searchResults.rows.length === 1) {
                    trOnClick(fieldOptions.data.fields.searchResults.rows[0]);
                    return;
                }

                $('#searchResultTable').remove();

                var $field = require('./field').call($('<div>'), fieldOptions)
                    .attr('id', 'searchResultTable');

                $field.find('.panel-heading')
                    .text(MetkaJS.L10N.get('search.result.title'))
                    .append($('<div class="pull-right">')
                        .text(MetkaJS.L10N.get('search.result.amount').supplant(results)));
                $('.content').append($field);
            }
        });
    };
});
