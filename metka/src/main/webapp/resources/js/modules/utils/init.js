define(function (require) {
    'use strict';

    /**
     * Like $.each, except 'this' refers to the jQuery object
     */
    $.fn.eachTo = function (c, f) {
        if (!c) {
            return this;
        }
        var that = this;
        $.each(c, function () {
            return f.apply(that, arguments);
        });
        return this;
    };

    /**
     * Calls 'f' in the context of jQuery object.
     * Useful when jQuery object needs to be: instantiated, manipulated using custom logic and then chained/returned
     */
    $.fn.me = function (f) {
        f.call(this);
        return this;
    };

    /**
     * Conditional $.fn.me
     */
    $.fn.if = function (x, f) {
        return x ? this.me(f) : this;
    };


    // Polyfills and language extensions

    // shorthand for console.log
    window.log = console.log.bind(console);

    // http://javascript.crockford.com/remedial.html
    if (!String.prototype.supplant) {
        String.prototype.supplant = function (o) {
            return this.replace(
                /\{([^{}]*)\}/g,
                function (a, b) {
                    var r = o[b];
                    return typeof r === 'string' || typeof r === 'number' ? r : a;
                }
            );
        };
    }

    // 'true'.bool() // true
    if (!String.prototype.bool) {
        String.prototype.bool = function () {
            return (/^true$/i).test(this);
        };
    }

    // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/find
    if (!Array.prototype.find) {
        Object.defineProperty(Array.prototype, 'find', {
            enumerable: false,
            configurable: true,
            writable: true,
            value: function(predicate) {
                if (this == null) {
                    throw new TypeError('Array.prototype.find called on null or undefined');
                }
                if (typeof predicate !== 'function') {
                    throw new TypeError('predicate must be a function');
                }
                var list = Object(this);
                var length = list.length >>> 0;
                var thisArg = arguments[1];
                var value;

                for (var i = 0; i < length; i++) {
                    if (i in list) {
                        value = list[i];
                        if (predicate.call(thisArg, value, i, list)) {
                            return value;
                        }
                    }
                }
                return undefined;
            }
        });
    }
    /*
    if (window.process) {
        window.process = {};
    }
    if (window.process.nextTick) {
        window.process.nextTick = function (f) {
            setTimeout(f, 0);
        };
    }*/

    // other stuff
    $(document).ajaxError(function () {
        require('./../modal')({
            title: MetkaJS.L10N.get('alert.error.title'),
            buttons: [{
                type: 'DISMISS'
            }]
        });
        log('ajax error', arguments);
    });
});
