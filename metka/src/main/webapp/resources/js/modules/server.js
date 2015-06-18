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

    var CACHE_SIZE = 25;

    var callCache = [];
    var callQueue = [];

    function initCallCache() {
        for(var i=0; i<CACHE_SIZE; i++) {
            callCache[i] = null;
        }
    }

    function checkQueue() {
        for(var i = 0; i < CACHE_SIZE; i++) {
            if(callCache[i] == null) {
                var call = callQueue.shift();
                if(!!call) {
                    cacheCall(i, call);
                    callCache[i]();
                }
                return;
            }
        }
    }

    function cacheCall(index, call) {
        var oldSuccess = call.success;
        call.success = function(data) {
            if(oldSuccess) {
                oldSuccess(data);
            }
            callCache[index] = null;
            checkQueue();
        };
        callCache[index] = function() {
            $.ajax(call);
        }
    }

    initCallCache();

    /**
     * Combines `url` module and jQuery AJAX with default options.
     *
     * @param {string} url Valid string for module `url`.
     * @param {object} (optional) urlOpts Valid object for module `url`.
     * @param {object} options jQuery AJAX options, if other then default values in this function.
     */
    return function (url/*[, urlOpts]*/, options) {
        switch (arguments.length) {
            case 3:
                url = require('./url')(url, options);
                options = arguments[2];
                break;
            case 2:
                url = require('./url')(url);
                break;
            default:
                throw 'illegal number of arguments';
        }

        callQueue.push($.extend({
            type: 'POST',
            cache: false,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: url,
            jsonp: false
        }, options));
        checkQueue();
        /*$.ajax($.extend({
            type: 'POST',
            cache: false,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: url,
            jsonp: false
        }, options));*/
    };
});