/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function (require) {
    'use strict';

    var getPropertyNS = require('./utils/getPropertyNS');
    var resultParser = require('./resultParser');

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
                callback(resultParser(response.result).getResult() === 'VIEW_SUCCESSFUL' && response.data.state.uiState === 'DRAFT' && MetkaJS.User.userName === response.data.state.handler);
            },
            error: function () {
                callback(false);
            }
        });
    };
});
