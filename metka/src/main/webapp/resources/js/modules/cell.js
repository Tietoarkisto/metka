define(function (require) {
    'use strict';

    return require('./inherit')(function (options) {
        var getPropertyNS = require('./utils/getPropertyNS');

        var $div = $('<div>')
                    // In conf, columns is set for section. Parent of cell is row and grand-parent is section.
                    .addClass('col-xs-' + (12 * (options.colspan || 1) / (options.parent.parent.columns || 1)));

        if(options.type === 'CELL') {
            if(options.contentType === "BUTTON") {
                $div.append(require('./button')(options)(options.button))
            } else {
                require('./field').call($div, $.extend(options, {
                    fieldOptions: getPropertyNS(options, 'dataConf.fields', options.field.key) || {}
                }));
            }
        }

        return require('./togglable')
                .call($div, options, true);
    });
});