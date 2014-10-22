define(function (require) {
    'use strict';

    return require('./../../organizationAddon')({
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
    });
});
