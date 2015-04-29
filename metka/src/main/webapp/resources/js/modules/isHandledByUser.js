define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');

    // Returns true or false depending on if referenced revisionable is in draft state and handled by current user
    // If the reference is not valid of the information can't be fetched then false is returned
    return function (options, key, callback) {
        var dataConf = require('./root')(options).dataConf;
        if(!dataConf) {
            return false;
        }

        var field = getPropertyNS(dataConf, 'fields', key);
        if(!field) {
            return false;
        }

        var references = getPropertyNS(dataConf, 'references');
        if(!references) {
            return false;
        }

        var reference = null;
        if(field.type === 'REFERENCE') {
            reference = getPropertyNS(dataConf, 'references', field.reference);
        } else if(field.type === 'SELECTION') {
            var selectionList = require('./selectionList')(options, key);
            if(!selectionList || selectionList.type !== 'REFERENCE') {
                return false;
            }
            reference = getPropertyNS(dataConf, 'references', selectionList.reference);
        }
        if(!reference || reference.type !== 'REVISIONABLE') {
            return false;
        }

        require('./server')('viewAjax', {
            PAGE: reference.target,
            id: require('./data')(options)(key).getByLang(options.defaultLang),
            no: ''
        }, {
            method: 'GET',
            success: function (response) {
                callback(response.result === 'VIEW_SUCCESSFUL' && response.data.state.uiState === 'DRAFT' && MetkaJS.User.userName === response.data.state.handler);
            },
            error: function () {
                callback(false);
            }
        });
    };
});
