define(function (require) {
    'use strict';

    return {
        create: require('./inherit')(function (options) {
            var $column = $('<div>');
            require('./togglable').call($column, options);

            if (MetkaJS.L10N.hasTranslation(options, 'title')) {
                $column.append($('<h4>')
                    .text(MetkaJS.L10N.localize(options, 'title')));
            }
            setTimeout(function() {
                $column.append(options.rows.map(require('./row')(options)));
            }, 0)
            return $column;
        }),
        add: function ($columns) {
            this.append($columns);
        }
    };
});
