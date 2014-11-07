define(function (require) {
    'use strict';

    // TODO: use prototypes for creating options objects (automatically traverse up prototype chain, when necessary etc.)
    return function root(options) {
        return options.parent ? root(options.parent) : options;
    };
});
