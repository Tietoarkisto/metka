define(function (require) {
    if (!MetkaJS.JSConfig) {
        return;
    }

    var options = MetkaJS.jsGUIConfig || {};

    var metka = require('./../../metka');

    if (metka.state !== 'DRAFT') {
        options.readOnly = true;
    }

    options.dataConf = MetkaJS.JSConfig[metka.PAGE];
    options.data = MetkaJS.data;

    options.header = function header($header) {
        var supplant = {
            page: metka.PAGE
        };

        var header = {
            localized: 'type.{page}.title',
            pattern: '{localized} - {id} - {revision}{state}',
            buttons: $('<div class="pull-right normalText">')
                .append((function () {
                    var buttonCreateFunctions = metka.state === 'DRAFT' ? [] : [function () {
                        $(this)
                            .addClass('btn-xs')
                            .html('<span class="glyphicon glyphicon-chevron-left"></span>')
                            .click(function () {
                                require('./../assignUrl')('prev');
                            });
                    }, function () {
                        $(this)
                            .addClass('btn-xs')
                            .html('<span class="glyphicon glyphicon-chevron-right"></span>')
                            .click(function () {
                                require('./../assignUrl')('next');
                            });
                    }];
                    buttonCreateFunctions.push(function () {
                        $(this)
                            .addClass('btn-xs')
                            .text(MetkaJS.L10N.get('general.buttons.download'))
                            .click(function () {
                                require('./../assignUrl')('download');
                            });
                    });
                    return buttonCreateFunctions;
                })().map(function (create) {
                    return require('./../button')()({
                        create: create,
                        style: 'default'
                    });
                }))
        };
        var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
        supplant.id = labelAndValue({
            label: MetkaJS.L10N.get('general.id'),
            value: metka.id
        });
        supplant.revision = labelAndValue({
            label: MetkaJS.L10N.get('general.revision'),
            value: metka.revision
        });
        supplant.state = metka.state === 'DRAFT' ? ' - ' + MetkaJS.L10N.get('general.DRAFT') : '';

        supplant.localized = MetkaJS.L10N.get(header.localized.supplant(supplant));
        $header.html(header.pattern.supplant(supplant));

        if (header.buttons) {
            $header.append(header.buttons);
        }

        return $header;
    };

    return options;
});
