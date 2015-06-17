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