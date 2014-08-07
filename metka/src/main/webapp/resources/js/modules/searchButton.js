define(function (require) {
    'use strict';

    return function (url, requestData, getResults, mapResult, fields, columnFields, trOnClick) {
        return {
            "&title": {
                "default": "Tee haku"
            },
            create: function () {
                this
                    .click(function () {
                        require('./server')(url, {
                            data: JSON.stringify(requestData()),
                            success: function (data) {
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

                                fieldOptions.data.fields.searchResults.rows = results.map(mapResult).map(require('./map/object/transferRow'));
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
                    })
            }
        };
    };
});
