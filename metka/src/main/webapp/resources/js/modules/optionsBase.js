define(function (require) {
    'use strict';

    return function(parent) {
        return {
            header: 'Metka',
            parent: parent,
            isReadOnly: function(options) {
                if(!options) {
                    return false;
                }

                if(!!options.readOnly) {
                    return true;
                }

                if(!options.parent) {
                    return false;
                }

                return options.isReadOnly(options.parent);
            },
            content: [],
            body: null,
            data: {},
            dataConf: {},
            $events: $({}),
            defaultLang: 'DEFAULT'
        }
    }
});