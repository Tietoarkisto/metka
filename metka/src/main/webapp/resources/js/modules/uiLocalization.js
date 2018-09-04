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
            "STUDY_VARIABLE": {
                "&title": {
                    "default": "Muuttuja"
                },
                "&search": {
                    "default": "Muuttujahaku"
                },
                "&edit": {
                    "default": "Muokkaa muuttujaa"
                }
            },
            "STUDY_ATTACHMENT": {
                "&title": {
                    "default": "Liite"
                },
                "&search": {
                    "default": "Liitehaku"
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
            },
            "BINDER_PAGE": {
                "&title": {
                    "default": "Mapitus"
                }
            },
            "STUDY_ERROR": {
                "&title": {
                    "default": "Aineistovirhe"
                }
            }
        },

        "general": {
            "result": {
                "&amount": {
                    "default": "Rivejä: {length}"
                }
            },
            "&name": {
                "default": "Nimi"
            },

            "downloadInfo": {
                "&currentlyDownloading": {
                    "default": "Lataus on jo käynnissä. Odota latauksen valmistumista ladataksesi uudelleen."
                }
            },

            "buttons": {
                "&add": {
                    "default": "Lisää"
                },
                "&addSeries": {
                    "default": "Lisää sarja"
                },
                "&addStudy": {
                    "default": "Lisää aineisto"
                },
                "&addPublication": {
                    "default": "Lisää julkaisu"
                },
                "&addBinder": {
                    "default": "Lisää aineisto mappiin"
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
                "&revert": {
                    "default": "Palauta"
                },
                "&no": {
                    "default": "Ei"
                },
                "&yes": {
                    "default": "Kyllä"
                },
                "&addGroup": {
                    "default": "Lisää ryhmä"
                }
            },
            "vargroups": {
                "&grouptexts": {
                    "default": "Ryhmän tekstit"
                },
                "&groupname": {
                    "default": "Ryhmän nimi"
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
                    "&current":{
                        "default": "Nykyinen"
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
                },
                "&revert": {
                    "default": "Palauta edellinen revisio"
                }
            },

            "&referenceValue": {
                "default": "Referenssiarvo"
            },
            "&referenceType": {
                "default": "Tyyppi"
            },

            "saveInfo": {
                "&savedAt": {
                    "default": "Päivämäärä"
                },
                "&savedBy": {
                    "default": "Tallentaja"
                }
            },

            "refSaveInfo": {
                "&savedAt": {
                    "default": "Päivämäärä (viittaus)"
                },
                "&savedBy": {
                    "default": "Tallentaja (viittaus)"
                }
            },

            "&refState": {
                "default": "Tila"
            },

            "refApproveInfo": {
                "&approvedAt": {
                    "default": "Hyväksytty (viittaus)"
                },
                "&approvedBy": {
                    "default": "Hyväksyjä (viittaus)"
                },
                "&approvedRevision": {
                    "default": "Revisio (viittaus)"
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
                },
                "countries": {
                    "&addFinland": {
                        "default": "Lisää Suomi"
                    }
                }
            },

            "&id": {
                "default": "ID"
            },

            "&revision": {
                "default": "Revisio"
            },

            "&handler": {
                "default": "Käsittelijä"
            },
            "&noHandler": {
                "default": "Ei käsittelijää"
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
                "&plural": {
                    "default": "Hakutulokset"
                },
                "&studysearch":{
                    "default": "Aineistohaun tulokset"
                },
                "&publicationsearch": {
                    "default": "Julkaisuhaun tulokset"
                },
                "&seriessearch": {
                    "default": "Sarjahaun tulokset"
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
            },
            "coltitle": {
                "&title": {
                    "default": "Otsikko"
                },
                "&state": {
                    "default": "Tila"
                },
                "&name": {
                    "default": "Nimi"
                },
                "&description": {
                    "default": "Kuvaus"
                },
                "&revisionnumber": {
                    "default": "Revisio"
                },
                "&type": {
                    "default": "Tyyppi"
                },
                "&studyid": {
                    "default": "Aineistonumero"
                },
                "&studyname": {
                    "default": "Aineiston nimi"
                },
                "&studytype": {
                    "default": "Laatu"
                },
                "&studyright": {
                    "default": "Käyttöoikeus"
                },
                "&studyerrorsscore": {
                    "default": "Virhepisteet"
                },
                "&language": {
                    "default": "Kieli"
                },
                "&publicationnumber": {
                    "default": "Julkaisun numero"
                },
                "&seriesresultsabbr": {
                    "default": "Lyhenne"
                },
                "&seriesresultsname": {
                    "default": "Nimi"
                },
                "&seriesresultsstate": {
                    "default": "Tila"
                },
                "&binderid": {
                    "default": "Mappinumero"
                },
                "&binderdescription": {
                    "default": "Mapitettu aineisto"
                },
                "&authors": {
                    "default": "Tekijät"
                },
                "&series": {
                    "default": "Sarja"
                },
                "&datakind": {
                    "default": "datakind"
                },
                "&termsofuse": {
                    "default": "Käyttöoikeus"
                }
            },
            "saved": {
                "&saved": {
                    "default": "Tallennetut haut"
                },
                "&save": {
                    "default": "Tallenna haku"
                }

            },
            "&expression": {
                "default": "Hakulause"
            },
            "study": {
                "&studysearch": {
                    "default": "Aineistohaku"
                },
                "&errorsearch": {
                    "default": "Virheelliset"
                },
                "&errors": {
                    "default": "Virheet"
                },
                "&studyid": {
                    "default": "Aineiston numero"
                },
                "&submissionid": {
                    "default": "Hankinta-aineistonumero"
                },
                "&title": {
                    "default": "Aineiston nimi"
                },
                "&author": {
                    "default": "Tekijän nimi"
                },
                "&authororganization": {
                    "default": "Tekijän organisaatio"
                },
                "&producername": {
                    "default": "Tuottajan nimi"
                },
                "&producerrole": {
                    "default": "Tuottajan rooli"
                },
                "&series": {
                    "default": "Sarjan nimi"
                },
                "&datakind": {
                    "default": "Aineiston laatu"
                },
                "&anonymization": {
                    "default": "Anonymisointi"
                },
                "&securityissues": {
                    "default": "Tietosuoja"
                },
                "&publication": {
                    "default": "Julkaisu"
                },
                "&aipcomplete": {
                    "default": "Valmis-päivämäärä"
                },
                "&termsofuse": {
                    "default": "Ehto 1: käyttöoikeus"
                },
                "&newtermsofuse": {
                    "default": "Käyttöehto muutospvm jälkeen"
                },
                "&termsofusechangedate": {
                    "default": "Käyttöehdon muutospvm"
                },
                "&agreementtype": {
                    "default": "Arkistointisopimuksen tapa"
                },
                "&depositortype": {
                    "default": "Luovuttajan tyyppi"
                },
                "&handler": {
                    "default": "Käsittelijä"
                },
                "&packageurn": {
                    "default": "URN-tunniste"
                },
                "&abstract": {
                    "default": "Abstrakti"
                },
                "&topictop": {
                    "default": "Pääala"
                },
                "&topic": {
                    "default": "Tieteenala"
                },
                "&timeperiod": {
                    "default": "Ajallinen kattavuus"
                },
                "&colltime": {
                    "default": "Aineistonkeruun ajankohta"
                },
                "&country": {
                    "default": "Maa"
                },
                "&collector": {
                    "default": "Aineiston kerääjän nimi"
                },
                "&analysisunit": {
                    "default": "Havainto/aineistoyksikkö"
                },
                "&timemethod": {
                    "default": "Aikaulottuvuus"
                },
                "&sampproc": {
                    "default": "Otantamenetelmä"
                },
                "&collmode": {
                    "default": "Keruumenetelmä"
                },
                "&errorscore": {
                    "default": "Pisteet"
                }
            },
            "studyvariables": {
                "&variablessearch":{
                    "default": "Muuttujajoukot"
                },
                "&variablesearch":{
                    "default": "Muuttujat"
                },
                "&variablestudyid":{
                    "default": "Aineistonumero"
                },
                "&variablelabel":{
                    "default": "Muuttujan selite"
                },
                "&variableqstnlit":{
                    "default": "Kysymysteksti"
                },
                "&variablevaluelabel":{
                    "default": "Arvon selite"
                },
                "&variablelanguage":{
                    "default": "Muuttujan kieli"
                },
                "&variablesstudyid":{
                    "default": "Aineistonumero"
                },
                "&variableslanguage":{
                    "default": "Muuttujajoukon kieli"
                }
            },
            "publication": {
                "&publicationid": {
                    "default": "Julkaisu id-nro"
                },
                "&studies": {
                    "default": "/ Aineiston numerot"
                },
                "&publicationfirstsaved": {
                    "default": "Julkaisun lisäyspvm"
                },
                "&savedAt": {
                    "default": "Viimeisin muutospvm"
                },
                "&publicationyear": {
                    "default": "Julkaisuvuosi"
                },
                "&studyname": {
                    "default": "Aineiston nimi"
                },
                "&seriesname": {
                    "default": "Sarjan nimi"
                },
                "&lastname": {
                    "default": "Tekijän sukunimi"
                },
                "&firstname": {
                    "default": "Etunimi"
                },
                "&publicationtitle": {
                    "default": "Julkaisun otsikko"
                },
                "&publicationrelpubl": {
                    "default": "relPubl"
                },
                "&publicationlanguage": {
                    "default": "Julkaisun kieli"
                },
                "&publicationpublic": {
                    "default": "Voiko julkaista"
                },
                "&savedBy": {
                    "default": "Käsitteljä"
                }
            },
            "series": {
                "&seriesid": {
                    "default": "ID"
                },
                "&seriesabbr": {
                    "default": "Lyhenne"
                },
                "&seriesname": {
                    "default": "Nimi"
                }
            },
            "binder": {
                "&title": {
                    "default": "Mapitukset"
                },
                "&studyid": {
                    "default": "Aineistonro"
                },
                "&binderid": {
                    "default": "Mappinro"
                },
                "&studytitle": {
                    "default": "Aineiston nimi"
                },
                "&binderdescription": {
                    "default": "Mapitettu aineisto"
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
            },
            "reports": {
                "&title": {
                    "default": "Raportit"
                },
                "&download": {
                    "default": "Lataa raportti"
                }
            },
            "indexing": {
                "&title": {
                    "default": "Indeksointi"
                },
                "&commandsqueue": {
                    "default": "Indeksikomentoja jonossa: "
                },
                "&notindexed": {
                    "default": "Indeksoimattomia revisioita: "
                },
                "&indexeverything": {
                    "default": "Uudelleenindeksoi kaikki"
                },
                "&closeindexers": {
                    "default": "Sammuta indekserit"
                },
                "&refresh": {
                    "default": "Päivitä"
                }
            },
            "configuration": {
                "&title": {
                    "default": "Konfiguraatiotiedostot"
                },
                "&dataconf": {
                    "default": "Data konfiguraatiot"
                },
                "&edit": {
                    "default": "Muokkaa"
                },
                "&configuration": {
                    "default": "Konfiguraatio"
                },
                "&keys": {
                    "default": "Avaimet"
                },
                "&guiconf": {
                    "default": "GUI konfiguraatiot"
                },
                "&json": {
                    "default": "JSON"
                },
                "editor": {
                    "&title": {
                        "default": "Konfiguraatio"
                    },
                    "general": {
                        "&title": {
                            "default": "Perustiedot"
                        },
                        "&key": {
                            "default": "Avain"
                        },
                        "&type": {
                            "default": "Tyyppi"
                        },
                        "&version": {
                            "default": "Versio"
                        },
                        "&idfield": {
                            "default": "ID-kenttä"
                        }
                    },
                    "selectionlists": {
                        "&title": {
                            "default": "Valintalistat"
                        },
                        "&key": {
                            "default": "Avain"
                        },
                        "&type": {
                            "default": "Tyyppi"
                        },
                        "&default": {
                            "default": "Vakioarvo"
                        },
                        "&includeempty": {
                            "default": "Lisää tyhjä valinta"
                        },
                        "&freetextvalues": {
                            "default": "Vapaatekstivalinnat"
                        },
                        "&freetext": {
                            "default": "Vapaatekstiarvo"
                        },
                        "&freetextkey": {
                            "default": "Vapaatekstikenttä"
                        },
                        "&sublistkey": {
                            "default": "Alilistan avain"
                        },
                        "&reference": {
                            "default": "Viittaus"
                        },
                        "&options": {
                            "default": "Arvot"
                        },
                        "&optionvalue": {
                            "default": "Arvo"
                        },
                        "&optiontitledefault": {
                            "default": "Teksti, Suomi"
                        },
                        "&optiontitleen": {
                            "default": "Teksti, Englanti"
                        },
                        "&optiontitlesv": {
                            "default": "Teksti, Ruotsi"
                        }
                    },
                    "reference": {
                        "&title": {
                            "default": "Viittaukset"
                        },
                        "&key": {
                            "default": "Viittausavain"
                        },
                        "&type": {
                            "default": "Viittaustyyppi"
                        },
                        "&target": {
                            "default": "Viittauksen kohde"
                        },
                        "&valuepath": {
                            "default": "Polku arvoon"
                        },
                        "&titlepath": {
                            "default": "Polku tekstiin"
                        },
                        "&approvedonly": {
                            "default": "Vain hyväksytyt"
                        },
                        "&ignoreremoved": {
                            "default": "Ohita poistetut"
                        }
                    },
                    "field": {
                        "&title": {
                            "default": "Kentät"
                        },
                        "&key": {
                            "default": "Kenttäavain"
                        },
                        "&type": {
                            "default": "Kenttätyyppi"
                        },
                        "&translatable": {
                            "default": "Käännettävyys"
                        },
                        "&immutable": {
                            "default": "Muuttumaton"
                        },
                        "&selectionlist": {
                            "default": "Valintalista"
                        },
                        "&subfields": {
                            "default": "Alikentät"
                        },
                        "&subfieldkey": {
                            "default": "Alikentän avain"
                        },
                        "&subfield": {
                            "default": "On alikenttä"
                        },
                        "&ref": {
                            "default": "Viitteen avain"
                        },
                        "&editable": {
                            "default": "Muokattavissa"
                        },
                        "&writable": {
                            "default": "Tallennettavissa"
                        },
                        "&indexed": {
                            "default": "Indeksoidaan"
                        },
                        "&generalsearch": {
                            "default": "Yleiseen hakuun"
                        },
                        "&exact": {
                            "default": "Tarkka haku"
                        },
                        "&bidirectional": {
                            "default": "Kaksisuuntainen"
                        },
                        "&indexname": {
                            "default": "Indeksinimi"
                        },
                        "&fixedorder": {
                            "default": "Muuttumaton järjestys"
                        },
                        "&removepermissions": {
                            "default": "Poisto-oikeudet"
                        },
                        "&permission": {
                            "default": "Oikeus"
                        }
                    },
                    "restrictions": {
                        "&title": {
                            "default": "Rajoitteet"
                        },
                        "&namedrestrictions": {
                            "default": "Nimetyt rajoitteet"
                        },
                        "&operations": {
                            "default": "Operaatiot"
                        },
                        "&operationtype": {
                            "default": "Operaation tyyppi"
                        },
                        "&namedtargetname": {
                            "default": "Nimetyn kohteen nimi"
                        },
                        "&targettype": {
                            "default": "Kohteen tyyppi"
                        },
                        "&targetcontent": {
                            "default": "Kohteen sisältö"
                        },
                        "&targets": {
                            "default": "Kohdelista"
                        },
                        "&checks": {
                            "default": "Kohteen tarkistukset"
                        },
                        "&checkconditiontype": {
                            "default": "Vaatimuksen tyyppi"
                        },
                        "&checkconditiontargettype": {
                            "default": "Vaatimuksen kohteen tyyppi"
                        },
                        "&checkconditiontargetcontent": {
                            "default": "Vaatimuksen kohteen sisältö"
                        },
                        "&restrictiontargets": {
                            "default": "Vaatimusta rajoittavat kohteet"
                        }
                    },
                    "&cascade": {
                        "default": "Sarjoitukset"
                    }
                }
            },
            "api": {
                "&title": {
                    "default": "API"
                },
                "&adduser": {
                    "default": "Lisää käyttäjä"
                },
                "&name": {
                    "default": "Nimi"
                },
                "&role": {
                    "default": "Rooli"
                },
                "&addbutton": {
                    "default": "Lisää käyttäjä"
                },
                "&users": {
                    "default": "Käyttäjät"
                },
                "&user": {
                    "default": "Käyttäjä"
                },
                "&lastlogin": {
                    "default": "Viimeksi kirjautunut"
                },
                "&createdby": {
                    "default": "Luoja"
                }
            }
        },

        "dialog": {
            "waitDialog": {
                "title": "Toimintoa suoritetaan..."
            },
            "history": {
                "&path": {
                    "default":"Polku"
                },
                "&language": {
                    "default":"Kieli"
                },
                "&original": {
                    "default":"Alkuperäinen arvo"
                },
                "&current": {
                    "default":"Nykyinen arvo"
                },
                "compare": {
                    "&rowId": {
                        "default": "rivi id"
                    },
                    "&newRow": {
                        "default": "Uusi rivi"
                    },
                    "&removedRow": {
                        "default": "Rivi poistettu"
                    }
                }
            }
        },
        "other": {
            "&organizationsubtitle": {
                "default": "Yksikkö ↳ Osastot"
            },
            "&agencysubtitle": {
                "default": "Organisaatio ↳ Yksiköt ↳ Osastot"
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
                            "&STUDY_VARIABLES": {
                                "default": "aineistomuuttujilta"
                            },
                            "&STUDY_VARIABLE": {
                                "default": "muuttujalta"
                            },
                            "&STUDY_ATTACHMENT": {
                                "default": "aineistoliitteistä"
                            },
                            "&PUBLICATION": {
                                "default": "julkaisulta"
                            },
                            "&BINDER_PAGE": {
                                "default": "mapitukselta"
                            },
                            "&STUDY_ERROR": {
                                "default": "aineistovirheeltä"
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
                            },
                            "&BINDER_PAGE": {
                                "default": "mapituksen"
                            }
                        }
                    }
                }
            }
        },
        "returnResults": {
            "configuration": {
                "CONFIG_UPDATE_PARTIAL_FAILURE": "Joitain revisioita ei voitu päivittää. Päivittämättömät revisiot: "
            }
        }
    });
});