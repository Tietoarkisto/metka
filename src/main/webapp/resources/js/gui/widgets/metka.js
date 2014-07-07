
define();

/*
 * jQuery widgets in this project:
 *
 * - metka.metka is a base widget with all of the common functionality.
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
        autoId: (function () {
            var i = 0;
            return function () {
                return 'METKA_UI_' + (i++);
            };
        })(),
        togglable: function (children) {
            (children ? this.element.children() : this.element)
                .toggleClass('containerHidden', !!this.options.hidden);
        },
        // get constructor function for metka widget
        children: function (widgetName) {
            return function () {
                //return $.metka[widgetName]($.extend.apply($, arguments)).element[widgetName]('inheritParentOptions', this.options).element;

                return $.metka[widgetName]($.extend.apply($, [{}].concat($.makeArray(arguments), {
                    parent: this.options
                }))).element;
            }.bind(this);
        },
        childrenApply: function (widgetName/*[, options]*/) {
            var args = $.makeArray(arguments);
            // remove first argument (widgetName)
            args.shift();

            return this.children(widgetName)(args);
        },
        inheritParentOptions: function (options) {
            //this.options.parent = options;
            // inherit readOnly option
            this.options.readOnly = options.readOnly || this.options.readOnly;
        }
    });
})();

define("gui/widgets/metka", function(){});
