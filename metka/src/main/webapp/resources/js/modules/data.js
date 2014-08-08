define(function (require) {
    'use strict';

    return function (options) {
        function io(key) {
            function byFieldKey(key) {
                return io(key);
            }

            var getPropertyNS = require('./utils/getPropertyNS');

            function getTransferField(createIfUndefined) {
                var transferField = require('./utils/getPropertyNS')(options, 'data.fields', key);

                if (transferField) {
                    return transferField;
                }

                if (createIfUndefined) {
                    return require('./utils/setPropertyNS')(options, 'data.fields', key, {})
                }
            }

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
                transferField.rows.push(trasferRow);
            };

            return byFieldKey;
        }

        return io(options.field ? options.field.key : undefined);
    };
});
