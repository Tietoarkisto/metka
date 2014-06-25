(function () {
    'use strict';

    $.widget('metka.metka', {
        options: {
            parent: null,
            readOnly: false,
            hidden: false,
            content: []
        },
        _create: function () {
            // inherit readOnly option
            if (this.options.parent) {
                this.options.readOnly = this.options.parent.readOnly || this.options.readOnly;
            }
            //console.log(this.options.type, this.options.content ? this.options.content.length : '-', this.options);
        },
        togglable: function (children) {
            (function () {
                if (children) {
                    return this.element.children();
                } else {
                    return this.element;
                }
            }).call(this).toggleClass('containerHidden', !!this.options.hidden);
        },
        extend: function () {
            return $.extend.apply($, [{}].concat($.makeArray(arguments), {
                parent: this.options
            }));
        },
        child: function (method) {
            var args = $.makeArray(arguments);
            args.shift();
            return $.metka[method](this.extend.apply(this, args)).element;
        },
        children: function (method) {
            return function () {
                return $.metka[method](this.extend.apply(this, arguments)).element;
            }.bind(this);
        }
    });
})();
