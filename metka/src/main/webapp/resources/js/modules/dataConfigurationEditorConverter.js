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

define(function(require) {
    'use strict';

    var data = require('./data');
    var lang = 'DEFAULT';

    function jsonParser() {
        function getNewField(key, type) {
            return {
                key: key,
                type: type,
                values: {
                    DEFAULT: type==='VALUE' ? getNewValue(null) : null
                },
                rows: {
                    DEFAULT: type !== 'VALUE' ? [] : null
                }
            }
        }

        function insertValue(fields, key, value) {
            fields[key] = getNewField(key, 'VALUE');
            fields[key].values.DEFAULT.current = value;
            return fields[key];
        }

        function insertContainer(fields, key) {
            fields[key] = getNewField(key, 'CONTAINER');
            return fields[key];
        }

        function insertReferenceContainer(fields, key) {
            fields[key] = getNewField(key, 'REFERENCECONTAINER');
            return fields[key];
        }

        function appendRow(field) {
            var row = getNewRow(field.key);
            field.rows.DEFAULT.push(row);
            return row;
        }

        function getNewValue(value) {
            return {
                current: value
            }
        }

        function getNewRow(key) {
            return {
                key: key,
                fields: {}
            }
        }

        return {
            setJsonProperty: function(json, fields, configuration) {
                if(!fields || !configuration || !json || (configuration.property && !json[configuration.property])) {
                    return;
                }
                var container = configuration.container ? insertContainer(fields, configuration.container) : null;
                Object.keys(configuration.property ? json[configuration.property] : json).map(function(key) {
                    var row = container ? appendRow(container) : null;

                    var value = configuration.property ? json[configuration.property][key] : json[key];
                    if(row && configuration.propertyKey) {
                        insertValue(row.fields, configuration.propertyKey, key);
                    }
                    if(row && configuration.valueMapping) {
                        if(typeof configuration.valueMapping === "string") {
                            insertValue(row.fields, configuration.valueMapping, value);
                        } else if(typeof configuration.valueMapping === "function") {
                            configuration.valueMapping(value, row.fields);
                        }
                    } else if(!configuration.valueMapping && configuration.mapping) {
                        if(row) {
                            Object.keys(configuration.mapping).map(function(property) {
                                if(typeof configuration.mapping[property] === "string") {
                                    insertValue(row.fields, configuration.mapping[property], value[property]);
                                } else if(typeof configuration.mapping[property] === "function") {
                                    configuration.mapping[property](value, row.fields);
                                }
                            });
                        } else if(configuration.mapping[key]) {
                            if(typeof configuration.mapping[key] === "string") {
                                insertValue(fields, configuration.mapping[key], value);
                            } else if(typeof configuration.mapping[key] === "function") {
                                configuration.mapping[key](json, fields);
                            }
                        }
                    }
                });
            }
        }
    }

    var parser = jsonParser();

    function setSelectionLists(json, fields) {
        var conf = {
            property: "selectionLists",
            container: "selectionLists",
            mapping: {
                key: "selectionLists_key",
                type: "selectionLists_type",
                freeTextKey: "selectionLists_freeTextKey",
                sublistKey: "selectionLists_sublistKey",
                includeEmpty: "selectionLists_includeEmpty",
                freeText: function(json, fields) {
                    var conf = {
                        property: "freeText",
                        container: "selectionLists_freeText_values",
                        valueMapping: "selectionLists_freeText"
                    };

                    setJsonProperty(json, fields, conf);
                },
                options: setOptions
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setOptions(json, fields) {
        var conf = {
            property: "options",
            container: "selectionLists_options",
            valueMapping: function(json, fields) {
                insertValue(fields, "selectionLists_option_value", json.value);
                if(MetkaJS.L10N.hasTranslation(json, 'title')) {
                    insertValue(fields, "selectionLists_option_title_default", json['&title'].default);
                    insertValue(fields, "selectionLists_option_title_en", json['&title'].en);
                    insertValue(fields, "selectionLists_option_title_sv", json['&title'].sv);
                } else {
                    insertValue(fields, "selectionLists_option_title_default", json.title);
                }
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setReferences(json, fields) {
        var conf = {
            property: "references",
            container: "references",
            mapping: {
                key: "reference_key",
                type: "reference_type",
                target: "reference_target",
                valuePath: "reference_valuePath",
                titlePath: "reference_titlePath",
                approveOnly: "reference_approveOnly",
                ignoreRemoved: "reference_ignoreRemoved"
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setFields(json, fields) {
        var conf = {
            property: "fields",
            container: "fields",
            mapping: {
                key: "field_key",
                type: "field_type",
                translatable: "field_translatable",
                immutable: "field_immutable",
                selectionList: "field_selectionList",
                subfield: "field_subfield",
                reference: "field_reference",
                editable: "field_editable",
                writable: "field_writable",
                indexed: "field_indexed",
                generalSearch: "field_generalSearch",
                exact: "field_exact",
                bidirectional: "field_bidirectional",
                indexName: "field_indexName",
                fixedOrder: "field_fixedOrder",
                subfields: function(json, fields) {
                    var conf = {
                        property: "subfields",
                        container: "field_subfields",
                        valueMapping: "field_subfield_key"
                    };

                    setJsonProperty(json, fields, conf);
                },
                removePermissions: function(json, fields) {
                    var conf = {
                        property: "removePermissions",
                        container: "field_removePermissions",
                        valueMapping: "field_removePermissions_permission"
                    };

                    setJsonProperty(json, fields, conf);
                }
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setNamedTargets(json, fields) {
        var conf = {
            property: "namedTargets",
            container: "namedTargets",
            propertyKey: "namedTarget_key",
            mapping: {
                type: "target_type",
                content: "target_content",
                targets: setTargets,
                checks: setChecks
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setRestrictions(json, fields) {
        var conf = {
            property: "restrictions",
            container: "restrictions",
            mapping: {
                type: "operation_type",
                targets: setTargets
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setCascade(json, fields) {
        var conf = {
            property: "cascade",
            container: "cascade",
            mapping: {
                type: "operation_type",
                targets: setTargets
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setTargets(json, fields) {
        var conf = {
            property: "targets",
            container: "targets",
            mapping: {
                type: "target_type",
                content: "target_content",
                targets: setTargets,
                checks: setChecks
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }

    function setChecks(json, fields) {
        var conf = {
            property: "checks",
            container: "checks",
            mapping: {
                condition: function(json, fields) {
                    insertValue(fields, "target_check_condition_type", json.condition.type);
                    if(json.condition.target) {
                        insertValue(fields, "target_check_condition_target_type", json.condition.target.type);
                        insertValue(fields, "target_check_condition_target_content", json.condition.target.content);
                    }
                },
                restrictions: setTargets
            }
        };

        parser.setJsonProperty(json, fields, conf);
    }


    // Configuration construction
    function getValue(fields, key) {
        var field = getField(fields, key);
        if(!field || field.type !== 'VALUE' || !field.values || !field.values.DEFAULT) {
            return;
        }
        return field.values.DEFAULT.current;
    }

    function getField(fields, key) {
        if(!fields || !fields[key]) {
            return null;
        }
        return fields[key];
    }

    function getContainer(fields, key) {
        var field = getField(fields, key);
        if(!field || field.type !== 'CONTAINER') {
            return null;
        }
        return field;
    }

    function getReferenceContainer(fields, key) {
        var field = getField(fields, key);
        if(!field || field.type !== 'REFERENCECONTAINER') {
            return null;
        }
        return field;
    }

    function formLists(selectionLists) {
        var ret = {};
        if(!selectionLists) {
            return ret;
        }
        selectionLists.rows.DEFAULT.map(function(row) {
            var list = {};
            ret[getValue(row.fields, "selectionLists_key")] = list;
            list.key = getValue(row.fields, "selectionLists_key");
            list.type = getValue(row.fields, "selectionLists_type");
            //list.default = getValue(row.fields, "selectionLists_default");
            list.freeTextKey = getValue(row.fields, "selectionLists_freeTextKey");
            list.sublistKey = getValue(row.fields, "selectionLists_sublistKey");
            list.includeEmpty = getValue(row.fields, "selectionLists_includeEmpty");
            list.freeText = formListFreeTextValues(getContainer(row.fields, "selectionLists_freeText_values"));
            list.options = formListOptions(getContainer(row.fields, "selectionLists_options"));
        });

        return ret;
    }

    function formListFreeTextValues(values) {
        var ret = [];
        if(!values) {
            return ret;
        }

        values.rows.DEFAULT.forEach(function(row) {
            var value = getValue(row.fields, "selectionLists_freeText");
            if(value) {
                ret.push(value);
            }
        });

        return ret;
    }

    function formListOptions(options) {
        var ret = [];
        if(!options) {
            return ret;
        }

        options.rows.DEFAULT.forEach(function(row) {
            var option = {};
            option.value = getValue(row.fields, "selectionLists_option_value");
            var title = {};
            title.default = getValue(row.fields, "selectionLists_option_title_default");
            title.en = getValue(row.fields, "selectionLists_option_title_en");
            title.sv = getValue(row.fields, "selectionLists_option_title_sv");
            option['&title'] = title;
            ret.push(option);
        });
        return ret;
    }

    function formReferences(references) {
        var ret = {};
        if(!references) {
            return ret;
        }

        references.rows.DEFAULT.map(function(row) {
            var reference = {};
            ret[getValue(row.fields, "reference_key")] = reference;
            reference.key = getValue(row.fields, "reference_key");
            reference.type = getValue(row.fields, "reference_type");
            reference.target = getValue(row.fields, "reference_target");
            reference.valuePath = getValue(row.fields, "reference_valuePath");
            reference.titlePath = getValue(row.fields, "reference_titlePath");
            reference.approvedOnly = getValue(row.fields, "reference_approveOnly");
            reference.ignoreRemoved = getValue(row.fields, "reference_ignoreRemoved");
        });

        return ret;
    }

    function formFields(fields) {
        var ret = {};
        if(!fields) {
            return ret;
        }

        fields.rows.DEFAULT.map(function(row) {
            var field = {};
            ret[getValue(row.fields, "field_key")] = field;
            field.key = getValue(row.fields, "field_key");
            field.type = getValue(row.fields, "field_type");
            field.translatable = getValue(row.fields, "field_translatable");
            field.immutable = getValue(row.fields, "field_immutable");
            field.selectionList = getValue(row.fields, "field_selectionList");
            field.subfield = getValue(row.fields, "field_subfield");
            field.reference = getValue(row.fields, "field_reference");
            field.editable = getValue(row.fields, "field_editable");
            field.writable = getValue(row.fields, "field_writable");
            field.indexed = getValue(row.fields, "field_indexed");
            field.generalSearch = getValue(row.fields, "field_generalSearch");
            field.exact = getValue(row.fields, "field_exact");
            field.bidirectional = getValue(row.fields, "field_bidirectional");
            field.indexName = getValue(row.fields, "field_indexName");
            field.fixedOrder = getValue(row.fields, "field_fixedOrder");
            field.subfields = formFieldSubfields(getContainer(row.fields, "field_subfields"));
            field.removePermissions = formFieldRemovePermissions(getContainer(row.fields, "field_removePermissions"));
        });

        return ret;
    }

    function formFieldSubfields(subfields) {
        var ret = [];
        if(!subfields) {
            return ret;
        }

        subfields.rows.DEFAULT.forEach(function(row) {
            var key = getValue(row.fields, "field_subfield_key");
            if(key) {
                ret.push(key);
            }
        });
        return ret;
    }

    function formFieldRemovePermissions(permissions) {
        var ret = [];
        if(!permissions) {
            return ret;
        }

        permissions.rows.DEFAULT.forEach(function(row) {
            var permission = getValue(row.fields, "field_removePermissions_permission");
            if(permission) {
                ret.push(permission);
            }
        });
        return ret;
    }

    function formNamedTargets(namedTargets) {
        var ret = {};
        if(!namedTargets) {
            return ret;
        }

        namedTargets.rows.DEFAULT.map(function(row) {
            ret[getValue(row.fields, "namedTarget_key")] = formTarget(row.fields);
        });
        return ret;
    }

    function formRestrictions(restrictions) {
        var ret = [];
        if(!restrictions) {
            return ret;
        }

        restrictions.rows.DEFAULT.forEach(function(row) {
            var operation = {};
            ret.push(operation);
            operation.type = getValue(row.fields, "operation_type");
            operation.targets = formTargets(getContainer(row.fields, "targets"));
        });
        return ret;
    }

    function formCascade(cascade) {
        var ret = [];
        if(!cascade) {
            return ret;
        }

        cascade.rows.DEFAULT.forEach(function(row) {
            var operation = {};
            ret.push(operation);
            operation.type = getValue(row.fields, "operation_type");
            operation.targets = formTargets(getContainer(row.fields, "targets"));
        });
        return ret;
    }

    function formTargets(targets) {
        var ret = [];
        if(!targets) {
            return ret;
        }

        targets.rows.DEFAULT.forEach(function(row) {
            ret.push(formTarget(row.fields));
        });
        return ret;
    }

    function formTarget(fields) {
        var target = {};
        target.type = getValue(fields, "target_type");
        target.content = getValue(fields, "target_content");
        target.targets = formTargets(getContainer(fields, "targets"));
        target.checks = formChecks(getContainer(fields, "checks"));
        return target;
    }

    function formChecks(checks) {
        var ret = [];
        if(!checks) {
            return ret;
        }

        checks.rows.DEFAULT.forEach(function(row) {
            ret.push(formCheck(row.fields));
        });
        return ret;
    }

    function formCheck(fields) {
        var check = {};
        var condition = {};
        check.condition = condition;
        condition.type = getValue(fields, "target_check_condition_type");
        if(getValue(fields, "target_check_condition_target_type")) {
            var target = {};
            condition.target = target;
            target.type = getValue(fields, "target_check_condition_target_type");
            target.content = getValue(fields, "target_check_condition_target_content");
        }
        check.restrictors = formTargets(getContainer(fields, "targets"));
        return check;
    }


    // Generalised read of json
    /*function readJsonToData(json, options) {
        var fields = {};
        Object.keys(json).map(function(property) {
            var value = json[property];
            readJsonPropertyToData(fields, value, property);
        });
    }

    function readJsonPropertyToData(fields, value, property) {
        if(typeof value === 'object') {
            if(Array.isArray(value)) {
                readJsonArrayToData(fields, value, property);
            } else {
                readJsonObjectToData(fields, value, property);
            }
        } else {
            insertValue(fields, property, value);
        }
    }

    function readJsonObjectToData(fields, object, property) {
        if(!object) {
            return;
        }
        var containerKey = property;
        var container = insertContainer(fields, containerKey);
        Object.keys(object).map(function(property) {
            var propertyKey = containerKey+"_"+property;
            var value = object[property];
            var row = appendRow(container);
            readJsonPropertyToData(row.fields, value, propertyKey);
        });
    }

    function readJsonArrayToData(fields, array, property) {
        var containerKey = property+"_values";
        var valueKey = containerKey+"_value";
        var container = insertContainer(fields, containerKey);
        array.forEach(function(value) {
            var row = appendRow(container);
            readJsonPropertyToData(row.fields, valueKey, value);
        });
    }*/

    return {
        toEditor: function(json, options) {
            //readJsonToData(json, options); // The generalisation is not really suitable for this as is, some other way needs to be deviced
            var conf = {
                property: null,
                container: null,
                mapping: {
                    key: function(json, fields) {
                        insertValue(fields, "key_type", json.key.type);
                        insertValue(fields, "key_version", json.key.version);
                    },
                    displayId: "displayId",
                    selectionLists: setSelectionLists,
                    references: setReferences,
                    fields: setFields,
                    namedTargets: setNamedTargets,
                    restrictions: setRestrictions,
                    cascade: setCascade
                }
            };

            var fields = {};
            setJsonProperty(json, fields, conf);

            /*
            if(json.key) {
                insertValue(fields, "key_type", json.key.type);
                insertValue(fields, "key_version", json.key.version);
            }
            insertValue(fields, "displayId", json.displayId);
            setSelectionLists(json, fields);
            setReferences(json, fields);
            setFields(json, fields);
            setNamedTargets(json, fields);
            setRestrictions(json, fields);
            setCascade(json, fields);*/

            options.data = {
                fields: fields
            }
        },
        toConfiguration: function(options) {
            if(!options.data || !options.data.fields) {
                return {};
            }
            var fields = options.data.fields;
            return {
                key: {
                    type: getValue(fields, "key_type"),
                    version: getValue(fields, "key_version")
                },
                displayId: getValue(fields, "displayId"),
                selectionLists: formLists(getContainer(fields, "selectionLists")),
                references: formReferences(getContainer(fields, "references")),
                fields: formFields(getContainer(fields, "fields")),
                namedTargets: formNamedTargets(getContainer(fields, "namedTargets")),
                restrictions: formRestrictions(getContainer(fields, "restrictions")),
                cascade: formCascade(getContainer(fields, "cascade"))
            };
        }
    }
});