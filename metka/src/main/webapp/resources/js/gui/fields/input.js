(function() {
    'use strict';
    GUI.Fields.input = function ($elem, type, cell, disabled) {
        var id = GUI.id();
        $elem.append($('<label>', {
            'for': id
        })
            .text(MetkaJS.L10N.localize(cell, 'title'))
            .toggleClass('containerHidden', cell.hidden)
            .if(cell.required, function () {
                this.append('<span class="glyphicon glyphicon-asterisk"></span>');
            }));

        var options = {
            'class': 'form-control',
            id: id
        };
        var nodeType = (function () {
            if (type === 'SELECTION') {
                return 'select';
            }

            if (cell.field.multiline) {
                return 'textarea';
            }

            options.type = 'text';
            return 'input';
        })();

        var $input = $('<' + nodeType + '>', options)
            .toggleClass('containerHidden', cell.hidden)
            .toggleClass('alert-warning', cell.important);

        if (['DATE', 'TIME', 'DATETIME'].indexOf(type) !== -1) {
            GUI.Fields.input.datetime($elem, type, $input, disabled);
        } else {
            $elem.append($input.prop('disabled', disabled));
        }
    }
}());