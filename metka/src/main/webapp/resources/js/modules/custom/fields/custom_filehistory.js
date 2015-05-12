define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            field: {
                onClick: function (transferRow) {
                    if(!transferRow.value) {
                        return;
                    }
                    var split = transferRow.value.split("-");
                    if(split.length < 2) {
                        return;
                    }
                    require('./../../server')('viewAjax', {
                        PAGE: 'STUDY_ATTACHMENT',
                        no: split[1],
                        id: split[0]
                    }, {
                        method: 'GET',
                        success: function (response) {
                            if (response.result === 'VIEW_SUCCESSFUL') {
                                $.extend(options.data, response.data);
                                $.extend(require('./../../root')(options).content, response.gui.content);
                                options.type = options.isReadOnly(options) ? 'VIEW' : 'MODIFY';
                                options.$events.trigger('refresh.metka');
                            }
                        }
                    })
                }
            },
            postCreate: function (options) {
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