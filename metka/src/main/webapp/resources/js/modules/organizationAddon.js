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

    return function (schema, onSave, dependencyKey) {
        return function (options) {
            delete options.field.displayType;
            return {
                postCreate: function (options) {
                    var dependencyValue;
                    if (dependencyKey) {
                        options.$events.on('data-changed-{key}-{lang}'.supplant({
                            key: dependencyKey,
                            lang: options.defaultLang
                        }), function (e, value) {
                            $button.prop('disabled', !value);
                            dependencyValue = value;
                        });
                    }
                    var $button = require('./button')()({
                        style: 'default',
                        title: MetkaJS.L10N.get('general.buttons.add'),
                        create: function () {
                            this
                                .html('<i class="glyphicon glyphicon-plus"></i> Lisää')
                                .click(function () {
                                    var $editor = $('<div>').jsoneditor({
                                        schema: schema
                                    }).on('change', function() {
                                        $(this).find('.btn').addClass('btn-sm');
                                    });
                                    require('./modal')($.extend(true, require('./optionsBase')(), {
                                        title: MetkaJS.L10N.get('general.buttons.add'),
                                        large: true,
                                        body: $editor,
                                        buttons: [{
                                            title: MetkaJS.L10N.get('general.buttons.add'),
                                            create: function () {
                                                this
                                                    .click(function () {
                                                        require('./server')('/study/getOrganizations', {
                                                            type: 'GET',
                                                            success: function (response) {
                                                                var organizations = JSON.parse(response);
                                                                if (organizations && Array.isArray(organizations.data)) {
                                                                    onSave(organizations, $editor.data('jsoneditor').getValue(), dependencyValue);

                                                                    require('./server')('/study/uploadOrganizations', {
                                                                        data: JSON.stringify({
                                                                            type: 'MISC',
                                                                            json: JSON.stringify(organizations)
                                                                        }),
                                                                        success: function (response) {
                                                                            require('./resultViewer')(response, null, function() {
                                                                                options.$events.trigger('refresh.metka');
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    });
                                            }
                                        }, {
                                            type: 'CANCEL'
                                        }]
                                    }));
                                });
                        }
                    });

                    this.children().children('.form-control')
                        .wrap('<div class="input-group">')
                        .parent()
                        .append($('<span class="input-group-btn">')
                            .append($button));
                }
            };
        };
    };
});