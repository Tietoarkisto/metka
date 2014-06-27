/*
 * jQuery widgets in this project:
 *
 * - metka.metka is base widget with all of the common functionality.
 *
 * Create new widget type, if:
 * - Element is usually other than basic <div>
 *
 * Extend metka.metka with method, if:
 * -
 */

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
            // TODO: metka widget should be a collection of methods and do _nothing_ by default.
            // TODO: move all functionality away from _create

            if (this.options.parent) {
                this.inheritParentOptions(this.options.parent);
            }
            //console.log(this.options.type, this.options.content ? this.options.content.length : '-', this.options);
        },
        togglable: function (children) {
            (children ? this.element.children() : this.element)
                .toggleClass('containerHidden', !!this.options.hidden);
        },
        childrenApply: function (method/*[, options]*/) {
            var args = $.makeArray(arguments);

            // remove first argument (method)
            args.shift();

            return this.children(method)(args);
        },
        children: function (method) {
            return function () {
                return $.metka[method]($.extend.apply($, [{}].concat($.makeArray(arguments), {
                    parent: this.options
                }))).element;
            }.bind(this);
        },
        inheritParentOptions: function (options) {
            // inherit readOnly option
            this.options.readOnly = options.readOnly || this.options.readOnly;
        }
    });
})();
