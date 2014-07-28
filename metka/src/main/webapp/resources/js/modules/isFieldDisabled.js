define(function (require) {
    return function (options) {
        // TODO: disabled: (field.type === MetkaJS.E.Field.REFERENCE)

        var key = options.field.key;

        // if data should be immutable and original value is set, field is disabled
        if (MetkaJS.objectGetPropertyNS(options, 'dataConf.fields', key, 'immutable') && MetkaJS.objectGetPropertyNS(MetkaJS.data.fields, key, 'originalValue')) {
            return true;
        }

        var editable = MetkaJS.objectGetPropertyNS(options, 'dataConf.fields', key, 'editable');
        return options.readOnly || options.field.readOnly || (typeof editable !== 'undefined' ? editable : false);
    };
});
