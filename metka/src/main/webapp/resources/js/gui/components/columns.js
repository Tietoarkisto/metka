(function() {
    'use strict';
    GUI.Components.columns = function ($container, content, readOnly, context, buildContainers) {
        content.forEach(function (container) {
            GUI.Components.rows($container, container.rows, readOnly, container, context, buildContainers);
        });
    }
}());