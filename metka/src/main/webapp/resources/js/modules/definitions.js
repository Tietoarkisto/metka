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
    "field": {
        "$ref": "#/definitions/simpleObject",
        "properties": {
            "key": {
                "description": "Kenttäavain. Tulee olla sama kuin `fields`-objektin ominaisuuden nimi."
            },
            "translatable": {
                "type": "boolean"
            },
            "immutable": {
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
            "generalSearch": {
                "type": "boolean"
            },
            "exact": {
                "type": "boolean"
            }
        },
        "additionalProperties": false
    },
    "fieldContainer": {
        properties: {
            generalSearch: {
                options: {
                    hidden: true
                }
            },
            exact: {
                options: {
                    hidden: true
                }
            },
            maxValues: {
                oneOf: [{
                    type: "integer"
                }, {
                    type: "null"
                }]
            },
            subfields: {
                type: "array",
                items: {
                    title: "Subfield key",
                    type: "string"
                }
            },
            removePermissions: {
                type: "array",
                items: {
                    title: "Permission name",
                    type: "string"
                }
            },
            fixedOrder: {
                type: "boolean"
            }
        }
    },
    "hasTarget": {
        "title": "Has target",
        "properties": {
            "target": {
                "title": "Type",
                "$ref": "#/definitions/restrictionTarget"
            }
        },
        "additionalProperties": false
    },
    "noTarget": {
        "title": "No target",
        "properties": {
            "target": {
                "type": "null"
            }
        },
        "additionalProperties": false
    },
    "namedTargets": {
        "type": "object",
        "patternProperties": {
            ".*": {
                "headerTemplate": "{{key}}",
                "$ref": "#/definitions/restrictionTarget"
            }
        }
    },
    "option": {
        "title": "Option",
        "type": "object",
        "format": "grid",
        "options": {
            "disable_edit_json": true,
            "disable_properties": true
        },
        "properties": {
            "value": {
                "$ref": "#/definitions/simpleValue"
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
        "$ref": "#/definitions/simpleObject",
        "required": ["target"],
        "properties": {
            "key": {
                "description": "Referenssin avain. Tulee olla sama kuin `references`-objektin ominaisuuden nimi."
            },
            "titlePath": {
                oneOf: [{
                    title: "Has title",
                    type: "string"
                }, {
                    title: "No title",
                    type: "null"
                }]
            },
            "target": {
                "type": "string"
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
        "type": "object",
        "additionalProperties": false,
        "properties": {
            "type": {
                "type": "string",
                "options": {
                    "hidden": true
                }
            },
            "targets": {
                "options": {
                    "hidden": true
                }
            },
            "checks": {
                "options": {
                    "hidden": true
                }
            }
        },
        "required": ["type", "content"],
        "oneOf": [{
            "title": "FIELD",
            "properties": {
                "type": {
                    "template": "FIELD"
                },
                "content": {
                    "description": "Kenttäavain, johon rajoitteet kohdistetaan.",
                    "type": "string"
                },
                "targets": {
                    "options": {
                        "collapsed": true,
                        "hidden": false
                    },
                    "$ref": "#/definitions/restrictionTargets"
                },
                "checks": {
                    "description": "Tarkistukset, jotka tehdään tämän target-objektin validoinnin yhteydessä.",
                    "options": {
                        "collapsed": true,
                        "hidden": false
                    },
                    "type": "array",
                    "items": {
                        "type": "object",
                        "title": "Check",
                        "options": {
                            "disable_properties": true
                        },
                        "additionalProperties": false,
                        "properties": {
                            "condition": {
                                "description": "Tietokentän sisällölle määritelty vaatimus.",
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
                                "oneOf": [{
                                    "description": "`Has target` / `No target` -valinta määrittää, onko vaatimuksella tarkentavia target-objekteja. Näiden target-objektien on oltava `LANGUAGE`-tyyppisiä.",
                                    "title": "NOT_EMPTY",
                                    "properties": {
                                        "type": {
                                            "template": "NOT_EMPTY"
                                        }
                                    },
                                    "oneOf": [{
                                        "$ref": "#/definitions/hasTarget"
                                    }, {
                                        "$ref": "#/definitions/noTarget"
                                    }]
                                }, {
                                    "description": "`Has target` / `No target` -valinta määrittää, onko vaatimuksella tarkentavia target-objekteja. Näiden target-objektien on oltava `LANGUAGE`-tyyppisiä.",
                                    "title": "IS_EMPTY",
                                    "additionalProperties": false,
                                    "properties": {
                                        "type": {
                                            "template": "IS_EMPTY"
                                        }
                                    },
                                    "oneOf": [{
                                        "$ref": "#/definitions/hasTarget"
                                    }, {
                                        "$ref": "#/definitions/noTarget"
                                    }]
                                }, {
                                    "description": "`Has target` / `No target` -valinta määrittää, onko vaatimuksella tarkentavia target-objekteja.",
                                    "title": "EQUALS",
                                    "options": {
                                        "collapsed": false,
                                        "disable_properties": true
                                    },
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
                                    "description": "`Has target` / `No target` -valinta määrittää, onko vaatimuksella tarkentavia target-objekteja.",
                                    "title": "NOT_EQUALS",
                                    "options": {
                                        "collapsed": false,
                                        "disable_properties": true
                                    },
                                    "properties": {
                                        "type": {
                                            "template": "NOT_EQUALS"
                                        },
                                        "target": {
                                            "$ref": "#/definitions/restrictionTarget"
                                        }
                                    },
                                    "required": ["target"]
                                }, {
                                    "title": "UNIQUE",
                                    "options": {
                                        "disable_collapse": true,
                                        "disable_properties": true
                                    },
                                    "properties": {
                                        "type": {
                                            "template": "UNIQUE"
                                        }
                                    },
                                    "additionalProperties": false
                                }, {
                                    "title": "INCREASING",
                                    "options": {
                                        "disable_collapse": true,
                                        "disable_properties": true
                                    },
                                    "properties": {
                                        "type": {
                                            "template": "INCREASING"
                                        }
                                    },
                                    "additionalProperties": false
                                }, {
                                    "title": "DECREASING",
                                    "options": {
                                        "disable_collapse": true,
                                        "disable_properties": true
                                    },
                                    "properties": {
                                        "type": {
                                            "template": "DECREASING"
                                        }
                                    },
                                    "additionalProperties": false
                                }]
                            },
                            "restrictors": {
                                "description": "Target-objektit, jotka määrittelevät, suoritetaanko tämä tarkistus. Jos näitä ei validoida onnistuneesti, tarkistusta ei suoriteta.",
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
                "content": {
                    "description": "Lucene-haku, joka suorietaan validointivaiheessa ja johon rajoitteet kohdistetaan.",
                    "type": "string"
                },
                "checks": {
                    "description": "Tarkistukset, jotka tehdään tämän target-objektin validoinnin yhteydessä.",
                    "options": {
                        "collapsed": true,
                        "hidden": false
                    },
                    "type": "array",
                    "items": {
                        "type": "object",
                        "title": "Check",
                        "options": {
                            "disable_properties": true
                        },
                        "additionalProperties": false,
                        "properties": {
                            "condition": {
                                "description": "Lucene-haun tulokselle määritelty vaatimus.",
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
                                "description": "Target-objektit, jotka määrittelevät, suoritetaanko tämä tarkistus. Jos näitä ei validoida onnistuneesti, tarkistusta ei suoriteta.",
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
                },
                "content": {
                    "description": "Tämän target-objektin arvo jota käytetään muiden rajoitteiden validoinnissa.",
                    "type": "string"
                }
            }
        }, {
            "title": "LANGUAGE",
            "properties": {
                "type": {
                    "template": "LANGUAGE"
                },
                "content": {
                    "description": "Kielikoodi, johon rajoitteiden validointi kohdistetaan.",
                    "type": "string"
                }
            }
        }, {
            "title": "NAMED",
            "properties": {
                "type": {
                    "template": "NAMED"
                },
                "content": {
                    "description": "`namedTargets`-objektin ominaisuuden nimi.",
                    "type": "string"
                }
            }
        }]
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
    },
    "selectionList": {
        "$ref": "#/definitions/simpleObject",
        "properties": {
            "key": {
                "description": "Valintalistan avain. Tulee olla sama kuin `selectionLists`-objektin ominaisuuden nimi."
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
                "$ref": "#/definitions/simpleArray",
                "items": {
                    "$ref": "#/definitions/simpleValue"
                }
            },
            "freeTextKey": {
                "type": "string"
            }
        }
    },
    "simpleArray": {
        "type": "array",
        "format": "table",
        "options": {
            "collapsed": false,
            "disable_collapse": true
        }
    },
    "simpleObject": {
        "type": "object",
        "format": "grid",
        "required": ["type", "key"],
        "additionalProperties": false,
        "properties": {
            "key": {
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
    "simpleValue": {
        "title": "Arvo",
        "type": "string"
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
        "required": ["type"]//,
        //"additionalProperties": false
    }
});