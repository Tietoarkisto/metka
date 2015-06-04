define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        var $container = null;
        function appendRow() {
            var transferRow = {
                key: options.field.key,
                removed: false,
                unapproved: true,
                fields: {
                    "country": {
                        key: "country",
                        type: "VALUE",
                        values: {
                            "DEFAULT": {
                                current: "Suomi"
                            }
                        }
                    },
                    "countryabbr": {
                        key: "countryabbr",
                        type: "VALUE",
                        values: {
                            "DEFAULT": {
                                current: "FI"
                            }
                        }
                    }
                }
            };

            $container.data('addRow')(transferRow);
        }

        return {
            preCreate: function (options) {
                var $elem = this;
                if(!options.buttons) {
                    options.buttons = [];
                }
                if(!options.buttons.some(function(button){
                        if(button.buttonId) {
                            return button.buttonId === options.field.key+"_addFinland"
                        }
                        return false;
                    })) {
                    options.buttons.push({
                        buttonId: options.field.key+"_addFinland",
                        create: function () {
                            this
                                .text(MetkaJS.L10N.get('general.table.countries.addFinland'))
                                .click(function () {
                                    appendRow();
                                });
                        }
                    })
                }
            },
            postCreate: function(options) {
                $container = $(this).children();
            }
        }
    };
});
