define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        if(options.data.state.saved && options.data.state.saved.user) {
            require('../../data')(options).setByLang('DEFAULT', options.data.state.saved.user);
        }

        return {
            "dataConf": {
                "fields": {
                    "savedBy": {
                        "key": "savedBy",
                        "type": "STRING",
                        "editable": false,
                        "translatable": false
                    }
                }
            }
        }
    };
});
