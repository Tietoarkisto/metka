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
     * @param {object} o Get property from this object
     * @param [ns] {string/integer/array} Namespace. Can be string, '.' (dot) separated string, or array of strings
     * @return {any} Value if path exists, or undefined.
     *
     * Example:
     * var o = {a:{b:{c:{d:{e:{f:123}}}}}};
     * MetkaJS.objectGetPropertyNS(o, 'a.b.c', 'd', ['e', 'f']); // 123
     * MetkaJS.objectGetPropertyNS(o, 'a.B.c', 'd', ['e', 'f']); // undefined
     */
    return function (o/*[, ns]*/) {
        var ns = $.makeArray(arguments);
        ns.shift(); // remove o
        if (!ns.length) {
            return o;
        }
        ns = Array.prototype.concat.apply([], ns.map(function (v) {
            return typeof v === 'string' ? v.split('.') : v;
        })).map(function (prop) {
            var numProp = parseInt(prop);
            return isNaN(numProp) ? prop : numProp;
        });
        return (function r(o) {
            if (typeof o !== 'object') {
                return;
            }
            if (o === null) {
                return;
            }

            var propName = ns.shift();
            var prop = o[propName];
            if (ns.length) {
                return r(prop);
            } else {
                return prop;
            }
        })(o);
    };
});
