define(function (require) {
    'use strict';

    return function (options, onLoad) {
        var metka = require('./../../metka');
        require('./../server')('viewAjax', {
            method: 'GET',
            success: function (data) {
                if (!data || !data.transferData) {
                    require('./../assignUrl')('searchPage');
                }
                metka.revision = metka.no = data.transferData.key.no;
                history.replaceState(undefined, '', require('./../url')('view'));
                $.extend(options, data.gui);
                options.readOnly = !data.transferData.state.uiState === 'DRAFT' || !(data.transferData.state.handler === MetkaJS.User.userName);
                options.dataConf = data.configuration;
                options.data = data.transferData;

                options.header = function header($header) {
                    var supplant = {
                        page: metka.PAGE
                    };

                    var $buttons = $('<div class="pull-right btn-toolbar">');

                    // visit content fields, until some has translatable: true
                    if ((function r(content) {
                        return content.some(function (item) {
                            var containerContent = {
                                TAB: item.content,
                                SECTION: item.content,
                                COLUMN: item.rows,
                                ROW: item.cells
                            }[item.type];
                            if (containerContent) {
                                return r(containerContent);
                            } else {
                                if (item.field) {
                                    return require('./../utils/getPropertyNS')(options, 'dataConf.fields', item.field.key, 'translatable');
                                } else {
                                    return false;
                                }
                            }
                        });
                    })(options.content)) {
                        $buttons
                            .append(require('./../languageRadioInputGroup')(options, 'translation-lang', options.defaultLang));
                    }

                    var header = {
                        localized: 'type.{page}.title',
                        pattern: '{localized} - {id} - {no}{state}',
                        buttons: $buttons
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

                    supplant.state = data.transferData.state.uiState ? ' - ' + MetkaJS.L10N.get('state.' + data.transferData.state.uiState) : '';

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
