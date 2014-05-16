$(document).ready(function() {
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
            }
        },

        "general": {
            "buttons": {
                "&close": {
                    "default": "Sulje"
                },
                "&ok": {
                    "default": "OK"
                },
                "&yes": {
                    "default": "Kyllä"
                },
                "&no": {
                    "default": "Ei"
                },
                "&search": {
                    "default": "Hae"
                },
                "&addSeries": {
                    "default": "Lisää sarja"
                },
                "&download": {
                    "default": "Lataa"
                },
                "&add": {
                    "default": "Lisää"
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
                    "default": "Hakutuloksia:"
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
                }
            },

            "error": {
                "&title": {
                    "default": "Virhe"
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
                            "default": "Haluatko varmasti poistaa luonnoksen {0} id:llä {1}"
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
                            "default": "Haluatko varmasti poistaa {0} id:llä {1}"
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




    /*general.revision=Revisio

    general.revision.publishDate=Julkaisupvm
    general.revision.revisions=Revisiot
    general.revision.compare=Vertaa
    general.revision.compare.begin=Alku
    general.revision.compare.end=Loppu
    general.revision.replace=Korvaa
    general.revision.compare.title=Revisioiden vertailu (revisio {0} -> revisio {1})
    general.revision.compare.property=Ominaisuus
    general.revision.compare.original=Alkuperäinen
    general.revision.compare.changed=Muutos
    general.revision.compare.modifier=Muuttaja
    general.revision.compare.modifyDate=Muutospvm*/







    // *********************************** //
    // These are translated somewhere else //
    // *********************************** //

    // These should go to gui configuration
    /*# Accordion titles, these follow {type}.accordion.{accordion section key}
    STUDY.accordion.names = Muut nimet
    STUDY.accordion.authors = Tekijät ja tuottajat
    STUDY.accordion.citations = Viittaustiedot
    STUDY.accordion.keywords = Asiasanat ja tieteenalat
    STUDY.accordion.abstract = Abstrakti
    STUDY.accordion.coverage = Kattavuus
    STUDY.accordion.datacollection = Aineistonkeruu
    STUDY.accordion.usage = Aineiston käyttö
    STUDY.accordion.related = Muut materiaalit

    # field properties, these follow {type}.field.{key}
    SERIES.field.seriesno=ID
    SERIES.field.seriesabb=Lyhenne
    SERIES.field.seriesname=Nimi
    SERIES.field.seriesdesc=Kuvaus
    SERIES.field.seriesnotes=Huomiot

    STUDY.field.id = Aineistonumero
    STUDY.field.title = Aineiston nimi
    STUDY.field.entitle = Aineiston nimi (en)
    STUDY.field.seriesid = Sarja
    STUDY.field.submissionid = Hankinta-aineistonumero
    STUDY.field.datakind = Aineiston laatu
    STUDY.field.dataarrivaldate = Datan saantipvm
    STUDY.field.public = Julkaisu
    STUDY.field.anonymization = Anonymisointi
    STUDY.field.descpublic = Aineiston kuvailun julkaisu
    STUDY.field.aipcomplete = Valmis pvm
    STUDY.field.securityissues = Tietosuoja
    STUDY.field.varpublic = Muuttujakuvailun julkaisu
    STUDY.field.originallocation = Alkuperäinen sijainti
    STUDY.field.processingnotes = Huomautuksia prosessiin

    STUDY.field.notes = Huomautukset
    STUDY.field.note = Huomautus

    STUDY.field.dataversions = Datan versiot
    STUDY.field.descversions = Kuvailun versiot
    STUDY.field.version = Versio
    STUDY.field.versiondate = Päivämäärä
    STUDY.field.versionpro = Käsittelijä
    STUDY.field.versionlabel = Lyhyt selite
    STUDY.field.versiontext = Julkinen selite
    STUDY.field.versionnotes = Ei-julkinen selite

    STUDY.field.varname = Nimi
    STUDY.field.varlabel = Selite
    STUDY.field.qstnlit = Kysymysteksti
    STUDY.field.preqtxt = Esiteksti
    STUDY.field.postqtxt = Jälkiteksti
    STUDY.field.ivuinstr = Haastattelijan ohje
    STUDY.field.varnotes = Huomiot
    STUDY.field.vartext = Lisätiedot
    STUDY.field.varsecurity = Tietosuoja-asiat
    STUDY.field.categories = Arvojen selitteet

    STUDY.field.files = Liitetyt tiedostot
    STUDY.field.filespath = Tiedostopolku
    STUDY.field.fileslang = Tiedoston kieli

    STUDY.field.cbattachments = Koodikirjan liitteet
    STUDY.field.cbattachmentlocation = Tiedosto
    STUDY.field.cbattachmenttitle = Kuvaus

    # ARKISTOINTISOPIMUS

    STUDY.field.termsofuse = Ehto 1: käyttöoikeus
    STUDY.field.agreementdate = Arkistointisopimuksen pvm
    STUDY.field.triggerdate = Jos heräte, ilmoituspvm
    STUDY.field.termsofusechangedate = Käyttöehdon muutospvm
    STUDY.field.depositortype = Luovuttajan tyyppi
    STUDY.field.triggerpro = Herätteen saaja
    STUDY.field.newtermsofuse = Käyttöehto muutospvm jälkeen
    STUDY.field.agreementtype = Arkistointisopimuksen tapa
    STUDY.field.triggerlabel = Herätteen selite
    STUDY.field.agreement = Arkistointisopimustiedosto
    STUDY.field.agreementnotes = Lisätiedot koskien arkistointisopimusta
    STUDY.field.permission = Menettely, jos luvanantajaa ei tavoiteta tai hän ei itse voi antaa lupaa
    STUDY.field.specialtermsofuse = Erityisehdot
    STUDY.field.agreementfsdnotes = Muuta kommentoitavaa

    # KUVAILU

    STUDY.field.alttitles = Rinnakkaiset nimet
    STUDY.field.alttitle = Rinnakkainen nimi

    # Kuvailu: Muunkieliset nimet
    STUDY.field.partitle = Nimi
    STUDY.field.partitlelang = Kieli

    # Kuvailu: Tekijä
    STUDY.field.authortype = Tyyppi
    STUDY.field.author = Tekijä
    STUDY.field.affiliation = Taustaorganisaatio

    # Kuvailu: Muu tekijä
    STUDY.field.otherauthor = Muu tekijä
    STUDY.field.otherauthoraffiliation = Taustaorganisaatio

    # Kuvailu: Tuottaja
    STUDY.field.producer = Tuottaja
    STUDY.field.producerid = Tunniste
    STUDY.field.produceridtype = Tunnistetyyppi
    STUDY.field.producerrole = Rooli
    STUDY.field.projectnr = Projektinumero
    STUDY.field.producerabbr = Lyhenne

    STUDY.field.biblcit = Viittaustiedot

    # Kuvailu: Asiasana
    STUDY.field.keywordvocab = Sanasto
    STUDY.field.keyword = Asiasana
    STUDY.field.keywordnovocab = Asiasana (ei sanastoa)
    STUDY.field.keywordvocaburi = Uri
    STUDY.field.keyworduri = Tunniste/osoite

    # Kuvailu: Tieteenala
    STUDY.field.topicvocab = Sanasto
    STUDY.field.topic = Tieteenala

    STUDY.field.abstract = Tiivistelmä

    # Kuvailu: Ajallinen kattavuus
    STUDY.field.timeperiod = Päivämäärä
    STUDY.field.timeperiodtext = Ajallinen kattavuus
    STUDY.field.timeperiodevent = Aikajakso

    # Kuvailu: Maa
    STUDY.field.country = Maa
    STUDY.field.countryabbr = Lyhenne

    # Kuvailu: Perusjoukko
    STUDY.field.universe = Perusjoukko
    STUDY.field.universeclusion = Rajaus

    # Kuvailu: Kohdealue
    STUDY.field.geogcover = Kohdealue

    # Kuvailu: Ajankohta
    STUDY.field.colldate = Päivämäärä
    STUDY.field.colldatetext = Ajankohta
    STUDY.field.colldateevent = Aikajakso

    # Kuvailu: Kerääjä
    STUDY.field.collector = Kerääjä
    STUDY.field.collectoraffiliation = Taustaorganisaatio

    STUDY.field.resprate = Vastausprosentti
    STUDY.field.datasources = Lähdeaineistot
    STUDY.field.datasource = Lähdeaineisto

    STUDY.field.weight = Painokertoimet
    STUDY.field.weightyesno = Ei painokertoimia
    STUDY.field.dataprosessing = Sisällöllinen muokkaus
    STUDY.field.collsize = Tiedostot
    STUDY.field.complete = Täydellisyys
    STUDY.field.disclaimer = Lisävarauma
    STUDY.field.datasetnotes = Huomioitavaa
    STUDY.field.appraisals = Arvioinnit
    STUDY.field.appraisal = Arviointi

    # Kuvailu: Oheismateriaali
    STUDY.field.relatedmaterial = Oheismateriaalit

    # Kuvailu: Muu materiaali
    STUDY.field.othermaterialuri = Uri
    STUDY.field.othermateriallabel = Lyhyt kuvaus
    STUDY.field.othermaterialtext = Tarkka kuvaus

    # Kuvailu: Huomiot
    STUDY.field.publicationcomment = Julkaisuihin liittyvä huomio

    STUDY.field.partitles = Aineiston muunkieliset nimet
    STUDY.field.authors = Aineiston tekijät
    STUDY.field.otherauthors = Aineiston muut tekijät
    STUDY.field.producers = Aineiston tuottaneet tahot
    STUDY.field.keywords = Aineiston sisältöä kuvaavat asiasanat
    STUDY.field.topics = Tieteenalat, jolle aineisto kuuluu
    STUDY.field.timeperiods = Aineiston ajallinen kattavuus
    STUDY.field.countries = Maat, jota koskevaa tietoa aineisto sisältää
    STUDY.field.universes = Aineiston perusjoukot/otos (kvanti) tai aineiston kohdejoukot (kvali)
    STUDY.field.geogcovers = Maantieteelliset alueet, jotka aineisto kattaa
    STUDY.field.colltime = Keräyksen ajankohdat
    STUDY.field.collectors = Aineiston kerääjät
    STUDY.field.analysis = Havaintoyksiköt (kvanti) tai aineistoyksikkötyypit (kvali)
    STUDY.field.timemethods = Tutkimuksen aikaulottuvuudet
    STUDY.field.collmodes = Aineiston keruumenetelmät (kvanti) tai aineistonkeruun tekniikat (kvali)
    STUDY.field.instruments = Keruuvälineet (kvanti) tai aineistonkeruun ohjeistukset (kvali)
    STUDY.field.sampprocs = Otantamenetelmät tai aineiston valintatavat
    STUDY.field.relatedmaterials = Käytön ja kuvailun oheismateriaalit
    STUDY.field.othermaterials = Linkit tietoarkiston ulkopuoliseen materiaaliin
    STUDY.field.publicationcomments = Julkaisuihin liittyviä huomioita

    STUDY_ATTACHMENT.field.fileno = Tiedostonumero
    STUDY_ATTACHMENT.field.file = Tiedoston polku
    STUDY_ATTACHMENT.field.filelabel = Virallinen selite
    STUDY_ATTACHMENT.field.filedescription = Epävirallinen selite
    STUDY_ATTACHMENT.field.filecomment = Kommentti
    STUDY_ATTACHMENT.field.filecategory = Tyyppi
    STUDY_ATTACHMENT.field.fileaip = PAS
    STUDY_ATTACHMENT.field.filelanguage = Kieli
    STUDY_ATTACHMENT.field.fileoriginal = Alkuperäinen
    STUDY_ATTACHMENT.field.filepublication = WWW
    STUDY_ATTACHMENT.field.filedip = Ulosluovutus*/

    // Section translations should go to data configuration
    /*# section properties, these follow {type}.section.{section}
    STUDY.section.basic_information = Perustiedot
    STUDY.section.deposit_agreement = Arkistointisopimus
    STUDY.section.study_description = Kuvailu
    STUDY.section.variables = Muuttujat
    STUDY.section.file_management = Tiedostojen hallinta
    STUDY.section.codebook = Koodikirja
    STUDY.section.study_errors = Virheet
    STUDY.section.identifiers = Tunnisteet
    STUDY.section.import_export = Import/Export*/

    // Selection translations should go to data configuration
    /*# Selection list titles, these follow {type}.{key}.option.{value}
    STUDY.datakind_list.option.1 = Ei tietoa
    STUDY.datakind_list.option.2 = Kvanti
    STUDY.datakind_list.option.3 = Kvali
    STUDY.datakind_list.option.4 = Kvanti&Kvali

    STUDY.anonymization_list.option.1 = Anonymisoidaan FSD:ssä
    STUDY.anonymization_list.option.2 = Anonymisoitu FSD:ssä
    STUDY.anonymization_list.option.3 = Luovuttajan anonymisoima
    STUDY.anonymization_list.option.4 = Ei vaadi anonymisointia
    STUDY.anonymization_list.option.5 = Ei tietoa

    STUDY.yes_no_na.option.1 = Kyllä
    STUDY.yes_no_na.option.2 = Ei
    STUDY.yes_no_na.option.3 = Ei tietoa

    STUDY.dataversionlabel_list.option.1 = Lyhyt selite
    STUDY.dataversionlabel_list.option.2 = Toinen lyhyt selite

    STUDY.statisticstype_list.option.vald = Lukumäärä
    STUDY.statisticstype_list.option.min = Minimi
    STUDY.statisticstype_list.option.max = Maksimi
    STUDY.statisticstype_list.option.mean = Keskiarvo
    STUDY.statisticstype_list.option.stdev = Keskihajonta

    STUDY.termsofuse_list.option.1 = Kaikkien käytettävissä
    STUDY.termsofuse_list.option.2 = Tutkimus, opetus, opiskelu
    STUDY.termsofuse_list.option.3 = Vain tutkimus
    STUDY.termsofuse_list.option.4 = Lupa
    STUDY.termsofuse_list.option.5 = Embargo

    STUDY.depositortype_list.option.1 = Ei tietoa
    STUDY.depositortype_list.option.2 = Yrit./yht.
    STUDY.depositortype_list.option.3 = Tutkija(t)

    STUDY.newtermsofuse_list.option.1 = Kaikkien käytettävissä
    STUDY.newtermsofuse_list.option.2 = Tutkimus, opetus, opiskelu
    STUDY.newtermsofuse_list.option.3 = Vain tutkimus
    STUDY.newtermsofuse_list.option.4 = Lupa

    STUDY.agreementtype_list.option.1 = Ei tietoa
    STUDY.agreementtype_list.option.2 = Ei tarvita sopimusta
    STUDY.agreementtype_list.option.3 = Könttäsopimus
    STUDY.agreementtype_list.option.4 = Norm+puite
    STUDY.agreementtype_list.option.5 = On sopimus

    STUDY.start_end_single.option.1 = Start
    STUDY.start_end_single.option.2 = End
    STUDY.start_end_single.option.3 = Single

    STUDY.universeclusion_list.option.1 = E
    STUDY.universeclusion_list.option.2 = I

    STUDY_ATTACHMENT.fileaip_list.option.1 = Ei tietoa
    STUDY_ATTACHMENT.fileaip_list.option.2 = Ei
    STUDY_ATTACHMENT.fileaip_list.option.3 = Kyllä
    STUDY_ATTACHMENT.fileaip_list.option.4 = Ei relevantti

    STUDY_ATTACHMENT.filecategory_list.option.1 = Kyselylomake
    STUDY_ATTACHMENT.filecategory_list.option.2 = Kirjoitusohjeet

    STUDY_ATTACHMENT.filepublication_list.option.1 = Ei julkinen
    STUDY_ATTACHMENT.filepublication_list.option.2 = Eng
    STUDY_ATTACHMENT.filepublication_list.option.3 = Fin
    STUDY_ATTACHMENT.filepublication_list.option.4 = Fin/Eng
    STUDY_ATTACHMENT.filepublication_list.option.5 = Fin/Sve
    STUDY_ATTACHMENT.filepublication_list.option.6 = Fin/Eng/Sve
    STUDY_ATTACHMENT.filepublication_list.option.7 = Sve

    STUDY_ATTACHMENT.yes_no_na.option.1 = Kyllä
    STUDY_ATTACHMENT.yes_no_na.option.2 = Ei
    STUDY_ATTACHMENT.yes_no_na.option.3 = Ei tietoa

    STUDY_ATTACHMENT.filelanguage_list.option.1 = de
    STUDY_ATTACHMENT.filelanguage_list.option.2 = en
    STUDY_ATTACHMENT.filelanguage_list.option.3 = es
    STUDY_ATTACHMENT.filelanguage_list.option.4 = st
    STUDY_ATTACHMENT.filelanguage_list.option.5 = fi
    STUDY_ATTACHMENT.filelanguage_list.option.6 = fr
    STUDY_ATTACHMENT.filelanguage_list.option.7 = it
    STUDY_ATTACHMENT.filelanguage_list.option.8 = lit
    STUDY_ATTACHMENT.filelanguage_list.option.9 = ru
    STUDY_ATTACHMENT.filelanguage_list.option.10 = sv
    STUDY_ATTACHMENT.filelanguage_list.option.11 = xx*/
});