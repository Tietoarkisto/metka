define(function (require) {
    'use strict';

    // Polyfills and language extensions

    // shorthand for console.log
    window.log = console.log.bind(console);

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
