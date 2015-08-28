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

        function getValue(fields, key) {
            var field = getField(fields, key);
            if(!field || field.type !== 'VALUE' || !field.values || !field.values.DEFAULT) {
                return;
            }
            return field.values.DEFAULT.current;
        }

        function handleMapping(value, fields, mapping) {
            if(typeof mapping === "string") {
                setFieldWithValue(fields, mapping, value);
            } else if(typeof mapping === "function") {
                mapping(value, fields);
            } else if(typeof mapping === "object" && !Array.isArray(mapping)) {
                jsonToData(value, fields, mapping);
            }
        }

        function jsonToData(json, fields, configuration) {
            if(!fields || !configuration || !json) {
                return;
            }

            if(!configuration.container) {
                if(configuration.valueKey) {
                    setFieldWithValue(fields, configuration.valueKey, json);
                } else if(configuration.mapping) {
                    Object.keys(json).map(function(property) {
                        if(configuration.mapping && configuration.mapping[property]) {
                            handleMapping(json[property], fields, configuration.mapping[property]);
                        }
                    });
                }
            } else if(configuration.configuration) {
                var container = insertContainer(fields, configuration.container);
                Object.keys(json).map(function(property) {
                    var row = appendRow(container);
                    if(configuration.propertyKey) {
                        setFieldWithValue(row.fields, configuration.propertyKey, property);
                    }
                    jsonToData(json[property], row.fields, configuration.configuration);
                });
            }
        }

        function dataToJson(fields, configuration) {
            var ret = null;
            if(configuration.container) {
                if(configuration.configuration) {
                    var container = getContainer(fields, configuration.container);
                    if(container && container.rows && container.rows.DEFAULT) {
                        if(configuration.propertyKey) {
                            ret = {};
                            container.rows.DEFAULT.forEach(function(row) {
                                if(!row.removed) {
                                    ret[getValue(row.fields, configuration.propertyKey)] = dataToJson(row.fields, configuration.configuration);
                                }
                            });
                        } else {
                            ret = [];
                            container.rows.DEFAULT.forEach(function(row) {
                                if(!row.removed) {
                                    ret.push(dataToJson(row.fields, configuration.configuration));
                                }
                            });
                        }
                    } else if(!container) {
                        ret = configuration.propertyKey ? {} : [];
                    }
                }
            } else {
                if(configuration.valueKey) {
                    ret = getValue(fields, configuration.valueKey);
                } else if(configuration.mapping) {
                    ret = {};
                    Object.keys(configuration.mapping).forEach(function(property) {
                        var mapping = configuration.mapping[property];
                        ret[property] = (function(fields, mapping) {
                            if(typeof mapping === 'string') {
                                return getValue(fields, mapping);
                            } else if(typeof mapping === 'function') {
                                return mapping(fields)
                            } else if(typeof mapping === 'object' && !Array.isArray(mapping)) {
                                return dataToJson(fields, mapping);
                            } else {
                                return null;
                            }
                        }(fields, mapping));
                    })
                }
            }

            return ret;
        }

        return {
            jsonToData: function(json, fields, configuration) {
                jsonToData(json, fields, configuration);
            },
            setFieldWithValue: function(fields, key, value) {
                setFieldWithValue(fields, key, value);
            },
            dataToJson: function(fields, configuration) {
                return dataToJson(fields, configuration);
            }
        }
    }

    var parser = jsonParser();

    function selectionListsConf(optionMapping) {
        return {
            container: "selectionLists",
            propertyKey: "selectionLists_key",
            configuration: {
                mapping: {
                    key: "selectionLists_key",
                    type: "selectionLists_type",
                    freeTextKey: "selectionLists_freeTextKey",
                    sublistKey: "selectionLists_sublistKey",
                    includeEmpty: "selectionLists_includeEmpty",
                    reference: "selectionLists_reference",
                    freeText: {
                        container: "selectionLists_freeText_values",
                        configuration: {
                            valueKey: "selectionLists_freeText"
                        }
                    },
                    options: {
                        container: "selectionLists_options",
                        configuration: {
                            mapping: optionMapping
                        }
                    }
                }
            }
        }
    }

    function referencesConf() {
        return {
            container: "references",
            propertyKey: "reference_key",
            configuration: {
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
    }

    function fieldsConf() {
        return {
            container: "fields",
            propertyKey: "field_key",
            configuration: {
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
                        configuration: {
                            valueKey: "field_subfield_key"
                        }
                    },
                    removePermissions: {
                        container: "field_removePermissions",
                        configuration: {
                            valueKey: "field_removePermissions_permission"
                        }
                    }
                }
            }
        }
    }


    function namedTargetsConf(targetsHandler, checksHandler) {
        return {
            container: "namedTargets",
            propertyKey: "namedTarget_key",
            configuration: {
                mapping: {
                    type: "target_type",
                    content: "target_content",
                    targets: targetsHandler,
                    checks: checksHandler
                }
            }
        }
    }

    function restrictionsConf(targetsHandler) {
        return {
            container: "restrictions",
            configuration: {
                mapping: {
                    type: "operation_type",
                    targets: targetsHandler
                }
            }
        }
    }

    function cascadeConf(targetsHandler) {
        return {
            container: "cascade",
            configuration: {
                mapping: {
                    type: "operation_type",
                    targets: targetsHandler
                }
            }
        }
    }

    return {
        toEditor: function(json, options) {
            function optionMapping() {
                return {
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

            function setTargets(json, fields) {
                parser.jsonToData(json, fields, {
                    container: "targets",
                    configuration: {
                        mapping: {
                            type: "target_type",
                            content: "target_content",
                            targets: setTargets,
                            checks: setChecks
                        }
                    }
                });
            }

            function setChecks(json, fields) {
                // NOTE: This is an example of manual insertion and function type mapping, LEAVE AS IS
                parser.jsonToData(json, fields, {
                    container: "checks",
                    configuration: {
                        mapping: {
                            condition: function(json, fields) {
                                parser.setFieldWithValue(fields, "target_check_condition_type", json.type);
                                if(json.target) {
                                    parser.setFieldWithValue(fields, "target_check_condition_target_type", json.target.type);
                                    parser.setFieldWithValue(fields, "target_check_condition_target_content", json.target.content);
                                }
                            },
                            restrictors: setTargets
                        }
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
                    selectionLists: selectionListsConf(optionMapping()),
                    references: referencesConf(),
                    fields: fieldsConf(),
                    namedTargets: namedTargetsConf(setTargets, setChecks),
                    restrictions: restrictionsConf(setTargets),
                    cascade: cascadeConf(setTargets)
                }
            });
        },
        toConfiguration: function(options) {
            function optionMapping() {
                return {
                    value: "selectionLists_option_value",
                    "&title": {
                        mapping: {
                            default: "selectionLists_option_title_default",
                            en: "selectionLists_option_title_en",
                            sv: "selectionLists_option_title_sv"
                        }
                    }
                }
            }

            function targetsDataToJson(fields) {
                return parser.dataToJson(fields, {
                    container: "targets",
                    configuration: {
                        mapping: {
                            type: "target_type",
                            content: "target_content",
                            targets: targetsDataToJson,
                            checks: checksDataToJson
                        }
                    }
                });
            }

            function checksDataToJson(fields) {
                return parser.dataToJson(fields, {
                    container: "checks",
                    configuration: {
                        mapping: {
                            condition: {
                                mapping: {
                                    type: "target_check_condition_type",
                                    target: {
                                        mapping: {
                                            type: "target_check_condition_target_type",
                                            content: "target_check_condition_target_content"
                                        }
                                    }
                                }
                            },
                            restrictors: targetsDataToJson
                        }
                    }
                });
            }

            if(!options.data || !options.data.fields) {
                return {};
            }
            return parser.dataToJson(options.data.fields, {
                mapping: {
                    key: {
                        mapping: {
                            type: "key_type",
                            version: "key_version"
                        }
                    },
                    displayId: "displayId",
                    selectionLists: selectionListsConf(optionMapping()),
                    references: referencesConf(),
                    fields: fieldsConf(),
                    namedTargets: namedTargetsConf(targetsDataToJson, checksDataToJson),
                    restrictions: restrictionsConf(targetsDataToJson),
                    cascade: cascadeConf(targetsDataToJson)
                }
            });
        }
    }
});