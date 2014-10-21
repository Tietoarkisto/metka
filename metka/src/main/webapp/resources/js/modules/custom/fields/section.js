define(function (require) {
    'use strict';

    return require('./../../organizationAddon')({
        "options": {
            "disable_properties": true,
            "disable_collapse": true
        },
        "$ref": "#/definitions/section",
        "definitions": require('./../../definitions')
    }, function (organizations, section, agencyId) {
        var agency;
        organizations.data.some(function (organization) {
            agency = organization.agencies.find(function (agency) {
                return agency.id === agencyId;
            });
            // to break out Array.prototype.some loop, return agency or undefined
            return agency;
        });
        if (!agency) {
            throw 'agency not found';
        }

        agency.sections  = agency.sections || [];

        // get current highest id + 1
        var id = agency.sections.reduce(function (highest, item) {
            return Math.max(highest, parseInt(item.id.split('.')[2], 10));
        } ,0) + 1;

        section.id = agencyId + '.' + id;

        agency.sections.push(section);
    }, 'agency');
});
