define(function (require) {
    'use strict';

    /**
     * Add localizations for general UI elements such as buttons
     * as well as all error messages even though most of these will not be needed at one time
     */

    require('./addTranslation')('', {
        "page": {
            "&title": {
                "default": "Yhteiskuntatieteellinen tietoarkisto - Metka"
            }
        },

        "topmenu": {
            "&desktop": {
                "default": "Työpöytä"
            },
            "&expert": {
                "default": "Eksperttihaku"
            },
            "&study": {
                "default": "Aineistot"
            },
            "&variables": {
                "default": "Muuttujat"
            },
            "&publication": {
                "default": "Julkaisut"
            },
            "&series": {
                "default": "Sarjat"
            },
            "&binder": {
                "default": "Mapit"
            },
            "&report": {
                "default": "Raportit"
            },
            "&settings": {
                "default": "Asetukset"
            },
            "&help": {
                "default": "Ohjeet"
            },
            "&logout": {
                "default": "Kirjaudu ulos"
            }
        },

        "state": {
            "&DRAFT": {
                "default": "LUONNOS"
            },
            "&APPROVED": {
                "default": "HYVÄKSYTTY"
            },
            "&REMOVED": {
                "default": "POISTETTU"
            }
        },

        "type": {
            "SERIES": {
                "&title": {
                    "default": "Sarja"
                },
                "&search": {
                    "default": "Sarjahaku"
                }
            },
            "STUDY": {
                "&title": {
                    "default": "Aineisto"
                },
                "&search": {
                    "default": "Aineistohaku"
                },
                "erroneous": {
                    "&title": {
                        "default": "Virheelliset"
                    },
                    "table": {
                        "&id": {
                            "default": "Aineistonumero"
                        },
                        "&name": {
                            "default": "Aineiston nimi"
                        },
                        "&errorPointCount": {
                            "default": "Virhepisteet"
                        }
                    }
                }
            },
            "STUDY_VARIABLES": {
                "&title": {
                    "default": "Muuttujat"
                },
                "&search": {
                    "default": "Muuttujahaku"
                }
            },
            "PUBLICATION": {
                "&title": {
                    "default": "Julkaisu"
                },
                "&search": {
                    "default": "Julkaisuhaku"
                }
            },
            "BINDERS": {
                "&title": {
                    "default": "Mapit"
                }
            },
            "SETTINGS": {
                "&title": {
                    "default": "Hallinta"
                }
            }
        },

        "general": {
            "buttons": {
                "&add": {
                    "default": "Lisää"
                },
                "&addSeries": {
                    "default": "Lisää sarja"
                },
                "&cancel": {
                    "default": "Peruuta"
                },
                "&close": {
                    "default": "Sulje"
                },
                "&download": {
                    "default": "Lataa"
                },
                "&upload": {
                    "default": "Lataa"
                },
                "&ok": {
                    "default": "OK"
                },
                "&save": {
                    "default": "Tallenna"
                },
                "&search": {
                    "default": "Hae"
                },
                "&remove": {
                    "default": "Poista"
                },
                "&no": {
                    "default": "Ei"
                },
                "&yes": {
                    "default": "Kyllä"
                }
            },

            "revision": {
                "compare": {
                    "&begin": {
                        "default": "Alku"
                    },
                    "&changed": {
                        "default": "Muutos"
                    },
                    "&end": {
                        "default": "Loppu"
                    },
                    "&modifier": {
                        "default": "Muuttaja"
                    },
                    "&modifyDate": {
                        "default": "Muutospvm"
                    },
                    "&property": {
                        "default": "Ominaisuus"
                    },
                    "&original": {
                        "default": "Alkuperäinen"
                    },
                    "&title": {
                        "default": "Revisioiden vertailu (revisio {0} -> revisio {1})"
                    }
                },
                "&compare": {
                    "default": "Vertaa"
                },
                "&publishDate": {
                    "default": "Julkaisupvm"
                },
                "&replace": {
                    "default": "Korvaa"
                },
                "&revisions": {
                    "default": "Revisiot"
                }
            },

            "saveInfo": {
                "&savedAt": {
                    "default": "Päivämäärä"
                },
                "&savedBy": {
                    "default": "Tallentaja"
                }
            },

            "selection": {
                "&empty": {
                    "default": "-- Valitse --"
                }
            },

            "table": {
                "&add": {
                    "default": "Lisää"
                }
            },

            "&id": {
                "default": "ID"
            },

            "&revision": {
                "default": "Revisio"
            }
        },

        "search": {
            "state": {
                "&title": {
                    "default": "Hae:"
                },
                "&APPROVED": {
                    "default": "Hyväksyttyjä"
                },
                "&DRAFT": {
                    "default": "Luonnoksia"
                },
                "&REMOVED": {
                    "default": "Poistettuja"
                }
            },
            "result": {
                "&title": {
                    "default": "Hakutulos"
                },
                "&amount": {
                    "default": "Hakutuloksia: {length}"
                },
                "state": {
                    "&title": {
                        "default": "Tila"
                    },
                    "&APPROVED": {
                        "default": "Hyväksytty"
                    },
                    "&DRAFT": {
                        "default": "Luonnos"
                    },
                    "&REMOVED": {
                        "default": "Poistettu"
                    }
                }
            }
        },

        "settings": {
            "&title": {
                "default": "Asetukset"
            },

            "upload": {
                "dataConfiguration": {
                    "&title": {
                        "default": "Datan konfiguraatio"
                    },
                    "&upload": {
                        "default": "Lataa datan konfiguraatio"
                    }
                },
                "guiConfiguration": {
                    "&title": {
                        "default": "GUI konfiguraatio"
                    },
                    "&upload": {
                        "default": "Lataa GUI konfiguraatio"
                    }
                },
                "miscJson": {
                    "&title": {
                        "default": "Json tiedosto"
                    },
                    "&upload": {
                        "default": "Lataa Json tiedosto"
                    }
                }
            }
        },

        "dialog": {
        },

        "alert": {
            "notice": {
                "&title": {
                    "default": "Huomio"
                },
                "approve": {
                    "success": "Luonnos hyväksytty onnistuneesti."
                },
                "save": {
                    "success": "Luonnos tallennettu onnistuneesti."
                }
            },

            "error": {
                "&title": {
                    "default": "Virhe"
                },
                "approve": {
                    "fail": {
                        "save": "Luonnoksen hyväksymisessä tapahtui virhe tallennuksen aikana.",
                        "validate": "Luonnoksen hyväksymisessä tapahtui virhe datan validoinnin aikana."
                    }
                },
                "save": {
                    "fail": "Luonnoksen tallentamisessa tapahtui virhe."
                }
            },

            "gui": {
                "missingButtonHandler": {
                    "&text": {
                        "default": 'Ei käsittelijää painikkeelle [{0}] otsikolla "{1}"'
                    }
                }
            }
        },

        "confirmation": {
            "&title": {
                "default": "Varmistus"
            },
            "remove": {
                "revision": {
                    "&title": {
                        "default": "Revision poiston varmistus"
                    },
                    "draft": {
                        "&text": {
                            "default": "Haluatko varmasti poistaa {target} id:llä {id} luonnoksen {no}?"
                        },
                        "data": {
                            "&SERIES": {
                                "default": "sarjalta"
                            },
                            "&STUDY": {
                                "default": "aineistolta"
                            },
                            "&STUDY_ATTACHMENT": {
                                "default": "aineistoliitteistä"
                            },
                            "&PUBLICATION": {
                                "default": "julkaisulta"
                            }
                        }
                    },
                    "logical": {
                        "&text": {
                            "default": "Haluatko varmasti poistaa {target} id:llä {id}?"
                        },
                        "data": {
                            "&SERIES": {
                                "default": "sarjan"
                            },
                            "&STUDY": {
                                "default": "aineiston"
                            },
                            "&STUDY_ATTACHMENT": {
                                "default": "aineistoliitteen"
                            },
                            "&PUBLICATION": {
                                "default": "julkaisun"
                            }
                        }
                    }
                }
            }
        }
    });
});