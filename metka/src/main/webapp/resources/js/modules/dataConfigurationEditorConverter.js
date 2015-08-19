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

    function setSelectionLists(configuration, data) {
        if(!configuration.selectionLists) {
            return;
        }
        var selectionLists = insertContainer(data, "selectionLists");
        Object.keys(configuration.selectionLists).map(function(key) {
            var list = configuration.selectionLists[key];
            var row = appendRow(selectionLists);
            insertValue(row.fields, "list_key", list.key);
            insertValue(row.fields, "list_type", list.type);
            //insertValue(row.fields, "list_default", list.default); // Default value doesn't seem to be implemented
            insertValue(row.fields, "list_freeTextKey", list.freeTextKey);
            insertValue(row.fields, "list_sublistKey", list.sublistKey);
            insertValue(row.fields, "list_includeEmpty", list.includeEmpty);
            if(list.freeText) setFreeTextValues(list, row);
            if(list.options) setOptions(list, row);
        });
    }

    function setFreeTextValues(selectionList, row) {
        var values = insertContainer(row.fields, "list_freeText_values");
        Object.keys(selectionList.freeText).forEach(function(key) {
            var value = selectionList.freeText[key];
            var row = appendRow(values);
            insertValue(row.fields, "list_freeText", value);
        });
    }

    function setOptions(selectionList, row) {
        var options = insertContainer(row.fields, "list_options");
        Object.keys(selectionList.options).forEach(function(key) {
            var option = selectionList.options[key];
            var row = appendRow(options);
            insertValue(row.fields, "list_option_value", option.value);
            if(MetkaJS.L10N.hasTranslation(option, 'title')) {
                insertValue(row.fields, "list_option_title_default", option['&title'].default);
                insertValue(row.fields, "list_option_title_en", option['&title'].en);
                insertValue(row.fields, "list_option_title_sv", option['&title'].sv);
            } else {
                insertValue(row.fields, "list_option_title_default", option.title);
            }
        });
    }

    function setReferences(configuration, data) {
        if(!configuration.references) {
            return;
        }
        var references = insertContainer(data, "references");
        Object.keys(configuration.references).map(function(key) {
            var reference = configuration.references[key];
            var row = appendRow(references);
            insertValue(row.fields, "reference_key", reference.key);
            insertValue(row.fields, "reference_type", reference.type);
            insertValue(row.fields, "reference_target", reference.target);
            insertValue(row.fields, "reference_valuePath", reference.valuePath);
            insertValue(row.fields, "reference_titlePath", reference.titlePath);
            insertValue(row.fields, "reference_approveOnly", reference.approvedOnly);
            insertValue(row.fields, "reference_ignoreRemoved", reference.ignoreRemoved);
        });
    }

    function setFields(configuration, data) {
        if(!configuration.fields) {
            return;
        }
        var fields = insertContainer(data, "fields");
        Object.keys(configuration.fields).map(function(key) {
            var field = configuration.fields[key];
            var row = appendRow(fields);
            insertValue(row.fields, "field_key", field.key);
            insertValue(row.fields, "field_type", field.type);
            insertValue(row.fields, "field_translatable", field.translatable);
            insertValue(row.fields, "field_immutable", field.immutable);
            insertValue(row.fields, "field_selectionList", field.selectionList);
            insertValue(row.fields, "field_subfield", field.subfield);
            insertValue(row.fields, "field_reference", field.reference);
            insertValue(row.fields, "field_editable", field.editable);
            insertValue(row.fields, "field_writable", field.writable);
            insertValue(row.fields, "field_indexed", field.indexed);
            insertValue(row.fields, "field_generalSearch", field.generalSearch);
            insertValue(row.fields, "field_exact", field.exact);
            insertValue(row.fields, "field_bidirectional", field.bidirectional);
            insertValue(row.fields, "field_indexName", field.indexName);
            insertValue(row.fields, "field_fixedOrder", field.fixedOrder);

            if(field.subfields) setFieldSubfields(field, row);
            if(field.removePermissions) setFieldRemovePermissions(field, row);
        });
    }

    function setFieldSubfields(field, row) {
        var subfields = insertContainer(row.fields, "field_subfields");
        Object.keys(field.subfields).forEach(function(key) {
            var subfield = field.subfields[key];
            var row = appendRow(subfields);
            insertValue(row.fields, "field_subfield_key", subfield);
        });
    }

    function setFieldRemovePermissions(field, row) {
        var permissions = insertContainer(row.fields, "field_removePermissions");
        Object.keys(field.removePermissions).forEach(function(key) {
            var permission = field.removePermissions[key];
            var row = appendRow(permissions);
            insertValue(row.fields, "field_removePermissions_permission", permission);
        });
    }

    function setNamedTargets(configuration, data) {
        if(!configuration.namedTargets) {
            return;
        }
        var namedTargets = insertContainer(data, "namedTargets");
        Object.keys(configuration.namedTargets).map(function(key) {
            var target = configuration.namedTargets[key];
            var row = appendRow(namedTargets);
            insertValue(row.fields, "namedTarget_key", key);
            setTargetValues(row.fields, target);
        });
    }

    function setRestrictions(configuration, data) {
        if(!configuration.restrictions) {
            return;
        }
        var restrictions = insertContainer(data, "restrictions");
        Object.keys(configuration.restrictions).forEach(function(key) {
            var operation = configuration.restrictions[key];
            var row = appendRow(restrictions);
            insertValue(row.fields, "operation_type", operation.type);
            if(operation.targets && operation.targets.length > 0) setTargets(operation.targets, row.fields);
        });
    }

    function setCascade(configuration, data) {
        if(!configuration.cascade) {
            return;
        }
        var cascade = insertContainer(data, "cascade");
        Object.keys(configuration.cascade).forEach(function(key) {
            var operation = configuration.cascade[key];
            var row = appendRow(cascade);
            insertValue(row.fields, "operation_type", operation.type);
            if(operation.targets && operation.targets.length > 0) setTargets(operation.targets, row.fields);
        });
    }

    function setTargetValues(fields, target) {
        insertValue(fields, "target_type", target.type);
        insertValue(fields, "target_content", target.content);
        if(target.targets && target.targets.length > 0) setTargets(target.targets, fields);
        if(target.checks && target.checks.length > 0) setChecks(target.checks, fields);
    }

    function setCheckValues(fields, check) {
        if(check.condition) {
            insertValue(fields, "target_check_condition_type", check.condition.type);
            if(check.condition.target) {
                insertValue(fields, "target_check_condition_target_type", check.condition.target.type);
                insertValue(fields, "target_check_condition_target_content", check.condition.target.content);
            }
        }
        if(check.restrictors && check.restrictors.length > 0) setTargets(check.restrictors, fields);
    }

    function setTargets(list, fields) {
        var targets = insertContainer(fields, "targets");
        list.forEach(function(target) {
            var row = appendRow(targets);
            setTargetValues(row.fields, target);
        });
    }

    function setChecks(list, fields) {
        var checks = insertContainer(fields, "checks");
        list.forEach(function(check) {
            var row = appendRow(checks);
            setCheckValues(row.fields, check);
        });
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
            ret[getValue(row.fields, "list_key")] = list;
            list.key = getValue(row.fields, "list_key");
            list.type = getValue(row.fields, "list_type");
            //list.default = getValue(row.fields, "list_default");
            list.freeTextKey = getValue(row.fields, "list_freeTextKey");
            list.sublistKey = getValue(row.fields, "list_sublistKey");
            list.includeEmpty = getValue(row.fields, "list_includeEmpty");
            list.freeText = formListFreeTextValues(getContainer(row.fields, "list_freeText_values"));
            list.options = formListOptions(getContainer(row.fields, "list_options"));
        });

        return ret;
    }

    function formListFreeTextValues(values) {
        var ret = [];
        if(!values) {
            return ret;
        }

        values.rows.DEFAULT.forEach(function(row) {
            var value = getValue(row.fields, "list_freeText");
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
            option.value = getValue(row.fields, "list_option_value");
            var title = {};
            title.default = getValue(row.fields, "list_option_title_default");
            title.en = getValue(row.fields, "list_option_title_en");
            title.sv = getValue(row.fields, "list_option_title_sv");
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
        toEditor: function(configuration, options) {
            var data = {};
            if(configuration.key) {
                insertValue(data, "key_type", configuration.key.type);
                insertValue(data, "key_version", configuration.key.version);
            }
            insertValue(data, "displayId", configuration.displayId);
            setSelectionLists(configuration, data);
            setReferences(configuration, data);
            setFields(configuration, data);
            setNamedTargets(configuration, data);
            setRestrictions(configuration, data);
            setCascade(configuration, data);
            options.data = {
                fields: data
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