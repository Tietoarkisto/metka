define(function (require) {
    'use strict';

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
                        (function addFieldValue(reference) {
                            var target = reference.target;
                            if (!fieldValues.hasOwnProperty(target)) {
                                fieldValues[target] = dataFields[reference.target].values[lang].current;
                                var ref2 = require('./utils/getPropertyNS')(options, 'dataConf.fields', target);
                                if (ref2 && ref2.type === 'SELECTION') {
                                    var refSelectionList = require('./utils/getPropertyNS')(options, 'dataConf.selectionLists', ref2.selectionList);
                                    var ref3 = require('./utils/getPropertyNS')(options, 'dataConf.references', refSelectionList.reference);
                                    if (ref3 && ref3.type === 'DEPENDENCY') {
                                        addFieldValue(ref3);
                                    }
                                }
                            }
                        })(reference);
                        return fieldValues;
                    })()
                }]
            }),
            success: function (data) {
                if (data.responses && data.responses.length && data.responses[0].options && data.responses[0].options.length) {
                    callback(require('./selectInputOptionText')(data.responses[0].options[0]));
                }
            }
        });
    };
});
