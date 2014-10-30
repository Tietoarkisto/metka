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
