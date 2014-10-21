define(function (require) {
    'use strict';

    return require('./../../organizationAddon')({
        "title": "Yksikkö ↳ Osastot",
        "options": {
            "disable_properties": true,
            "disable_collapse": true
        },
        "$ref": "#/definitions/agency",
        "definitions": require('./../../definitions')
    }, function (organizations, agency, organizationId) {
        var organization = organizations.data.find(function (organization) {
            return organization.id === organizationId;
        });
        if (!organization) {
            throw 'organization not found';
        }

        organization.agencies  = organization.agencies || [];

        // get current highest id + 1
        var id = organization.agencies.reduce(function (highest, item) {
            return Math.max(highest, parseInt(item.id.split('.')[1], 10));
        } ,0) + 1;

        agency.id = organizationId + '.' + id;

        agency.sections.forEach(function (section, i) {
            section.id = agency.id + '.' + (i + 1);
        });

        organization.agencies.push(agency);
    }, 'organisation');
});
