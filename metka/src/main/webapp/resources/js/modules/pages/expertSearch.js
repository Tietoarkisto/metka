define(function (require) {
    return {
        header: MetkaJS.L10N.get('topmenu.expert'),
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
                                }
                            },
                            {
                                "type": "CELL",
                                "title": "Tallennetut haut",
                                "colspan": 1,
                                "field": {
                                    "readOnly": true,
                                    "displayType": "CONTAINER",
                                    "key": "savedSearches",
                                    "columnFields": [
                                        "name",
                                        "user",
                                        "date",
                                        "remove"
                                    ]
                                }
                            }
                        ]
                    }
                ]
            }
        ],
        buttons: [{
            "&title": {
                "default": "Tee haku"
            },
            create: function () {
                this
                    .click(function () {
                        $.ajax({
                            type: 'POST',
                            data: JSON.stringify({
                                operation: 'QUERY',
                                data: MetkaJS.Data.get('search')
                            }),
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            dataType: 'json',
                            url: require('./../url')('/expertSearch/query'),
                            success: function (data) {
                                log(data);
                                return;
                                MetkaJS.Data.set('searchResults', data.searchData.results.map(function (result) {
                                    return {
                                        id: result.id,
                                        revision: result.revision,
                                        seriesid: result.id,
                                        seriesabbr: result.values.seriesabb,
                                        seriesname: result.values.seriesname,
                                        state: MetkaJS.L10N.get('search.result.state.{state}'.supplant(result))
                                    };
                                }));
                                $('#searchResultTable').remove();
                                var $field = $.metka.metkaField({
                                    style: 'primary',
                                    "field": {
                                        "key": "searchResults",
                                        "columnFields": [
                                            "seriesid",
                                            "seriesabbr",
                                            "seriesname",
                                            "state"
                                        ]
                                    }
                                }).element
                                    .attr('id', 'searchResultTable');
                                $field.find('table')
                                    .addClass('table-hover')
                                    .find('tbody')
                                    .addClass('table-hover')
                                    .on('click', 'tr', function () {
                                        var $this = $(this);
                                        MetkaJS.view($this.data('id'), $this.data('revision'));
                                    });
                                $field.find('.panel-heading')
                                    .text(MetkaJS.L10N.get('search.result.title'))
                                    .append($('<div class="pull-right">')
                                        .text(MetkaJS.L10N.get('search.result.amount').supplant(data.searchData.results)));
                                $('#dynamicContent').append($field);
                            }
                        });
                    })
            }
        }, {
            "&title": {
                "default": "Tyhjennä"
            },
            "type": "SAVE"
        }, {
            "&title": {
                "default": "Tallenna haku"
            },
            "type": "SAVE"
        }],
        data: {
        },
        dataConf: {}
    };
});