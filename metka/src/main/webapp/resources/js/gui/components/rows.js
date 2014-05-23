(function() {
    'use strict';
    GUI.Components.rows = function ($container, content, readOnly, column, context, buildContainers) {
        content.forEach(function (container) {
            //container.title
            //container.readOnly
            var $row = $('<div class="row">')
                .toggleClass('containerHidden', container.hidden);
            GUI.Components.cells($row, container.cells, readOnly, column, context, buildContainers);

            $container.append($row);
        });
    }
}());