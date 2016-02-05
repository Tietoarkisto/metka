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
                },
                "&addGroup": {
                    "default": "Lisää ryhmä"
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
            "waitDialog": {
                "title": "Toimintoa suoritetaan..."
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
        }
    });
});