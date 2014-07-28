define([
    '../button'
], function (button) {
    if (!MetkaJS.JSConfig) {
        return;
    }

    var options = MetkaJS.jsGUIConfig || {};

    if (MetkaJS.SingleObject.state !== 'DRAFT') {
        options.readOnly = true;
    }

    var page = MetkaJS.Globals.page.toUpperCase();

    options.dataConf = MetkaJS.JSConfig[page];

    options.header = function header($header) {
        var supplant = {
            page: page
        };

        var header = {
            localized: 'type.{page}.title',
            pattern: '{localized} - {id} - {revision}{state}',
            buttons: $('<div class="pull-right normalText">')
                .append((function () {
                    var buttonCreateFunctions = MetkaJS.SingleObject.state === 'DRAFT' ? [] : [function () {
                        $(this)
                            .addClass('btn-xs')
                            .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                            .click(function () {
                                require('./assignUrl')('prev');
                            });
                    }, function () {
                        $(this)
                            .addClass('btn-xs')
                            .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                            .click(function () {
                                require('./assignUrl')('next');
                            });
                    }];
                    buttonCreateFunctions.push(function () {
                        $(this)
                            .addClass('btn-xs')
                            .text(MetkaJS.L10N.get('general.buttons.download'))
                            .click(function () {
                                require('./assignUrl')('download');
                            });
                    });
                    return buttonCreateFunctions;
                })().map(function (create) {
                    return button({
                        create: create,
                        style: 'default'
                    });
                }))
        };
        var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
        supplant.id = labelAndValue({
            label: MetkaJS.L10N.get('general.id'),
            value: MetkaJS.SingleObject.id
        });
        supplant.revision = labelAndValue({
            label: MetkaJS.L10N.get('general.revision'),
            value: MetkaJS.SingleObject.revision
        });
        supplant.state = MetkaJS.SingleObject.state === 'DRAFT' ? ' - ' + MetkaJS.L10N.get('general.DRAFT') : '';

        supplant.localized = MetkaJS.L10N.get(header.localized.supplant(supplant));
        $header.html(header.pattern.supplant(supplant));

        if (header.buttons) {
            $header.append(header.buttons);
        }

        return $header;
    };

    return options;
});
