define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    function data(options) {
        function io(key) {
            function byFieldKey(key) {
                return io(key);
            }

            function getTransferField(createIfUndefined) {
                var transferField = getPropertyNS(options, 'data.fields', key);

                if (transferField) {
                    return transferField;
                }

                var type = getPropertyNS(options, 'dataConf.fields', key, 'type');
                if (type !== 'CONTAINER' && type !== 'REFERENCECONTAINER') {
                    type = 'VALUE';
                }
                if (createIfUndefined) {
                    return require('./utils/setPropertyNS')(options, 'data.fields', key, {
                        key: key,
                        type: type
                    })
                }
            }

            byFieldKey.getByLang = function (lang) {
                var transferField = getTransferField();

                if (transferField) {
                    if (transferField.type === 'VALUE') {
                        return data.latestValue(transferField, lang);
                    } else {
                        return getPropertyNS(transferField, 'rows', lang);
                    }
                }
            };
            byFieldKey.errors = function () {
                var transferField = getTransferField();

                if (transferField) {
                    return transferField.errors || [];
                }
                return [];
            };
            byFieldKey.errorsByLang = function (lang) {
                var transferField = getTransferField();

                if (transferField && transferField.values && transferField.values[lang]) {
                    return transferField.values[lang].errors || [];
                }
                return [];
            };
            /*byFieldKey.set = function (value) {
             var transferField = getTransferField(true);

             transferField.value = transferField.value || {};
             transferField.type = transferField.type || 'VALUE';
             transferField.value.current = value;
             };*/
            byFieldKey.setByLang = function (lang, value) {
                var transferField = getTransferField(true);

                transferField.values = transferField.values || {};
                transferField.values[lang] = transferField.values[lang] || {};
                transferField.type = transferField.type || 'VALUE';


                transferField.values[lang].current = value;
                options.$events.trigger('data-changed-{key}-{lang}'.supplant({
                    key: key,
                    lang: lang
                }), [value]);
            };
            /*byFieldKey.append = function (trasferRow) {
             var transferField = getTransferField(true);

             transferField.rows = transferField.rows || [];
             transferField.type = transferField.type || 'CONTAINER';
             trasferRow.key = trasferRow.key || key;

             transferField.rows.push(trasferRow);
             };*/
            byFieldKey.appendByLang = function (lang, trasferRow) {
                var transferField = getTransferField(true);

                transferField.rows = transferField.rows || {};
                transferField.rows[lang] = transferField.rows[lang] || [];
                trasferRow.key = trasferRow.key || key;

                transferField.rows[lang].push(trasferRow);
            };
            byFieldKey.onChange = function (callback) {
                callback();
                if (options.$events) {
                    // TODO: välitä data tai errors callbackille, koska se lähes aina tarvitsee jotain
                    options.$events.on('dataChanged', callback);
                }
            };

            return byFieldKey;
        }

        return io(options.field ? options.field.key : undefined);
    }

    data.latestValue = function (transferField, lang) {
        var current = getPropertyNS(transferField, 'values', lang, 'current');
        if (MetkaJS.exists(current)) {
            return current;
        }
        return getPropertyNS(transferField, 'values', lang, 'original');
    };

    return data;
});
