(function() {
    'use strict';
    /**
     * Add localizations for general UI elements such as buttons
     * as well as all error messages even though most of these will not be needed at one time
     */

    var uiTranslations = {
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
                            "default": "Haluatko varmasti poistaa luonnoksen {0} id:llä {1}?"
                        },
                        "data": {
                            "&series": {
                                "default": "sarjalta"
                            },
                            "&study": {
                                "default": "aineistolta"
                            }
                        }
                    },
                    "logical": {
                        "&text": {
                            "default": "Haluatko varmasti poistaa {0} id:llä {1}?"
                        },
                        "data": {
                            "&series": {
                                "default": "sarjan"
                            },
                            "&study": {
                                "default": "aineiston"
                            }
                        }
                    }
                }
            }
        },

        "EXPERT": {
            "field": {
                id: 'ID',
                name: "Nimi",
                remove: "Poista",
                revision: 'Revisio',
                savedAt: "Pvm.",
                savedBy: "Käyttäjä",
                state: 'Tila',
                title: "Otsikko",
                type: "Tyyppi"
            }
        },

        "SERIES": {
            "field": {
                "id": "ID",
                "seriesabbr": "Lyhenne",
                "seriesname": "Nimi",
                "state": "Tila"
            }
        },

        "STUDY": {
            "field": {
                "notes": "Huomautukset",
                "note": "Huomautus",
                "version": "Versio",
                "versiondate": "Päivämäärä",
                "versionpro": "Käsittelijä",
                "versionlabel": "Lyhyt selite",
                "versiontext": "Julkinen selite",
                "versionnotes": "Ei-julkinen selite",
                "filespath": "Tiedostopolku",
                "fileslang": "Tiedoston kieli",
                "cbattachmentlocation": "Tiedosto",
                "cbattachmenttitle": "Kuvaus",
                "alttitle": "Rinnakkainen nimi",
                "partitle": "Nimi",
                "partitlelang": "Kieli",
                "authortype": "Tyyppi",
                "author": "Tekijä",
                "affiliation": "Taustaorganisaatio",
                "otherauthor": "Muu tekijä",
                "otherauthoraffiliation": "Taustaorganisaatio",
                "producer": "Tuottaja",
                "producerid": "Tunniste",
                "produceridtype": "Tunnistetyyppi",
                "producerrole": "Rooli",
                "projectnr": "Projektinumero",
                "producerabbr": "Lyhenne",

                // Kuvailu: Asiasana
                "keywordvocab": "Sanasto",
                "keyword": "Asiasana",
                "keywordnovocab": "Asiasana (ei sanastoa)",
                "keywordvocaburi": "Uri",
                "keyworduri": "Tunniste/osoite",

                // Kuvailu: Tieteenala
                "topicvocab": "Sanasto",
                "topic": "Tieteenala",

                // Kuvailu: Ajallinen kattavuus
                "timeperiod": "Päivämäärä",
                "timeperiodtext": "Ajallinen kattavuus",
                "timeperiodevent": "Aikajakso",

                // Kuvailu: Maa
                "country": "Maa",
                "countryabbr": "Lyhenne",

                // Kuvailu: Perusjoukko
                "universe": "Perusjoukko",
                "universeclusion": "Rajaus",

                // Kuvailu: Kohdealue
                "geogcover": "Kohdealue",

                // Kuvailu: Ajankohta
                "colldate": "Päivämäärä",
                "colldatetext": "Ajankohta",
                "colldateevent": "Aikajakso",

                // Kuvailu: Kerääjä
                "collector": "Kerääjä",
                "collectoraffiliation": "Taustaorganisaatio",

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
                "appraisals": "Arvioinnit",
                "appraisal": "Arviointi",

                // Kuvailu: Oheismateriaali
                "relatedmaterial": "Oheismateriaalit",

                // Kuvailu: Muu materiaali
                "othermaterialuri": "Uri",
                "othermateriallabel": "Lyhyt kuvaus",
                "othermaterialtext": "Tarkka kuvaus",

                // Kuvailu: Huomiot
                "publicationcomment": "Julkaisuihin liittyvä huomio",

                // Virheet
                "score": "Pisteet",
                "section": "Aineiston osa",
                "subsection": "Osio",
                "language": "Kieli",
                "summary": "Selite",
                "description": "Pitkä selite",
                "triggerDate": "Herätepäivämäärä",
                "triggerTarget": "Herätteen saaja",

                // Mapit
                "binderId": "Mappinumero",
                "binderDescription": "Mapitetun aineiston kuvaus"
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

        STUDY_VARIABLES: {
            field: {
                title: "Aineisto",
                varlabel: "Muuttuja",
                qstnlit: "Kysymysteksti",
                preqtxt: "Esiteksti",
                postqtxt: "Jälkiteksti",
                ivuinstr: "Haastattelijan ohje",
                varnote: "Huomio",
                vartext: "Lisätieto",
                varsecurity: "Tietosuoja-asia",
                statisticstype: "Arvo",
                statisticsvalue: "Selite"
            }
        },

        "PUBLICATION": {
            "field": {
                "firstname": "Etunimi",
                "lastname": "Sukunimi",
                "pid": "Pysyvä tunniste",
                "pidtype": "Tunnisteen tyyppi"
            }
        },

        "BINDER": {
            "field": {
                "studyId": "Aineistonro",
                "studyTitle": "Aineiston nimi",
                "savedBy": "Käsittelijä",
                "binderId": "Mappinro",
                "description": "Mapitettu aineisto"
            }
        }
    };

    function addTranslation(path, root) {
        for(var prop in root) {
            if(root.hasOwnProperty(prop)) {
                var curPath = path;
                if(curPath.length > 0) {
                    curPath += '.';
                }
                if(MetkaJS.isString(root[prop])) {
                    curPath += prop;
                    MetkaJS.L10N.put(curPath, root[prop]);
                } else if(prop.charAt(0) === '&') {
                    curPath += prop.slice(1);
                    MetkaJS.L10N.put(curPath, root[prop]);
                } else {
                    curPath += prop;
                    addTranslation(curPath, root[prop]);
                }
            }
        }
    }

    addTranslation("", uiTranslations);

    /*# Virheviestejä, kannattaa keksiä parempia jossain välissä
    general.errors.title.noImplementation = Ei käsittelijää

    general.errors.search.noResult = Ei hakua vastaavia {0}.
    general.errors.search.noResult.series = sarjoja
    general.errors.search.noResult.study = aineistoja

    general.errors.revision.noRevision = {0} id:llä {1} ei löytynyt revisiota {2}.
    general.errors.revision.noRevision.series = Sarjalle
    general.errors.revision.noRevision.study = Aineistolle

    general.errors.revision.noViewableRevision = {0} id:llä {1} ei löytynyt näytettävää revisiota.
    general.errors.revision.noViewableRevision.series = Sarjalle
    general.errors.revision.noViewableRevision.study = Aineistolle

    general.errors.save.success = Luonnos tallennettu onnistuneesti.
    general.errors.save.fail = Luonnoksen tallentamisessa tapahtui virhe.

    general.errors.approve.success = Luonnos hyväksytty onnistuneesti.
    general.errors.approve.fail.save = Luonnoksen hyväksymisessä tapahtui virhe tallennuksen aikana.
    general.errors.approve.fail.validate = Luonnoksen hyväksymisessä tapahtui virhe datan validoinnin aikana.

    general.errors.move.previous = Olet ensimmäisessä {0}.
    general.errors.move.next = Olet viimeisessä {0}.
    general.errors.move.series = sarjassa
    general.errors.move.study = aineistossa
    general.errors.move.publication = julkaisussa
    #...

    # Remove draft
    general.errors.remove.draft.noObject = ID:llä {1} ei löytynyt {0}.
    general.errors.remove.draft.noObject.series = sarjaa
    general.errors.remove.draft.noObject.study = aineistoa
    general.errors.remove.draft.noObject.publication = julkaisua
    #...

    general.errors.remove.draft.noDraft = {0} ei löytynyt poistettavaa luonnosta.
    general.errors.remove.draft.noDraft.series = Sarjalle
    general.errors.remove.draft.noDraft.study = Aineistolle
    general.errors.remove.draft.noDraft.publication = Julkaisulle
    #...

    general.errors.remove.draft.final = Luonnos poistettu onnistuneesti. Hyväksyttyjä revisioita ei löytynyt. {0} id:llä {1} poistettu.
    general.errors.remove.draft.final.series = Sarja
    general.errors.remove.draft.final.study = Aineisto
    general.errors.remove.draft.final.publication = Julkaisu
    #...

    general.errors.remove.draft.complete = Luonnos poistettu onnistuneesti {0} {1}
    general.errors.remove.draft.complete.series = sarjalta
    general.errors.remove.draft.complete.study = aineistolta
    general.errors.remove.draft.complete.publication = julkaisulta
    #...

    # Remove logical
    general.errors.remove.logical.noObject = ID:llä {1} ei löytynyt {0}.
    general.errors.remove.logical.noObject.series = sarjaa
    general.errors.remove.logical.noObject.study = aineistoa
    general.errors.remove.logical.noObject.publication = julkaisua
    #...

    general.errors.remove.logical.draft = {0} id:llä {1} sisältää avoimen luonnoksen.
    general.errors.remove.logical.draft.series = Sarja
    general.errors.remove.logical.draft.study = Aineisto
    general.errors.remove.logical.draft.publication = Julkaisu
    #...

    general.errors.remove.logical.noApproved = {0} id:llä {1} ei sisällä hyväksyttyjä revisioita.
    general.errors.remove.logical.noApproved.series = Sarja
    general.errors.remove.logical.noApproved.study = Aineisto
    general.errors.remove.logical.noApproved.publication = Julkaisu
    #...

    general.errors.remove.logical.complete = {0} id:llä {1} poistettu onnistuneesti.
    general.errors.remove.logical.complete.series = Sarja
    general.errors.remove.logical.complete.study = Aineisto
    general.errors.remove.logical.complete.publication = Julkaisu
    #...

    general.errors.container.dialog.noImplementation = Taululle [{0}: {1}] ei ole toteutettu käsittelijää.

    general.errors.studyAttachment.saveSuccess = Tiedoston tiedot tallennettu onnistuneesti.
    general.errors.studyAttachment.saveFail = Tiedoston tietojen tallennuksessa tapahtui virhe: {0}
    general.errors.studyAttachment.saveFailAjax = Tiedoston tietojen tallennuspyynnössä tapahtui virhe
    */

})();