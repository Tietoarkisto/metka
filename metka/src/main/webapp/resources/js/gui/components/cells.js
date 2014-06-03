(function() {
    'use strict';
    GUI.Components.cells = function ($container, content, readOnly, container, context, buildContainers) {
        $container.eachTo(content, function (i, cell) {
            this
                .append($('<div>', {
                    'class': GUI.Grid.getColumnClass(container.columns, cell.colspan)
                })
                .me(function () {
                    //cell.field.multichoice
                    //cell.field.showReferenceKey
                    //cell.field.showReferenceValue
                    //cell.field.handlerName
                    var type = cell.field.displayType || MetkaJS.JSConfig[context].fields[cell.field.key].type;
                    if (['INTEGER', 'STRING', 'DATE', 'TIME', 'DATETIME', 'CONCAT', 'SELECTION', 'CONTAINER'].indexOf(type) === -1) {
                        console.log('not implemented type', type, arguments);
                        return;
                    }

                    var disabled = readOnly || container.readOnly || cell.readOnly || !MetkaJS.JSConfig[context].fields[cell.field.key].editable;

                    if (type === 'CONTAINER') {
                        GUI.Fields.container(this, cell, disabled, context);
                    } else {
                        GUI.Fields.input(this, type, cell, disabled);
                    }
                }));
        });
    }
}());