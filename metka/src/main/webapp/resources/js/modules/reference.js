define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return {
        options: function (key, options, lang, callback) {
            return function (dataFields, reference) {
                require('./server')('options', {
                    data: JSON.stringify({
                        requests : [{
                            key: key,
                            confType: options.dataConf.key.type,
                            confVersion: options.dataConf.key.version,
                            language: lang,
                            fieldValues: dataFields && reference ? (function () {
                                var response = {};
                                (function addFieldValue(target) {
                                    if (response.hasOwnProperty(target)) {
                                        log('fieldValues should not have property "' + target + '"');
                                        return;
                                    }
                                    response[target] = getPropertyNS(dataFields, target, 'values', lang, 'current');
                                    var ref2 = getPropertyNS(options, 'dataConf.fields', target);
                                    if (ref2 && ref2.type === 'SELECTION') {
                                        var refSelectionList = getPropertyNS(options, 'dataConf.selectionLists', ref2.selectionList);
                                        var ref3 = getPropertyNS(options, 'dataConf.references', refSelectionList.reference);
                                        if (ref3 && ref3.type === 'DEPENDENCY') {
                                            addFieldValue(ref3.target);
                                        }
                                    }
                                })(reference.target);
                                return response;
                            })() : undefined
                        }]
                    }),
                    success: function (data) {
                        callback(getPropertyNS(data, 'responses.0.options') || []);
                    }
                });
            };
        },
        option: function request(key, options, lang, callback) {
            return this.options(key, options, lang, function (options) {
                callback(require('./selectInputOptionText')(options[0]));
            });
        }
    };
});
