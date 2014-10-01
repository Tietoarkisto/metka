define(function (require) {
    'use strict';

    return function ($field, options, lang) {
        var key = options.field.key;
        var dataConf = options.dataConf.fields[key];

        if (dataConf.type === 'REFERENCE') {

            var reference = require('./utils/getPropertyNS')(options, 'dataConf.references', dataConf.reference);
            var getOptions = require('./reference').options(key, options, lang, function (listOptions) {
                var value = require('./data')(options).getByLang(lang);
                var option = listOptions.find(function (option) {
                    return option.value === value;
                });

                if (option) {
                    $field.append($('<a>', {
                        text: '{label} - {value}'.supplant({
                            label: MetkaJS.L10N.get('type.{target}.title'.supplant(reference)),
                            value: require('./selectInputOptionText')(option)
                        }),
                        href: require('./url')('view', {
                            PAGE: reference.target,
                            id: option.value,
                            no: ''
                        })
                    }));
                }
            });
            if (!reference) {
                return
            }
            if (reference.type === 'DEPENDENCY') {
                // TODO: merge code with other users of ´reference´ module
                log('not implemented');
            } else {
                getOptions();
            }
        }
    };
});

