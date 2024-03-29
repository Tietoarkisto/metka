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
            "title": MetkaJS.L10N.get("other.agencysubtitle"),
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
