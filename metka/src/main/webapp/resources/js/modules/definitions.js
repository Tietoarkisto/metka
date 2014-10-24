// Shared JSON schema definitions
define({
    "agency": {
        "title": "Yksikkö",
        "type": "object",
        "options": {
            "disable_properties": true,
            "collapsed": false,
            "disable_edit_json": true
        },
        "$ref": "#/definitions/organizationEntry",
        "properties": {
            "&agencyname": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            },
            "&agencyabbr": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            },
            "sections": {
                "title": "Osastot",
                "type": "array",
                "options": {
                    "collapsed": true
                },
                "items": {
                    "$ref": "#/definitions/section"
                }
            }
        }
    },
    "organization": {
        "title": "Organisaatio",
        "type": "object",
        "options": {
            "disable_properties": true,
            "collapsed": false,
            "disable_edit_json": true
        },
        "$ref": "#/definitions/organizationEntry",
        "properties": {
            "&organizationname": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            },
            "&organizationabbr": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            },
            "agencies": {
                "options": {
                    "collapsed": true
                },
                "title": "Yksiköt",
                "type": "array",
                "items": {
                    "$ref": "#/definitions/agency"
                }
            }
        }
    },
    "organizationEntry": {
        "properties": {
            "pids": {
                "options": {
                    "collapsed": true
                },
                "$ref": "#/definitions/pids"
            }
        },
        "patternProperties": {
            "^&.+name$": {
                "title": "Nimi",
                "options": {
                    "disable_edit_json": true
                },
                "$ref": "#/definitions/translatableText"
            },
            "^&.+abbr$": {
                "title": "Lyhenne",
                "options": {
                    "disable_edit_json": true
                },
                "$ref": "#/definitions/translatableText"
            }
        }
    },
    "pids": {
        "type": "array",
        "format": "table",
        "items": {
            "title": "pid",
            "type": "object",
            "options": {
                "disable_properties": true,
                "disable_collapse": true,
                "disable_edit_json": true
            },
            "properties": {
                "pid": {
                    "type": "string"
                },
                "pidagency": {
                    "type": "string"
                }
            }
        }
    },
    "reference": {
        "type": "object",
        "format": "grid",
        "additionalProperties": false,
        "required": ["key", "target", "type"],
        "properties": {
            "key": {
                "type": "string"
            },
            "titlePath": {
                "type": "string"
            },
            "target": {
                "type": "string"
            },
            "type": {
                "type": "string",
                "options": {
                    "hidden": true
                }
            }
        }
    },
    "restrictionTargets": {
        "type": "array",
        "options": {
            "collapsed": true
        },
        "items": {
            "title": "Target",
            "$ref": "#/definitions/restrictionTarget"
        }
    },
    "restrictionTarget": {
        "allOf": [{
            "$ref": "#/definitions/type"
        }, {
            "title": "Target",
            "properties": {
                "content": {
                    "type": "string"
                }
            },
            "required": ["content"]
        }, {
            "oneOf": [{
                "title": "FIELD",
                "properties": {
                    "type": {
                        "template": "FIELD"
                    },
                    "targets": {
                        "options": {
                            "collapsed": true
                        },
                        "$ref": "#/definitions/restrictionTargets"
                    },
                    "checks": {
                        "options": {
                            "collapsed": true
                        },
                        "type": "array",
                        "items": {
                            "type": "object",
                            "title": "Check",
                            "properties": {
                                "condition": {
                                    "allOf": [{
                                        "$ref": "#/definitions/type"
                                    }, {
                                        "oneOf": [{
                                            "title": "NOT_EMPTY",
                                            "properties": {
                                                "type": {
                                                    "template": "NOT_EMPTY"
                                                },
                                                "target": {
                                                    "$ref": "#/definitions/restrictionTarget"
                                                }
                                            }
                                        }, {
                                            "title": "IS_EMPTY",
                                            "properties": {
                                                "type": {
                                                    "template": "IS_EMPTY"
                                                },
                                                "target": {
                                                    "$ref": "#/definitions/restrictionTarget"
                                                }
                                            }
                                        }, {
                                            "title": "EQUALS",
                                            "properties": {
                                                "type": {
                                                    "template": "EQUALS"
                                                },
                                                "target": {
                                                    "$ref": "#/definitions/restrictionTarget"
                                                }
                                            },
                                            "required": ["target"]
                                        }, {
                                            "title": "UNIQUE",
                                            "properties": {
                                                "type": {
                                                    "template": "UNIQUE"
                                                }
                                            }
                                        }, {
                                            "title": "INCREASING",
                                            "properties": {
                                                "type": {
                                                    "template": "INCREASING"
                                                }
                                            }
                                        }, {
                                            "title": "DECREASING",
                                            "properties": {
                                                "type": {
                                                    "template": "DECREASING"
                                                }
                                            }
                                        }]
                                    }]
                                },
                                "restrictors": {
                                    "$ref": "#/definitions/restrictionTargets"
                                }
                            }
                        }
                    }
                }
            }, {
                "title": "QUERY",
                "properties": {
                    "type": {
                        "template": "QUERY"
                    },
                    "checks": {
                        "options": {
                            "collapsed": true
                        },
                        "type": "array",
                        "items": {
                            "type": "object",
                            "title": "Check",
                            "properties": {
                                "condition": {
                                    "type": "object",
                                    "properties": {
                                        "type": {
                                            "type": "string",
                                            "enum": [
                                                "NOT_EMPTY",
                                                "IS_EMPTY",
                                                "EQUALS"
                                            ]
                                        }
                                    }
                                },
                                "restrictors": {
                                    "$ref": "#/definitions/restrictionTargets"
                                }
                            }
                        }
                    }
                }
            }, {
                "title": "VALUE",
                "properties": {
                    "type": {
                        "template": "VALUE"
                    }
                }
            }, {
                "title": "NAMED",
                "properties": {
                    "type": {
                        "template": "NAMED"
                    }
                }
            }, {
                "title": "LANGUAGE",
                "properties": {
                    "type": {
                        "template": "LANGUAGE"
                    }
                }
            }]
        }]
    },
    /*"restrictionCheck": {
        "type": "object",
        "title": "Check",
        "properties": {
            "condition": {
                "type": "object",
                "properties": {
                    "type": {
                        "type": "string",
                        "enum": [
                            "NOT_EMPTY",
                            "IS_EMPTY",
                            "EQUALS",
                            "UNIQUE",
                            "INCREASING",
                            "DECREASING"
                        ]
                    },
                    "target": {
                        "$ref": "#/definitions/restrictionTarget"
                    }
                }
            },
            "restrictors": {
                "$ref": "#/definitions/restrictionTargets"
            }
        }
    },*/
    "section": {
        "title": "Osasto",
        "type": "object",
        "options": {
            "disable_properties": true,
            "collapsed": false,
            "disable_edit_json": true
        },
        "$ref": "#/definitions/organizationEntry",
        "properties": {
            "&sectionname": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            },
            "&sectionabbr": {
                "options": {
                    "disable_properties": true,
                    "collapsed": true
                }
            }
        }
    },
    "selectionList": {
        "type": "object",
        "format": "grid",
        "additionalProperties": false,
        "required": ["key", "type"],
        "properties": {
            "key": {
                "type": "string"
            },
            "type": {
                "type": "string",
                "options": {
                    "hidden": true
                }
            },
            "default": {
                "type": "string"
            },
            "includeEmpty": {
                "type": "boolean",
                "default": true
            },
            "freeText": {
                "type": "array",
                "format": "table",
                "items": {
                    "title": "Arvo",
                    "type": "string"
                }
            },
            "freeTextKey": {
                "type": "string"
            }
        }
    },
    "translatableText": {
        "type": "object",
        "properties": {
            "default": {
                "title": "suomeksi",
                "type": "string"
            },
            "en": {
                "title": "englanniksi",
                "type": "string"
            },
            "sv": {
                "title": "ruotsiksi",
                "type": "string"
            }
        }
    },
    "type": {
        "type": "object",
        "properties": {
            "type": {
                "type": "string",
                "options": {
                    "hidden": true
                }
            }
        },
        "required": ["type"],
        "additionalProperties": false
    }
});