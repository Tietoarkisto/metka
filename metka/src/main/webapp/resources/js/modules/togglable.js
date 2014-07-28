define(function (require) {
    return function (options, children) {
        (children ? this.children() : this)
            .toggleClass('containerHidden', !!options.hidden);
        return this;
    };
});