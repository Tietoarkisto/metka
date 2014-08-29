define(function (require) {
    return function (options, onLoad) {
        var metka = require('./../../metka');
        require('./../server')('/revision/ajax/view/{page}/{id}/{no}', {
            method: 'GET',
            success: function (data) {
                $.extend(options, data.gui);
                options.readOnly = !data.transferData.state.draft;

                options.dataConf = data.configuration;
                options.data = data.transferData;

                options.header = function header($header) {
                    var supplant = {
                        page: metka.PAGE
                    };

                    var header = {
                        localized: 'type.{page}.title',
                        pattern: '{localized} - {id} - {no}{state}',
                        buttons: $('<div class="pull-right btn-toolbar">')
                            .append(require('./../languageRadioInputGroup')(options, 'translation-lang', options.defaultLang))
                            .append($('<div class="btn-group btn-group-xs">')
                                .append([{
                                    icon: 'glyphicon-chevron-left',
                                    action: 'prev'
                                }, {
                                    icon: 'glyphicon-chevron-right',
                                    action: 'next'
                                }].map(function (o) {
                                        return $('<button type="button" class="btn btn-default">')
                                            .prop('disabled', true)
                                            .append($('<span class="glyphicon">')
                                                .addClass(o.icon))
                                            .click(function () {
                                                require('./../assignUrl')(o.action);
                                            });
                                    })))
                            .append($('<div class="btn-group btn-group-xs">')
                                .append($('<button type="button" class="btn btn-default">')
                                    //.prop('disabled', true)
                                    .text(MetkaJS.L10N.get('general.buttons.download'))
                                    .click(function () {
                                        require('./../assignUrl')('download');
                                    })))

                    };
                    var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
                    supplant.id = labelAndValue({
                        label: MetkaJS.L10N.get('general.id'),
                        value: metka.id
                    });
                    supplant.no = labelAndValue({
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

                onLoad();
            }
        });
    };
});
