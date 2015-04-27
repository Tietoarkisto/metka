define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            onClick: function (transferRow) {
                /*var $row = $(this);*/
                require('./../../server')('viewAjax', {
                    PAGE: 'STUDY_ATTACHMENT',
                    no: transferRow.fields.no.values.DEFAULT.current,
                    id: options.data.key.id
                }, {
                    method: 'GET',
                    success: function (response) {
                        if (response.result === 'VIEW_SUCCESSFUL') {
                            $.extend(options.data, response.transferData);
                            options.type = options.isReadOnly(options) ? 'VIEW' : 'MODIFY';
                            options.$events.trigger('refresh.metka');
                        }
                    }
                })
            },
            create: function () {
                var $containerField = $(this).children();
                require('./../../server')('/study/attachmentHistory/', {
                    data: JSON.stringify(options.data),
                    success: function (data) {
                        //var objectToTransferRow = require('./../../map/object/transferRow');
                        var revisions = data.rows.map(function (result) {
                            return {
                                key: 'custom_fileHistory',
                                value: result.id+"-"+result.no
                            };
                        });

                        revisions && revisions.forEach(function (row) {
                            $containerField.data('addRow')(row);
                        });
                    }
                });
            }
        }
    };
});