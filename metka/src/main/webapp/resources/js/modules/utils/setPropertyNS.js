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

    /**
     * @param {object} o Set property to this object
     * @param [ns] {string/integer/array} Namespace. Can be string, '.' (dot) separated string, or array of strings
     * @param {any} value Any value.
     * @returns value
     *
     * Example:
     * var o = {};
     * MetkaJS.objectSetPropertyNS(o, 'a.b.c', 'd', ['e', 'f'], 123);
     * JSON.stringify(o); // "{"a":{"b":{"c":{"d":{"e":{"f":123}}}}}}"
     */
    return function (o, ns/*[, ns]*/,  value) {
        var ns = $.makeArray(arguments);
        ns.shift(); // remove o
        value = ns.pop(); // value is last argument
        if (!ns.length) {
            throw 'Property name was not specified.';
        }
        if (!o) {
            throw 'Object was not specified.';
        }

        ns = Array.prototype.concat.apply([], ns.map(function (v) {
            return typeof v === 'string' ? v.split('.') : v;
        })).map(function (prop) {
            var numProp = parseInt(prop);
            return isNaN(numProp) ? prop : numProp;
        });
        return (function r(o) {
            var propName = ns.shift();
            if (ns.length) {
                var prop;
                if (typeof o[propName] === 'undefined') {
                    prop = o[propName] = {};
                } else {
                    if (typeof o[propName] !== 'object') {
                        throw 'Typeof property is not object.';
                    }
                    if (o[propName] === null) {
                        prop = o[propName] = {};
                        //throw 'Property is null.';
                    }
                    prop = o[propName];
                }
                return r(prop);
            } else {
                return o[propName] = value;
            }
        })(o);
    };
});
