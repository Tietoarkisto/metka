define(function (require) {
    return function (header) {
        var $header = $('<div class="page-header clearfix">');
        if (typeof header === 'function') {
            header($header);
        } else {
            $header.append(header);
        }
        return $header;
    };
});
