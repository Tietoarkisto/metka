define(function (require) {
    'use strict';

    return function (options) {
        delete options.field.displayType;
        var create = options.create;
        return {
            create: function (options) {
                JSONEditor.defaults.editors.object.options.collapsed = true;
                this.children().first().jsoneditor({
                    "schema": {
                        "title": "Datan konfiguraatio",
                        "options": {
                            "disable_properties": true,
                            "disable_collapse": false
                        },
                        "type": "object",
                        "properties": {
                            "key": {
                                "type": "object",
                                "headerTemplate": "{{key}} (type: {{self.type}}, version: {{self.version}})",
                                "options": {
                                    "disable_properties": true,
                                    "disable_collapse": false,
                                    "disable_edit_json": true
                                },
                                "format": "grid",
                                "properties": {
                                    "type": {
                                        "type": "string",
                                        "enum": ["STUDY", "SERIES", "PUBLICATION", "STUDY_ATTACHMENT", "STUDY_VARIABLES", "STUDY_VARIABLE"],
                                        "readOnly": true
                                    },
                                    "version": {
                                        "type": "integer"
                                    }
                                },
                                "additionalProperties": false
                            },
                            "references": {
                                "type": "object",
                                "patternProperties": {
                                    ".*": {
                                        "allOf": [{
                                            "$ref": "#/definitions/reference"
                                        }, {
                                            "oneOf": [{
                                                "title": "DEPENDENCY",
                                                "properties": {
                                                    "type": {
                                                        "template": "DEPENDENCY"
                                                    },
                                                    "valuePath": {
                                                        "type": "string"
                                                    }
                                                }
                                            }, {
                                                "title": "REVISIONABLE",
                                                "properties": {
                                                    "type": {
                                                        "template": "REVISIONABLE"
                                                    },
                                                    "approvedOnly": {
                                                        "type": "boolean",
                                                        "default": false
                                                    },
                                                    "ignoreRemoved": {
                                                        "type": "boolean",
                                                        "default": false
                                                    }
                                                }
                                            }, {
                                                "title": "JSON",
                                                "properties": {
                                                    "type": {
                                                        "template": "JSON"
                                                    },
                                                    "valuePath": {
                                                        "type": "string"
                                                    }
                                                }
                                            }]
                                        }]
                                    }
                                }
                            },
                            "selectionLists": {
                                "type": "object",
                                "patternProperties": {
                                    ".*": {
                                        "allOf": [{
                                            "$ref": "#/definitions/selectionList"
                                        }, {
                                            "oneOf": [{
                                                "title": "VALUE",
                                                "properties": {
                                                    "type": {
                                                        "template": "VALUE"
                                                    },
                                                    "options": {
                                                        "type": "array",
                                                        "items": {
                                                            "title": "Option",
                                                            "type": "object",
                                                            "format": "grid",
                                                            "properties": {
                                                                "&title": {
                                                                    "title": "Teksti",
                                                                    "format": "grid",
                                                                    "options": {
                                                                        "disable_edit_json": true
                                                                    },
                                                                    "$ref": "#/definitions/translatableText"
                                                                },
                                                                "value": {
                                                                    "title": "Arvo",
                                                                    "type": "string"
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }, {
                                                "title": "LITERAL",
                                                "properties": {
                                                    "type": {
                                                        "template": "LITERAL"
                                                    },
                                                    "options": {
                                                        "type": "array",
                                                        "items": {
                                                            "title": "Option",
                                                            "type": "object",
                                                            "format": "grid",
                                                            "properties": {
                                                                "value": {
                                                                    "title": "Arvo",
                                                                    "type": "string"
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }, {
                                                "title": "REFERENCE",
                                                "properties": {
                                                    "type": {
                                                        "template": "REFERENCE"
                                                    },
                                                    "reference": {
                                                        "type": "string"
                                                    }
                                                }
                                            }, {
                                                "title": "SUBLIST",
                                                "properties": {
                                                    "type": {
                                                        "template": "SUBLIST"
                                                    },
                                                    "sublistKey": {
                                                        "type": "string",
                                                        "description": "Listan avain"
                                                    }
                                                }
                                            }]
                                        }]
                                    }
                                }
                            },
                            "fields": {
                                "type": "object",
                                "patternProperties": {
                                    ".*": {
                                        "type": "object",
                                        "format": "grid",
                                        "properties": {
                                            "key": {
                                                "type": "string"
                                            },
                                            "type": {
                                                "type": "string",
                                                "enum": [
                                                    "STRING",
                                                    "INTEGER",
                                                    "REAL",
                                                    "REFERENCE",
                                                    "CONTAINER",
                                                    "REFERENCECONTAINER",
                                                    "SELECTION",
                                                    "CONCAT",
                                                    "DATE",
                                                    "DATETIME",
                                                    "TIME",
                                                    "RICHTEXT"
                                                ]
                                            },
                                            "unique": {
                                                "type": "boolean"
                                            },
                                            "translatable": {
                                                "type": "boolean"
                                            },
                                            "immutable": {
                                                "type": "boolean"
                                            },
                                            "display": {
                                                "type": "boolean"
                                            },
                                            "subfield": {
                                                "type": "boolean"
                                            },
                                            "editable": {
                                                "type": "boolean"
                                            },
                                            "writable": {
                                                "type": "boolean"
                                            },
                                            "indexed": {
                                                "type": "boolean"
                                            },
                                            "exact": {
                                                "type": "boolean"
                                            }
                                        },
                                        "additionalProperties": false
                                    }
                                }
                            },
                            "restrictions": {
                                "type": "array",
                                "options": {
                                    "collapsed": true
                                },
                                "items": {
                                    "type": "object",
                                    "title": "Operation",
                                    "properties": {
                                        "type": {
                                            "type": "string",
                                            "enum": [
                                                "SAVE",
                                                "APPROVE",
                                                "DELETE"
                                            ]
                                        },
                                        "targets": {
                                            "options": {
                                                "collapsed": true
                                            },
                                            "$ref": "#/definitions/restrictionTargets"
                                        }
                                    },
                                    "additionalProperties": false
                                }
                            },
                            "namedTargets": {
                                "type": "object",
                                "patternProperties": {
                                    ".*": {
                                        // without headerTemplate, title default to "Target" (from definition)
                                        "headerTemplate": "{{key}}",
                                        "$ref": "#/definitions/restrictionTarget"
                                    }
                                }
                            },
                            "displayId": {
                                "type": "string"
                            }
                        },
                        "additionalProperties": false,
                        "definitions": require('./../../definitions')
                    }
                });
                create.apply(this, arguments);
            }
        };
    };
});
