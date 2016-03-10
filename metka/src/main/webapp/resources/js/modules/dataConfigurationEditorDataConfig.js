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

    return {
        selectionLists: {
            configurationTypes_list: {
                key: "configurationTypes_list",
                type: "VALUE",
                options: [{
                    value: "STUDY",
                    title: "Aineisto"
                }, {
                    value: "PUBLICATION",
                    title: "Julkaisu"
                }, {
                    value: "SERIES",
                    title: "Sarja"
                }, {
                    value: "STUDY_ATTACHMENT",
                    title: "Aineistoliite"
                }, {
                    value: "STUDY_VARIABLES",
                    title: "Muuttujajoukko"
                }, {
                    value: "STUDY_VARIABLE",
                    title: "Muuttuja"
                }, {
                    value: "STUDY_ERROR",
                    title: "Aineistovirhe"
                }, {
                    value: "BINDER_PAGE",
                    title: "Mapitus"
                }]
            },
            selectionLists_type_list: {
                key: "selectionLists_type_list",
                type: "LITERAL",
                options: [{
                    value: "VALUE"
                }, {
                    value: "LITERAL"
                }, {
                    value: "REFERENCE"
                }, {
                    value: "SUBLIST"
                }]
            },
            reference_type_list: {
                key: "reference_type_list",
                type: "LITERAL",
                options: [{
                    value: "REVISIONABLE"
                }, {
                    value: "REVISION"
                }, {
                    value: "JSON"
                }, {
                    value: "DEPENDENCY"
                }]
            },
            field_type_list: {
                key: "field_type_list",
                type: "LITERAL",
                options: [{
                    value: "STRING"
                }, {
                    value: "INTEGER"
                }, {
                    value: "REAL"
                }, {
                    value: "BOOLEAN"
                }, {
                    value: "REFERENCE"
                }, {
                    value: "CONTAINER"
                }, {
                    value: "REFERENCECONTAINER"
                }, {
                    value: "SELECTION"
                }, {
                    value: "DATE"
                }, {
                    value: "DATETIME"
                }, {
                    value: "TIME"
                }, {
                    value: "RICHTEXT"
                }]
            },
            operation_type_list: {
                key: "operation_type_list",
                type: "LITERAL",
                options: [{
                    value: "SAVE"
                }, {
                    value: "APPROVE"
                }, {
                    value: "REMOVE"
                }, {
                    value: "REMOVE_LOGICAL"
                }, {
                    value: "REMOVE_DRAFT"
                }, {
                    value: "REMOVE_REVISIONABLE"
                }, {
                    value: "RESTORE"
                }, {
                    value: "CLAIM"
                }, {
                    value: "RELEASE"
                }, {
                    value: "ALL"
                }]
            },
            target_type_list: {
                key: "target_type_list",
                type: "LITERAL",
                options: [{
                    value: "FIELD"
                }, {
                    value: "QUERY"
                }, {
                    value: "VALUE"
                }, {
                    value: "NAMED"
                }, {
                    value: "LANGUAGE"
                }, {
                    value: "PARENT"
                }, {
                    value: "CHILDREN"
                }]
            },
            condition_type_list: {
                key: "condition_type_list",
                type: "LITERAL",
                options: [{
                    value: "NOT_EMPTY"
                }, {
                    value: "IS_EMPTY"
                }, {
                    value: "EQUALS"
                }, {
                    value: "NOT_EQUALS"
                }, {
                    value: "UNIQUE"
                }, {
                    value: "INCREASING"
                }, {
                    value: "DECREASING"
                }, {
                    value: "FREE_TEXT"
                }, {
                    value: "REGEX"
                }]
            },
            displayId_list: { // This is built and updated dynamically
                key: "displayId_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            freeTextKey_list: { // This is built and updated dynamically
                key: "freeTextKey_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            sublistKey_list: { // This is built and updated dynamically
                key: "sublistKey_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            selectionLists_reference_list: { // This is built and updated dynamically
                key: "selectionLists_reference_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            field_reference_list: { // This is built and updated dynamically
                key: "field_reference_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            field_subfield_key_list: { // This is built and updated dynamically
                key: "field_subfield_key_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            field_selectionList_list: { // This is built and updated dynamically
                key: "field_selectionList_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            },
            reference_target_list: { // This is built and updated dynamically
                key: "reference_target_list",
                includeEmpty: true,
                type: "LITERAL",
                options: []
            }
        },
        fields: {
            // Basics
            key_type: {
                key: "key_type",
                type: "SELECTION",
                selectionList: "configurationTypes_list"
            },
            key_version: {
                key: "key_version",
                type: "INTEGER"
            },
            displayId: {
                key: "displayId",
                type: "SELECTION",
                selectionList: "displayId_list"
            },

            // SelectionLists
            selectionLists: {
                key: "selectionLists",
                type: "CONTAINER",
                fixedOrder: true,
                subfields: [
                    "selectionLists_key",
                    "selectionLists_type",
                    "selectionLists_default",
                    "selectionLists_includeEmpty",
                    "selectionLists_options",
                    "selectionLists_freeText_values",
                    "selectionLists_freeTextKey",
                    "selectionLists_sublistKey",
                    "selectionLists_reference"
                ]
            },
            selectionLists_key: {
                key: "selectionLists_key",
                type: "STRING",
                subfield: true
            },
            selectionLists_type: {
                key: "selectionLists_type",
                type: "SELECTION",
                selectionList: "selectionLists_type_list",
                subfield: true
            },
            selectionLists_default: {
                key: "selectionLists_default",
                type: "STRING",
                subfield: true
            },
            selectionLists_includeEmpty: {
                key: "selectionLists_includeEmpty",
                type: "BOOLEAN",
                subfield: true
            },
            selectionLists_freeText_values: {
                key: "selectionLists_freeText_values",
                type: "CONTAINER",
                fixedOrder: true,
                subfields: [
                    "selectionLists_freeText"
                ],
                subfield: true
            },
            selectionLists_freeText: {
                key: "selectionLists_freeText",
                type: "STRING",
                subfield: true
            },
            selectionLists_freeTextKey: {
                key: "selectionLists_freeTextKey",
                type: "SELECTION",
                selectionList: "freeTextKey_list",
                subfield: true
            },
            selectionLists_sublistKey: {
                key: "selectionLists_sublistKey",
                type: "SELECTION",
                selectionList: "sublistKey_list",
                subfield: true
            },
            selectionLists_reference: {
                key: "selectionLists_reference",
                type: "SELECTION",
                selectionList: "selectionLists_reference_list",
                subfield: true
            },
            selectionLists_options: {
                key: "selectionLists_options",
                type: "CONTAINER",
                subfield: true,
                subfields: [
                    "selectionLists_option_value",
                    "selectionLists_option_title_default",
                    "selectionLists_option_title_en",
                    "selectionLists_option_title_sv"
                ]
            },
            selectionLists_option_value: {
                key: "selectionLists_option_value",
                type: "STRING",
                subfield: true
            },
            selectionLists_option_title_default: {
                key: "selectionLists_option_title_default",
                type: "STRING",
                subfield: true
            },
            selectionLists_option_title_en: {
                key: "selectionLists_option_title_en",
                type: "STRING",
                subfield: true
            },
            selectionLists_option_title_sv: {
                key: "selectionLists_option_title_sv",
                type: "STRING",
                subfield: true
            },

            // References
            references: {
                key: "references",
                type: "CONTAINER",
                fixedOrder: true,
                subfields: [
                    "reference_key",
                    "reference_type",
                    "reference_target",
                    "reference_valuePath",
                    "reference_titlePath",
                    "reference_approveOnly",
                    "reference_ignoreRemoved"
                ]
            },
            reference_key: {
                key: "reference_key",
                type: "STRING",
                subfield: true
            },
            reference_type: {
                key: "reference_type",
                type: "SELECTION",
                selectionList: "reference_type_list",
                subfield: true
            },
            reference_target: {
                key: "reference_target",
                type: "SELECTION",
                selectionList: "reference_target_list",
                subfield: true
            },
            reference_valuePath: {
                key: "reference_valuePath",
                type: "STRING",
                subfield: true
            },
            reference_titlePath: {
                key: "reference_titlePath",
                type: "STRING",
                subfield: true
            },
            reference_approveOnly: {
                key: "reference_approveOnly",
                type: "BOOLEAN",
                subfield: true
            },
            reference_ignoreRemoved: {
                key: "reference_ignoreRemoved",
                type: "BOOLEAN",
                subfield: true
            },

            // Fields
            fields: {
                key: "fields",
                type: "CONTAINER",
                fixedOrder: true,
                subfields: [
                    "field_key",
                    "field_type",
                    "field_translatable",
                    "field_immutable",
                    //"field_maxValues", // maxValues is not implemented at the moment
                    "field_selectionList",
                    "field_subfields",
                    "field_subfield",
                    "field_reference",
                    "field_editable",
                    "field_writable",
                    "field_indexed",
                    "field_generalSearch",
                    "field_exact",
                    "field_bidirectional",
                    "field_indexName",
                    "field_fixedOrder",
                    "field_removePermissions"
                ]
            },
            field_key: {
                key: "field_key",
                type: "STRING",
                subfield: true
            },
            field_type: {
                key: "field_type",
                type: "SELECTION",
                selectionList: "field_type_list",
                subfield: true
            },
            field_translatable: {
                key: "field_translatable",
                type: "BOOLEAN",
                subfield: true
            },
            field_immutable: {
                key: "field_immutable",
                type: "BOOLEAN",
                subfield: true
            },
            field_selectionList: {
                key: "field_selectionList",
                type: "SELECTION",
                selectionList: "field_selectionList_list",
                subfield: true
            },
            field_subfields: {
                key: "field_subfields",
                type: "CONTAINER",
                subfields: [
                    "field_subfield_key"
                ],
                subfield: true
            },
            field_subfield_key: {
                key: "field_subfield_key",
                type: "SELECTION",
                selectionList: "field_subfield_key_list",
                subfield: true
            },
            field_subfield: {
                key: "field_subfield",
                type: "BOOLEAN",
                subfield: true
            },
            field_reference: {
                key: "field_reference",
                type: "SELECTION",
                selectionList: "field_reference_list",
                subfield: true
            },
            field_editable: {
                key: "field_editable",
                type: "BOOLEAN",
                subfield: true
            },
            field_writable: {
                key: "field_writable",
                type: "BOOLEAN",
                subfield: true
            },
            field_indexed: {
                key: "field_indexed",
                type: "BOOLEAN",
                subfield: true
            },
            field_generalSearch: {
                key: "field_generalSearch",
                type: "BOOLEAN",
                subfield: true
            },
            field_exact: {
                key: "field_exact",
                type: "BOOLEAN",
                subfield: true
            },
            field_bidirectional: {
                key: "field_bidirectional",
                type: "STRING",
                subfield: true
            },
            field_indexName: {
                key: "field_indexName",
                type: "STRING",
                subfield: true
            },
            field_fixedOrder: {
                key: "field_fixedOrder",
                type: "BOOLEAN",
                subfield: true
            },
            field_removePermissions: {
                key: "field_removePermissions",
                type: "CONTAINER",
                subfields: [
                    "field_removePermissions_permission"
                ],
                subfield: true
            },
            field_removePermissions_permission: {
                key: "field_removePermissions_permission",
                type: "STRING",
                subfield: true
            },

            // General for restrictions and cascade
            operation_type: {
                key: "operation_type",
                type: "SELECTION",
                selectionList: "operation_type_list",
                subfield: true
            },
            targets: {
                key: "targets",
                type: "CONTAINER",
                subfields: [
                    "target_type",
                    "target_content",
                    "targets",
                    "checks"
                ],
                subfield: true
            },
            target_type: {
                key: "target_type",
                type: "SELECTION",
                selectionList: "target_type_list",
                subfield: true
            },
            target_content: {
                key: "target_content",
                type: "STRING",
                subfield: true
            },
            checks: {
                key: "checks",
                type: "CONTAINER",
                subfields: [
                    "target_check_condition_type",
                    "target_check_condition_target_type",
                    "target_check_condition_target_content",
                    "targets"
                ]
            },
            target_check_condition_type: {
                key: "target_check_condition_type",
                type: "SELECTION",
                selectionList: "condition_type_list",
                subfield: true
            },
            target_check_condition_target_type: {
                key: "target_check_condition_target_type",
                type: "SELECTION",
                selectionList: "target_type_list",
                subfield: true
            },
            target_check_condition_target_content: {
                key: "target_check_condition_target_content",
                type: "STRING",
                subfield: true
            },

            // NamedTargets
            namedTargets: {
                key: "namedTargets",
                type: "CONTAINER",
                fixedOrder: true,
                subfields: [
                    "namedTarget_key",
                    "target_type",
                    "target_content",
                    "targets",
                    "checks"
                ]
            },
            namedTarget_key: {
                key: "namedTarget_key",
                type: "STRING",
                subfield: true
            },

            // Restrictions
            restrictions: {
                key: "restrictions",
                type: "CONTAINER",
                subfields: [
                    "operation_type",
                    "targets"
                ]
            },


            // Cascade
            cascade: {
                key: "cascade",
                type: "CONTAINER",
                subfields: [
                    "operation_type",
                    "targets"
                ]
            }
        }
    }
});