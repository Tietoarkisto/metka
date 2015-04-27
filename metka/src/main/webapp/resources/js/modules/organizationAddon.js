define(function (require) {
    'use strict';

    return function (schema, onSave, dependencyKey) {
        return function (options) {
            delete options.field.displayType;
            return {
                create: function (options) {
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
                                        title: 'Lisää',
                                        large: true,
                                        body: $editor,
                                        buttons: [{
                                            create: function () {
                                                this
                                                    .text(MetkaJS.L10N.get('general.buttons.add'))
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