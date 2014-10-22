// Shared JSON schema definitions
define({
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
    }
});