define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return function (key, reference, options, lang, dataFields, callback) {
        require('./server')('options', {
            data: JSON.stringify({
                requests : [{
                    key: key,
                    confType: options.dataConf.key.type,
                    confVersion: options.dataConf.key.version,
                    language: lang,
                    fieldValues: (function () {
                        var fieldValues = {};
                        (function addFieldValue(target) {
                            if (!fieldValues.hasOwnProperty(target)) {
                                fieldValues[target] = dataFields[target].values[lang].current;
                                var ref2 = getPropertyNS(options, 'dataConf.fields', target);
                                if (ref2 && ref2.type === 'SELECTION') {
                                    var refSelectionList = getPropertyNS(options, 'dataConf.selectionLists', ref2.selectionList);
                                    var ref3 = getPropertyNS(options, 'dataConf.references', refSelectionList.reference);
                                    if (ref3 && ref3.type === 'DEPENDENCY') {
                                        addFieldValue(ref3.target);
                                    }
                                }
                            }
                        })(reference.target);
                        return fieldValues;
                    })()
                }]
            }),
            success: function (data) {
                var option = getPropertyNS(data, 'responses.0.options.0');
                if (option) {
                    callback(require('./selectInputOptionText')(option));
                } else {
                    callback();
                }
            }
        });
    };
});
