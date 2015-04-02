define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;

        return {
            prepareModal: function(modalOptions) {
                var oldIsReadOnly = modalOptions.isReadOnly;
                modalOptions.isReadOnly = function(options) {
                    if(!modalOptions.data.unapproved && !require('./../../hasEveryPermission')(['canRemoveStudyVersions'])) {
                        return true;
                    }

                    return oldIsReadOnly(options);
                }
            }
        }
    };
});
