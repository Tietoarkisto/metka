define(function (require) {
    'use strict';

    return function (options) {

        //this.options.field.multichoice
        //this.options.field.showReferenceKey
        //this.options.field.showReferenceValue
        //this.options.field.handlerName

        //log(this.options.dataConf === x.dataConf)
        //log(this.options.dataConf.mo === x.dataConf.mo)
        var type = options.field.displayType || MetkaJS.objectGetPropertyNS(options, 'dataConf.fields', options.field.key, 'type');

        if (!type) {
            log('field type is not set', options);
            return this;
        }

        if (type === 'CONTAINER') {
            require('./containerField').call(this, options);
        } else {
            if (type === 'CHECKBOX') {
                require('./checkboxField').call(this, options);
            } else {
                require('./inputField').call(this, options, type);
            }
        }

        return this;
    };
});