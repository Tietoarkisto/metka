define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        if(options.data.state.saved && options.data.state.saved.time) {
            require('../../data')(options).setByLang('DEFAULT', options.data.state.saved.time);
        }

        return {
            "dataConf": {
                "fields": {
                    "savedAt": {
                        "key": "savedAt",
                        "type": "DATE",
                        "editable": false,
                        "translatable": false
                    }
                }
            }
        }
    };
});
