define(function (require) {
    'use strict';

    return function (options) {
        // TODO: share code with containerField rowDialog
        function rowDialog(title, button) {
            return function (transferRow, onClose) {
                // copy data, so if dialog is dismissed, original data won't change
                var transferRowCopy = $.extend(true, {}, transferRow);

                var containerOptions = {
                    title: MetkaJS.L10N.get(['dialog', PAGE, key, title].join('.')),
                    data: transferRowCopy,
                    dataConf: options.dataConf,
                    $events: $({}),
                    defaultLang: fieldOptions.translatable ? lang : options.defaultLang,
                    content: [
                        {
                            type: 'COLUMN',
                            columns: 1,
                            rows: [{
                                type: 'ROW',
                                cells: [{
                                    "type": "CELL",
                                    "title": "Ryhmän nimi",
                                    "field": {
                                        "key": "vargrouptitle"
                                    }
                                }]
                            }, {
                                type: 'ROW',
                                cells: [{
                                    "type": "CELL",
                                    "title": "Ryhmän tekstit",
                                    "field": {
                                        "key": "vargrouptexts",
                                        "displayHeader": false,
                                        "columnFields": [
                                            "vargrouptext"
                                        ]
                                    }
                                }]
                            }]
                        }
                    ],
                    buttons: [
                        {
                            create: function () {
                                this
                                    .text(MetkaJS.L10N.get('general.buttons.' + button))
                                    .click(function () {
                                        $.extend(transferRow, transferRowCopy);
                                        onClose();
                                    });
                            }
                        },
                        {
                            type: 'CANCEL'
                        }
                    ]
                };

                var $modal = require('./../../modal')(containerOptions);

                // if not translatable container and has translatable subfields, show language selector
                if (!fieldOptions.translatable && require('./../../containerHasTranslatableSubfields')(options)) {
                    $modal.find('.modal-header').append(require('./../../languageRadioInputGroup')(containerOptions, 'dialog-translation-lang', $('input[name="translation-lang"]:checked').val()));
                }
            };
        }

        var PAGE = require('./../../../metka').PAGE;
        var getPropertyNS = require('./../../utils/getPropertyNS');
        var key = 'vargroups';
        var fieldOptions = getPropertyNS(options, 'dataConf.fields', key) || {};

        return {
            create: function create(options) {
                var key = 'variables';
                var column = 'varlabel';

                var $elem = this;


                require('./../../data')(options).onChange(function onChange() {

                    require('./../../preloader')($elem);
                    var rows = (function () {
                        return require('./../../data')(options)(key).getByLang(options.defaultLang);
                    })();
                    if (rows) {
                        require('./../../server')('options', {
                            data: JSON.stringify({
                                key: key,
                                requests: rows.map(function (transferRow) {
                                    var fieldValues = {};
                                    fieldValues[key] = transferRow.value;
                                    return {
                                        key: column,
                                        container: key,
                                        confType: options.dataConf.key.type,
                                        confVersion: options.dataConf.key.version,
                                        fieldValues: fieldValues
                                    }
                                })
                            }),
                            success: function (data) {
                                var variables = data.responses.map(function (response) {
                                    return {
                                        text: response.options[0].title.value,
                                        value: response.fieldValues.variables
                                    };
                                });

                                $elem.empty().append(require('./../../treeView')((require('./../../data')(options)('vargroups').getByLang(options.defaultLang) || []).filter(function (row) {
                                    return row.fields && row.fields.vargrouptitle;
                                }).map(function (transferRow) {
                                    return require('./../../treeViewVariableGroup')(
                                        transferRow.fields.vargrouptitle.values.DEFAULT.current,
                                        transferRow.fields.vargroupvars ? transferRow.fields.vargroupvars.rows.DEFAULT.map(function (transferRow) {
                                            return {
                                                text: variables.find(function (variable) {
                                                    return variable.value === transferRow.value;
                                                }).text,
                                                transferRow: transferRow
                                            };
                                        }) : [],
                                        transferRow
                                    );
                                }), {
                                    onClick: function (node) {
                                        if (!node.children) {
                                            require('./../../variableModal')(options.field.key, {
                                                id: node.transferRow.value
                                            }, onChange);
                                        } else {
                                            rowDialog('modify', 'ok')(node.transferRow, onChange);
                                        }
                                    }
                                }));
                            }
                        });
                    }
                });
            }
        };
    };
});
