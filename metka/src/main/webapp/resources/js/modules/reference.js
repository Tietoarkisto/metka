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

                                    var ref2 = getPropertyNS(options, 'dataConf.fields', target);

                                    if (!ref2) {
                                        return;
                                    }

                                    response[target] = require('./data').latestValue(dataFields[target], lang);
                                    var ref3 = (function () {
                                        if (ref2.type === 'SELECTION') {
                                            var refSelectionList = getPropertyNS(options, 'dataConf.selectionLists', ref2.selectionList);
                                            return getPropertyNS(options, 'dataConf.references', refSelectionList.reference);
                                        }
                                        if (ref2.type === 'REFERENCE') {
                                            return getPropertyNS(options, 'dataConf.references', ref2.reference);
                                        }
                                    })();

                                    if (ref3 && ref3.type === 'DEPENDENCY') {
                                        addFieldValue(ref3.target);
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
        optionsByPath: function (key, options, lang, callback) {
            return function (dataFields, reference) {

                // TODO: This should always be called with reference, also reference fetching should be generalized somewhere
                if(!reference) {
                    var target = getPropertyNS(options, 'dataConf.fields', key);
                    if(target.type === "REFERENCE") {
                        reference = getPropertyNS(options, 'dataConf.references', target.reference);
                    } else if(target.type === "SELECTION") {
                        var list = getPropertyNS(options, 'dataConf.selectionLists', target.selectionList);
                        if(list.type === "REFERENCE") {
                            reference = getPropertyNS(options, 'dataConf.references', list.reference);
                        }
                    }
                }
                var root = function r(key, dataFields, lang, reference, next) {
                    var path = {
                        reference: reference,
                        value: dataFields && dataFields[key] ? require('./data').latestValue(dataFields[key], lang) : undefined,
                        next: next
                    };

                    if(reference && reference.type === "DEPENDENCY") {
                        var target = getPropertyNS(options, 'dataConf.fields', reference.target);
                        var targetRef = null;
                        if(target.type === "REFERENCE") {
                            targetRef = getPropertyNS(options, 'dataConf.references', target.reference);
                        } else if(target.type === "SELECTION") {
                            var list = getPropertyNS(options, 'dataConf.selectionLists', target.selectionList);
                            if(list.type === "REFERENCE") {
                                targetRef = getPropertyNS(options, 'dataConf.references', list.reference);
                            }
                        }
                        var prev = r(reference.target, dataFields, lang, targetRef, path);
                        if(prev) {
                            return prev;
                        } else {
                            return path;
                        }
                    }

                    return path;
                }(key, dataFields, lang, reference);

                var cur = root;
                while(cur.next) {
                    if(!cur.value) {
                        callback([]);
                        return;
                    } else {
                        cur = cur.next;
                    }
                }

                require('./server')('optionsByPath', {
                    data: JSON.stringify({
                        requests : [{
                            key: key,
                            container: "",
                            language: MetkaJS.L10N.locale.toUpperCase(),
                            root: root
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
        },
        optionByPath: function request(key, options, lang, callback) {
            return this.optionsByPath(key, options, lang, function (options) {
                callback(require('./selectInputOptionText')(options[0]));
            });
        }
    };
});
