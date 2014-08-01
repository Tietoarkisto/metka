/**
 * @param o Set property to this object
 * @param [ns] Can be string, '.' (dot) separated string, or array of strings
 * @param value any value
 * @returns value
 *
 * Example:
 * var o = {};
 * MetkaJS.objectSetPropertyNS(o, 'a.b.c', 'd', ['e', 'f'], 123);
 * JSON.stringify(o); // "{"a":{"b":{"c":{"d":{"e":{"f":123}}}}}}"
 */

define(function (require) {
    'use strict';

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
        }));
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
