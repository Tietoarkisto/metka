/**************************************************************************************
 * Copyright (c) 2013-2015, Finnish Social Science Data Archive/University of Tampere *
 *                                                                                    *
 * All rights reserved.                                                               *
 *                                                                                    *
 * Redistribution and use in source and binary forms, with or without modification,   *
 * are permitted provided that the following conditions are met:                      *
 * 1. Redistributions of source code must retain the above copyright notice, this     *
 *    list of conditions and the following disclaimer.                                *
 * 2. Redistributions in binary form must reproduce the above copyright notice,       *
 *    this list of conditions and the following disclaimer in the documentation       *
 *    and/or other materials provided with the distribution.                          *
 * 3. Neither the name of the copyright holder nor the names of its contributors      *
 *    may be used to endorse or promote products derived from this software           *
 *    without specific prior written permission.                                      *
 *                                                                                    *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND    *
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED      *
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE             *
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR   *
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES     *
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;       *
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON     *
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT            *
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS      *
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.                       *
 **************************************************************************************/

define(function (require) {
    'use strict';

    var resultParser = require('./../resultParser');

    /**
     * Creates page configuration based on requested JSON configurations and data from server.
     *
     * @this {undefined} No this value.
     * @param {object} options Object to set additional page related properties, like header.
     * @param {function} onLoad Callback gets called when options are set.
     * @return {undefined} No return value.
     */
    return function (options, onLoad) {
        var metka = require('./../../metka');
        require('./../server')('viewAjax', {
            method: 'GET',
            success: function (data) {
                if (!data || !data.data) {
                    require('./../assignUrl')('searchPage');
                }
                $.extend(options, data.gui);
                options.dataConf = data.configuration;
                options.data = data.data;
                history.replaceState(undefined, '', require('./../url')('view', options.data.key));

                options.header = function ($header) {
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
                            .append(require('./../languageRadioInputGroup')(options, 'translation-lang', MetkaJS.User.role.defaultLanguage.toUpperCase()));
                    }

                    var header = {
                        localized: 'type.{page}.title',
                        pattern: '{localized} - {id}{name} - {no}{state}{handler}',
                        buttons: $buttons
                            .append($('<div class="btn-group btn-group-xs">')
                                .append([{
                                    icon: 'glyphicon-chevron-left',
                                    action: 'PREVIOUS'
                                }, {
                                    icon: 'glyphicon-chevron-right',
                                    action: 'NEXT'
                                }].map(function (o) {
                                        return $('<button type="button" class="btn btn-default">')
                                            //.prop('disabled', true)
                                            .append($('<span class="glyphicon">')
                                                .addClass(o.icon))
                                            .click(function () {
                                                var $button = $(this);
                                                require('./../server')("adjacent", {
                                                    data: JSON.stringify({
                                                        current: options.data,
                                                        ignoreRemoved: true,
                                                        direction: o.action
                                                    }),
                                                    success: function (response) {
                                                        if(resultParser(response.result).getResult() === "REVISION_FOUND") {
                                                            $.extend(options.data, response.data);
                                                            options.$events.trigger('refresh.metka');
                                                            history.replaceState(undefined, '', require('./../url')('view'));
                                                            location.reload();
                                                        } else {
                                                            require('./../resultViewer')(response.result);
                                                        }
                                                    }
                                                });
                                            });
                                    })))
                            .append($('<div class="btn-group btn-group-xs">')
                                .append($('<button type="button" class="btn btn-default">')
                                    //.prop('disabled', true)
                                    .text(MetkaJS.L10N.get('general.buttons.download'))
                                    .click(function () {
                                        require('./../server')("download", {
                                            data: JSON.stringify(options.data.key),
                                            success: function (response) {
                                                if(resultParser(response.result).getResult() === "REVISION_FOUND") {
                                                    saveAs(new Blob([response.content], {type: "text/json;charset=utf-8"}), "id_"+response.key.id+"_revision_"+response.key.no+".json");
                                                } else {
                                                    require('./../resultViewer')(response.result);
                                                }
                                            }
                                        });
                                    })))
                    };
                    var labelAndValue = String.prototype.supplant.bind('{label}&nbsp;{value}');
                    supplant.id = labelAndValue({
                        label: MetkaJS.L10N.get('general.id'),
                        value: function() {
                            if(options.dataConf.displayId) {
                                return require('../data').latestValue(options.data.fields[options.dataConf.displayId], 'DEFAULT')
                            }
                            return options.data.key.id;
                        }()
                    });
                    supplant.no = labelAndValue({
                        label: MetkaJS.L10N.get('general.revision'),
                        value: options.data.key.no
                    });

                    var title = options.data.fields['title'] !== undefined ?
                        (options.data.fields['title'].values['DEFAULT'].current || options.data.fields['title'].values['DEFAULT'].original) : options.data.fields['seriesname'] !== undefined ?
                            (options.data.fields['seriesname'].values['DEFAULT'].current || options.data.fields['seriesname'].values['DEFAULT'].original) : "";

                    supplant.name = title ? ' - ' + title : '';

                    supplant.state = data.data.state.uiState ? ' - ' + MetkaJS.L10N.get('state.' + data.data.state.uiState) : '';
                    supplant.handler = data.data.state.handler ? ' - ' + MetkaJS.L10N.get('general.handler') + " " + data.data.state.handler : (data.data.state.uiState === 'DRAFT' ? ' - ' + MetkaJS.L10N.get('general.noHandler') : '');
                    supplant.localized = MetkaJS.L10N.get(header.localized.supplant(supplant));
                    $header.html("<div class='page-header-text'>" + header.pattern.supplant(supplant) + "</div>");

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
