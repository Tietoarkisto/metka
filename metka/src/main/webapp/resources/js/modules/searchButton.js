define(function (require) {
    'use strict';

    return function (url, requestData, results, mapResult, fields, columnFields, trOnClick) {
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
                                    style: 'primary',
                                    readOnly: true,
                                    field: {
                                        displayType: 'CONTAINER',
                                        key: "searchResults",
                                        columnFields: columnFields
                                    }
                                };
                                require('./data').set(fieldOptions, 'searchResults', results(data).map(mapResult));
                                $('#searchResultTable').remove();



                                var $field = require('./field').call($('<div>'), fieldOptions)
                                    .attr('id', 'searchResultTable');

                                $field.find('table')
                                    .addClass('table-hover')
                                    .find('tbody')
                                    .on('click', 'tr', trOnClick);

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
