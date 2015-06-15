define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    return {
        optionsByPath: function (key, options, lang, callback) {
            return function (dataFields, reference, rowValue) {

                // TODO: This should always be called with reference, also reference fetching should be generalized somewhere
                var target = getPropertyNS(options, 'dataConf.fields', key);
                if (!dataFields && options.data) {
                    dataFields = options.data.fields;
                }
                if(!reference) {
                    if(target.type === "REFERENCE" || target.type === 'REFERENCECONTAINER') {
                        reference = getPropertyNS(options, 'dataConf.references', target.reference);
                    } else if(target.type === "SELECTION") {
                        var list = getPropertyNS(options, 'dataConf.selectionLists', target.selectionList);
                        if(list.type === "REFERENCE") {
                            reference = getPropertyNS(options, 'dataConf.references', list.reference);
                        }
                    }
                }
                var root = function r(currentKey, dataFields, lang, reference, next) {
                    var path = {
                        reference: reference,
                        value: (function() {
                            var target = getPropertyNS(options, 'dataConf.fields', currentKey);
                            if(target.type === 'REFERENCECONTAINER') {
                                return rowValue;
                            } else {
                                return dataFields && dataFields[currentKey] ? require('./data').latestValue(dataFields[currentKey], lang) : undefined;
                            }
                        })(),
                        next: next
                    };

                    if(reference && reference.type === "DEPENDENCY") {
                        var target = getPropertyNS(options, 'dataConf.fields', reference.target);
                        var targetRef = null;
                        if(target.type === "REFERENCE" || target.type === 'REFERENCECONTAINER') {
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
                while (cur.next) {
                    if(!cur.value) {
                        callback([]);
                        return;
                    } else {
                        cur = cur.next;
                    }
                }

                if (target.type === 'SELECTION') {
                    cur.value = null;
                }

                require('./server')('optionsByPath', {
                    data: JSON.stringify({
                        requests : [{
                            key: key,
                            container: "",
                            language: MetkaJS.L10N.locale.toUpperCase(),
                            root: root,
                            returnFirst: target.type === 'REFERENCE'
                        }]
                    }),
                    success: function (data) {
                        callback(getPropertyNS(data, 'responses.0.options') || []);
                    }
                });
            };
        },
        optionByPath: function request(key, options, lang, callback) {
            return this.optionsByPath(key, options, lang, function (options) {
                callback(require('./selectInputOptionText')(options[0]));
            });
        }
    };
});
