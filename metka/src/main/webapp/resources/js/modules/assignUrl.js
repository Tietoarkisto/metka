// same as ´url´ module, except also navigates to the url

define(function (require) {
    'use strict';

    return function () {
        location.assign(require('./url').apply(this, arguments));
    };
});