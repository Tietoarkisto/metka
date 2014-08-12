define(function (require) {
    'use strict';

    return function (options) {
        function io(key) {
            function byFieldKey(key) {
                return io(key);
            }

            function getTransferField(createIfUndefined) {
                var transferField = require('./utils/getPropertyNS')(options, 'data.fields', key);

                if (transferField) {
                    return transferField;
                }

                if (createIfUndefined) {
                    return require('./utils/setPropertyNS')(options, 'data.fields', key, {
                        key: key
                    })
                }
            }

            var getPropertyNS = require('./utils/getPropertyNS');

            byFieldKey.get = function () {
                var transferField = getTransferField();

                if (transferField) {
                    if (transferField.type === 'VALUE') {
                        var current = getPropertyNS(transferField, 'value.current');
                        if (MetkaJS.exists(current)) {
                            return current;
                        }
                        return getPropertyNS(transferField, 'value.original');
                    } else {
                        return getPropertyNS(transferField, 'rows');
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
            byFieldKey.set = function (value) {
                var transferField = getTransferField(true);

                transferField.value = transferField.value || {};
                transferField.type = transferField.type || 'VALUE';
                transferField.value.current = value;
            };
            byFieldKey.append = function (trasferRow) {
                var transferField = getTransferField(true);

                transferField.rows = transferField.rows || [];
                transferField.type = transferField.type || 'CONTAINER';
                trasferRow.key = trasferRow.key || key;

                transferField.rows.push(trasferRow);
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
    };
});
