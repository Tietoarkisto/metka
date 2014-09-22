/**
 * @param o Get property from this object
 * @param [ns] Namespace. Can be string, '.' (dot) separated string, or array of strings
 */

define(function (require) {
    'use strict';

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
