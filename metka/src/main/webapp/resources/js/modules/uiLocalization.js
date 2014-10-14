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
            "STUDY": {
                "notes": {
                    "&add": {
                        "default": "Lisää huomautus"
                    },
                    "&modify": {
                        "default": "Muokkaa huomautusta"
                    }
                },
                "dataversions": {
                    "&add": {
                        "default": "Lisää datan versiotieto"
                    },
                    "&modify": {
                        "default": "Muokkaa datan versiotietoa"
                    }
                },
                "descversions": {
                    "&add": {
                        "default": "Lisää kuvailun versiotieto"
                    },
                    "&modify": {
                        "default": "Muokkaa kuvailun versiotietoa"
                    }
                },
                "publications": {
                    "&add": {
                        "default": "Lisää liittyvä julkaisu"
                    },
                    "&modify": {
                        "default": "Muokkaa liittyvää julkaisua"
                    }
                },
                "relatedstudies": {
                    "&add": {
                        "default": "Lisää liittyvä aineisto"
                    },
                    "&modify": {
                        "default": "Muokkaa liittyvää aineistoa"
                    }
                },
                "alttitles": {
                    "&add": {
                        "default": "Lisää rinnakkainen nimi"
                    },
                    "&modify": {
                        "default": "Muokkaa rinnakkaista nimeä"
                    }
                },
                "partitles": {
                    "&add": {
                        "default": "Lisää muunkielinen nimi"
                    },
                    "&modify": {
                        "default": "Muokkaa muunkielistä nimeä"
                    }
                },
                "authors": {
                    "&add": {
                        "default": "Lisää tekijä"
                    },
                    "&modify": {
                        "default": "Muokkaa tekijää"
                    }
                },
                "otherauthors": {
                    "&add": {
                        "default": "Lisää muu tekijä"
                    },
                    "&modify": {
                        "default": "Muokkaa muuta tekijää"
                    }
                },
                "producers": {
                    "&add": {
                        "default": "Lisää tuottaja"
                    },
                    "&modify": {
                        "default": "Muokkaa tuottajaa"
                    }
                },
                "keywords": {
                    "&add": {
                        "default": "Lisää asiasana"
                    },
                    "&modify": {
                        "default": "Muokkaa asiasanaa"
                    }
                },
                "topics": {
                    "&add": {
                        "default": "Lisää tieteenala"
                    },
                    "&modify": {
                        "default": "Muokkaa tieteenalaa"
                    }
                },
                "timeperiods": {
                    "&add": {
                        "default": "Lisää ajallinen kattavuus"
                    },
                    "&modify": {
                        "default": "Muokkaa ajallista kattavuutta"
                    }
                },
                "colltime": {
                    "&add": {
                        "default": "Lisää keräyksen ajankohta"
                    },
                    "&modify": {
                        "default": "Muokkaa keräyksen ajankohtaa"
                    }
                },
                "countries": {
                    "&add": {
                        "default": "Lisää maa"
                    },
                    "&modify": {
                        "default": "Muokkaa maata"
                    }
                },
                "universes": {
                    "&add": {
                        "default": "Lisää perusjoukko"
                    },
                    "&modify": {
                        "default": "Muokkaa perusjoukkoa"
                    }
                },
                "geogcovers": {
                    "&add": {
                        "default": "Lisää kohdealue"
                    },
                    "&modify": {
                        "default": "Muokkaa kohdealuetta"
                    }
                },
                "collectors": {
                    "&add": {
                        "default": "Lisää kerääjä"
                    },
                    "&modify": {
                        "default": "Muokkaa kerääjää"
                    }
                },
                "analysis": {
                    "&add": {
                        "default": "Lisää havainto-/aineistoyksikkö"
                    },
                    "&modify": {
                        "default": "Muokkaa havainto-/aineistoyksikköä"
                    }
                },
                "timemethods": {
                    "&add": {
                        "default": "Lisää aikaulottuvuus"
                    },
                    "&modify": {
                        "default": "Muokkaa aikaulottuvuutta"
                    }
                },
                "datasources": {
                    "&add": {
                        "default": "Lisää lähdeaineisto"
                    },
                    "&modify": {
                        "default": "Muokkaa lähdeaineistoa"
                    }
                },
                "appraisals": {
                    "&add": {
                        "default": "Lisää arviointi"
                    },
                    "&modify": {
                        "default": "Muokkaa arviointia"
                    }
                },
                "collmodes": {
                    "&add": {
                        "default": "Lisää keruumenetelmä/-tekniikka"
                    },
                    "&modify": {
                        "default": "Muokkaa keruumenetelmää/-tekniikkaa"
                    }
                },
                "instruments": {
                    "&add": {
                        "default": "Lisää keruumenetelmä/ohjeistus"
                    },
                    "&modify": {
                        "default": "Muokkaa keruumenetelmää/ohjeistusta"
                    }
                },
                "sampprocs": {
                    "&add": {
                        "default": "Lisää otantamenetelmä/aineiston valintatapa"
                    },
                    "&modify": {
                        "default": "Muokkaa otantamenetelmää/aineiston valintatapaa"
                    }
                },
                "relatedmaterials": {
                    "&add": {
                        "default": "Lisää oheismateriaali"
                    },
                    "&modify": {
                        "default": "Muokkaa oheismateriaalia"
                    }
                },
                "othermaterials": {
                    "&add": {
                        "default": "Lisää muu materiaali"
                    },
                    "&modify": {
                        "default": "Muokkaa muuta materiaalia"
                    }
                },
                "publicationcomments": {
                    "&add": {
                        "default": "Lisää julkaisuun liittyvä huomio"
                    },
                    "&modify": {
                        "default": "Muokkaa julkaisuun liittyvää huomiota"
                    }
                },
                "errors": {
                    "&add": {
                        "default": "Lisää aineistovirhe"
                    },
                    "&modify": {
                        "default": "Muokkaa aineistovirhettä"
                    }
                }
            },

            "STUDY_ATTACHMENT": {
                "filemanagement": {
                    "&add": {
                        "default": "Lisää tiedosto"
                    },
                    "&modify": {
                        "default": "Muokkaa tiedostoa"
                    }
                }
            },

            "STUDY_VARIABLES": {
                "vargroups": {
                    "&add": {
                        "default": "Lisää muuttujaryhmä"
                    },
                    "&modify": {
                        "default": "Muokkaa muuttujaryhmää"
                    }
                },
                "vargrouptexts": {
                    "&add": {
                        "default": "Lisää muuttujaryhmän teksti"
                    },
                    "&modify": {
                        "default": "Muokkaa muuttujaryhmän tekstiä"
                    }
                }
            }
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
                            }
                        }
                    }
                }
            }
        }/*,

        "STUDY": {
            "field": {
                "notes": "Huomautukset",

                // Kuvailu: Asiasana
                "keywordvocab": "Sanasto",
                "keyword": "Asiasana",
                "keywordnovocab": "Asiasana (ei sanastoa)",
                "keywordvocaburi": "Uri",
                "keyworduri": "Tunniste/osoite",

                // Kuvailu: Tieteenala
                "topicvocab": "Sanasto",
                "topic": "Tieteenala",


                "resprate": "Vastausprosentti",
                "datasources": "Lähdeaineistot",
                "datasource": "Lähdeaineisto",

                "weight": "Painokertoimet",
                "weightyesno": "Ei painokertoimia",
                "dataprosessing": "Sisällöllinen muokkaus",
                "collsize": "Tiedostot",
                "complete": "Täydellisyys",
                "disclaimer": "Lisävarauma",
                "datasetnotes": "Huomioitavaa",
                "appraisals": "Arvioinnit"
            }
        },

        "STUDY_ATTACHMENT": {
            "fileaip": {
                "1": "Ei tietoa",
                "2": "Ei",
                "3": "Kyllä",
                "4": "Ei relevantti"
            },
            "filecategory": {
                "1": "Kyselylomake",
                "2": "Kirjoitusohjeet"
            },
            "filepublication": {
                "1": "Ei julkinen",
                "2": "Eng",
                "3": "Fin",
                "4": "Fin/Eng",
                "5": "Fin/Sve",
                "6": "Fin/Eng/Sve",
                "7": "Sve"
            },
            "yes_no_na": {
                "1": "Kyllä",
                "2": "Ei",
                "3": "Ei tietoa"
            },
            "filelanguage": {
                "1": "de",
                "2": "en",
                "3": "es",
                "4": "st",
                "5": "fi",
                "6": "fr",
                "7": "it",
                "8": "lit",
                "9": "ru",
                "10": "sv",
                "11": "xx"
            },
            "field": {
                "fileno": "Tiedostonumero",
                "file": "Tiedoston polku",
                "filelabel": "Virallinen selite",
                "filedescription": "Epävirallinen selite",
                "filecomment": "Kommentti",
                "filecategory": "Tyyppi",
                "fileaip": "PAS",
                "filelanguage": "Kieli",
                "fileoriginal": "Alkuperäinen",
                "filepublication": "WWW",
                "filedip": "Ulosluovutus"
            }
        },

        "PUBLICATION": {
            "field": {
                "firstname": "Etunimi",
                "lastname": "Sukunimi",
                "pid": "Pysyvä tunniste",
                "pidtype": "Tunnisteen tyyppi"
            }
        }*/
    });
});