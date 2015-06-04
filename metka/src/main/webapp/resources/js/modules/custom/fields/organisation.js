define(function (require) {
    'use strict';

    return function(options) {
        function initField($elem, key) {
            var curVal = require('./../../data')(options)(key).getByLang("DEFAULT");
            options.required = !!curVal && curVal === '2';

            options.$events.on('data-changed-{key}-{lang}'.supplant({
                key: key,
                lang: "DEFAULT"
            }), function(e, value) {
                options.required = !!value && value === '2';
                options.$events.trigger('label-update-{key}-{lang}'.supplant({
                    key: 'organisation',
                    lang: 'DEFAULT'
                }))
            });
        }

        return $.extend(true
        , {}
        , require('./../../organizationAddon')({
            "title": "Organisaatio ↳ Yksiköt ↳ Osastot",
            "options": {
                "disable_properties": true,
                "disable_collapse": true
            },
            "$ref": "#/definitions/organization",
            "definitions": require('./../../definitions')
        }, function (organizations, organization) {
            // get current highest id + 1
            var id = organizations.data.reduce(function (highest, organization) {
                    return Math.max(highest, parseInt(organization.id, 10));
                } ,0) + 1;

            // to string
            id = id + '';

            organization.id = id;

            organization.agencies.forEach(function (agency, i) {
                agency.id = id + '.' + (i + 1);
                agency.sections.forEach(function (section, i) {
                    section.id = agency.id + '.' + (i + 1);
                });
            });

            organizations.data.push(organization);
        })(options)
        , {
            preCreate: function(options) {
                var $elem = this;
                // This is a bad way to do this but it works because custom fields are always run after other fields.
                if(options.data.fields.authortype) {
                    initField($elem, "authortype");
                } else if(options.data.fields.otherauthortype) {
                    initField($elem, "otherauthortype");
                }
            }
        });
    }
});
