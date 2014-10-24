define(function (require) {
    'use strict';

    return function (option) {
        if (!option) {
            return;
        }

        if(typeof option.title === "string" || MetkaJS.L10N.hasTranslation(option, 'title')) {
            return MetkaJS.L10N.localize(option, 'title');
        }

        if (option.title) {
            if (option.title.type === 'LITERAL') {
                return option.title.value;
            }
        }

        return option.value;
    };
});
