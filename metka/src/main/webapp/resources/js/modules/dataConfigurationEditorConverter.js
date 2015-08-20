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
                values: type === 'VALUE' ? {
                    DEFAULT: {
                        current: null
                    }
                } : null,
                rows: type !== 'VALUE' ? {
                    DEFAULT: []
                } : null
            }
        }

        function insertContainer(fields, key) {
            fields[key] = getNewField(key, 'CONTAINER');
            return fields[key];
        }

        function appendRow(field) {
            var row = {
                key: field.key,
                fields: {}
            };
            field.rows.DEFAULT.push(row);
            return row;
        }

        function setFieldWithValue(fields, key, value) {
            fields[key] = getNewField(key, 'VALUE');
            fields[key].values.DEFAULT.current = value;
            return fields[key];
        }

        function setJsonProperty(json, fields, configuration) {
            if(!fields || !configuration || !json || (configuration.property && !json[configuration.property])) {
                return;
            }
            var container = configuration.container ? insertContainer(fields, configuration.container) : null;
            Object.keys(configuration.property ? json[configuration.property] : json).map(function(key) {
                var row = container ? appendRow(container) : null;

                var value = configuration.property ? json[configuration.property][key] : json[key];
                if(row && configuration.propertyKey) {
                    setFieldWithValue(row.fields, configuration.propertyKey, key);
                }
                if(row && configuration.valueMapping) {
                    if(typeof configuration.valueMapping === "string") {
                        setFieldWithValue(row.fields, configuration.valueMapping, value);
                    } else if(typeof configuration.valueMapping === "function") {
                        configuration.valueMapping(value, row.fields);
                    } else if(typeof configuration.valueMapping === "object" && !Array.isArray(configuration.valueMapping)) {
                        setJsonProperty(value, row.fields, configuration.valueMapping);
                    }
                } else if(!configuration.valueMapping && configuration.mapping) {
                    if(row) {
                        Object.keys(configuration.mapping).map(function(property) {
                            if(typeof configuration.mapping[property] === "string") {
                                setFieldWithValue(row.fields, configuration.mapping[property], value[property]);
                            } else if(typeof configuration.mapping[property] === "function") {
                                configuration.mapping[property](value[property], row.fields);
                            } else if(typeof configuration.mapping[property] === "object" && !Array.isArray(configuration.mapping[property])) {
                                setJsonProperty(value[property], row.fields, configuration.mapping[property]);
                            }
                        });
                    } else if(configuration.mapping[key]) {
                        if(typeof configuration.mapping[key] === "string") {
                            setFieldWithValue(fields, configuration.mapping[key], value);
                        } else if(typeof configuration.mapping[key] === "function") {
                            configuration.mapping[key](value, fields);
                        } else if(typeof configuration.mapping[key] === "object" && !Array.isArray(configuration.mapping[key])) {
                            setJsonProperty(value, fields, configuration.mapping[key]);
                        }
                    }
                }
            });
        }

        return {
            jsonToData: function(json, fields, configuration) {
                setJsonProperty(json, fields, configuration);
            },
            setFieldWithValue: function(fields, key, value) {
                setFieldWithValue(fields, key, value);
            },
            dataToJson: function(fields, json, configuration) {
                /*if(!transferField || !configuration || !json /!*|| (configuration.property && !json[configuration.property])*!/) {
                    return;
                }

                if(configuration.valueMapping) {

                }

                Object.keys(configuration.property ? json[configuration.property] : json).map(function(key) {
                    var row = container ? appendRow(container) : null;

                    var value = configuration.property ? json[configuration.property][key] : json[key];
                    if(row && configuration.propertyKey) {
                        setFieldWithValue(row.fields, configuration.propertyKey, key);
                    }
                    if(row && configuration.valueMapping) {
                        if(typeof configuration.valueMapping === "string") {
                            setFieldWithValue(row.fields, configuration.valueMapping, value);
                        } else if(typeof configuration.valueMapping === "function") {
                            configuration.valueMapping(value, row.fields);
                        } else if(typeof configuration.valueMapping === "object" && !Array.isArray(configuration.valueMapping)) {
                            setJsonProperty(value, row.fields, configuration.valueMapping);
                        }
                    } else if(!configuration.valueMapping && configuration.mapping) {
                        if(row) {
                            Object.keys(configuration.mapping).map(function(property) {
                                if(typeof configuration.mapping[property] === "string") {
                                    setFieldWithValue(row.fields, configuration.mapping[property], value[property]);
                                } else if(typeof configuration.mapping[property] === "function") {
                                    configuration.mapping[property](value[property], row.fields);
                                } else if(typeof configuration.mapping[property] === "object" && !Array.isArray(configuration.mapping[property])) {
                                    setJsonProperty(value[property], row.fields, configuration.mapping[property]);
                                }
                            });
                        } else if(configuration.mapping[key]) {
                            if(typeof configuration.mapping[key] === "string") {
                                setFieldWithValue(fields, configuration.mapping[key], value);
                            } else if(typeof configuration.mapping[key] === "function") {
                                configuration.mapping[key](value, fields);
                            } else if(typeof configuration.mapping[key] === "object" && !Array.isArray(configuration.mapping[key])) {
                                setJsonProperty(value, fields, configuration.mapping[key]);
                            }
                        }
                    }
                });







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

                return ret;*/
            }
        }
    }

    var parser = jsonParser();

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

    return {
        toEditor: function(json, options) {
            function setSelectionLists() {
                return {
                    container: "selectionLists",
                    mapping: {
                        key: "selectionLists_key",
                        type: "selectionLists_type",
                        freeTextKey: "selectionLists_freeTextKey",
                        sublistKey: "selectionLists_sublistKey",
                        includeEmpty: "selectionLists_includeEmpty",
                        freeText: {
                            container: "selectionLists_freeText_values",
                            valueMapping: "selectionLists_freeText"
                        },
                        options: {
                            container: "selectionLists_options",
                            valueMapping: {
                                mapping: {
                                    value: "selectionLists_option_value",
                                    title: "selectionLists_option_title_default",
                                    "&title": {
                                        mapping: {
                                            default: "selectionLists_option_title_default",
                                            en: "selectionLists_option_title_en",
                                            sv: "selectionLists_option_title_sv"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            function setReferences() {
                return {
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
                }
            }

            function setFields() {
                return {
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
                        subfields: {
                            container: "field_subfields",
                            valueMapping: "field_subfield_key"
                        },
                        removePermissions: {
                            container: "field_removePermissions",
                            valueMapping: "field_removePermissions_permission"
                        }
                    }
                }
            }

            function setNamedTargets() {
                return {
                    container: "namedTargets",
                    propertyKey: "namedTarget_key",
                    mapping: {
                        type: "target_type",
                        content: "target_content",
                        targets: setTargets,
                        checks: setChecks
                    }
                }
            }

            function setRestrictions() {
                return {
                    container: "restrictions",
                    mapping: {
                        type: "operation_type",
                        targets: setTargets
                    }
                }
            }

            function setCascade() {
                return {
                    container: "cascade",
                    mapping: {
                        type: "operation_type",
                        targets: setTargets
                    }
                }
            }

            function setTargets(json, fields) {
                parser.jsonToData(json, fields, {
                    container: "targets",
                    mapping: {
                        type: "target_type",
                        content: "target_content",
                        targets: setTargets,
                        checks: setChecks
                    }
                });
            }

            function setChecks(json, fields) {
                parser.jsonToData(json, fields, {
                    container: "checks",
                    mapping: {
                        condition: function(json, fields) {
                            // NOTE: This is an example of manual insertion and function type mapping, LEAVE AS IS
                            parser.setFieldWithValue(fields, "target_check_condition_type", json.type);
                            if(json.target) {
                                parser.setFieldWithValue(fields, "target_check_condition_target_type", json.target.type);
                                parser.setFieldWithValue(fields, "target_check_condition_target_content", json.target.content);
                            }
                        },
                        restrictors: setTargets
                    }
                });
            }

            options.data = {
                fields: {}
            };
            parser.jsonToData(json, options.data.fields, {
                mapping: {
                    key: {
                        mapping: {
                            type: "key_type",
                            version: "key_version"
                        }
                    },
                    displayId: "displayId",
                    selectionLists: setSelectionLists(),
                    references: setReferences(),
                    fields: setFields(),
                    namedTargets: setNamedTargets(),
                    restrictions: setRestrictions(),
                    cascade: setCascade()
                }
            });
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