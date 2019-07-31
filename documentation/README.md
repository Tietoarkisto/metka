# Dokumentaatio sisältö
## Yleiskuva
Dokumentaatiokansio sisältää joukon spesifikaatioita, uml-kaavioita sekä erilaisia sekalaisia muutos- ja release listauksia. Alla on listattu kansion tärkeintä sisältöä selitteiden kanssa. Dokumentaatio on kirjoitettu yleensä englanniksi mutta poikkeuksia saattaa löytyä.

## Sisältö
### changes-kansio
Sisältää listauksia dokumenttirakenteiden muutoksesta. Lähinnä selittäviä tekstejä. Nämä eivät ole varsinaista spesifikaatiota joten sisältö ei ole kaikenkattavaa tai lopullista.
#### release notes -kansio
Sisältää metkan release notes tiedostoja. Tiedostot sisältävät yleisesti listauksia GitHubiin merkityistä issueista joita eri kohdissa kehitystä on saatu korjattua ja testattua.
#### uml-kansio
Sisältää uml-kaavioita dokumenttirakenteista sekä ohjelman toiminnasta.
##### data-kansio
Sisältää spesifikaation uml muodossa varsinaisen revisio-tiedoson sisällöstä. Revisio-tiedosto on tietokantaan tallennetta json-tiedosto joka sisältää yksittäisen revision tietosisällön ja muokkaustiedot (eli mitä on muuttunut
sitten edellisen revision).
##### data_config-kansio
Sisältää spesifikaation uml muodossa revisio-tiedoston määrittelyyn tarkoitetun konfiguraatiotiedoston sisällöstä. Data-configuraatiolla tarkoitetaan tiedostoa joka kertoo minkä nimisiä kenttiä revisio-tiedoston sisältä voi löytyä, minkä tyyppistä dataqa kentissä voi olla, mitä arvoja kenttä saa sisältää ja miten arvot löydetään.
##### gui_config-kansio
Sisältää spesifikaation uml muodossa web-lomakkeiden konfiguraatiosta. Tämä konfiguraatio määrittää lomakkeen jaottelun välilehtiin, sektioihin (suljettava laatikko), sarakkeisiin ja riveihin. Jotta jokin tieto näkyy nettisivulla sen on löydyttävä jossain muodossa GUI-konfiguraatiosta. Myös erityiset toiminnot, kuten asetus-sivu, löytyvät GUI-konfiguraationa mutta vain javascript-koodista.
#### asennusohje-tiedosto
Metkan asennusohje
#### tietokannan_dokumentaatio-tiedosto
Tietokannan taulujen sisällön kuvaus
#### create-database-tables-tiedosto
Tietokannan taulujen luontilauseet
#### jatkokehitysohje-tiedosto
Metkan jatkokehitysohje
##### uml_action_definition-tiedosto
Sisältää alun spesifikaatiolle jolla UI:n dynaamisuutta voisi konfiguroida. Tätä ominaisuutta ei ole toteutettu joten tiedostosto on vain idea tasolla.
##### uml_general-tiedosto
Sisältää yleisiä määrityksiä jotka pätevät kaikkiin dokumentaatiossa oleviin uml-kaavioihin, sekä selittävää tekstiä joidenkin objektien toiminnasta.
##### uml_json_misc_json-tiedosto
Sisältää yleisten json-tiedostojen vaatimukset jotta tiedosto on metkan käytettävissä referenssejä varten.
##### uml_json_transfer-tiedosto
Sisältää spesifikaation uml muodossa yksinkertaistetulle json-formaatille jolla palvelin ja käyttöliittymä kommunikoivat keskenään kun lähetetään revisio-dataa näytettäväksi tai käsiteltäväksi.
##### uml_revision-tiedosto
Sisältää kuvan revisiosta käsitteellisellä tasolla. Ei kovin hyödyllinen varsinaiseen data-spesifikaatioon verrattuna.
##### uml_revisionable-tiedosto
Sisältää kuvan revisioitavasta objektista käsitteellisellä tasolla. Ei kovin hyödyllinen varsinaiseen data-spesifikaatioon verrattuna.
#### api_notes-tiedosto
Sisältää huomioita ulkoisten palveluiden käyttämästä rajapinnasta, sekä tietokannan ja revisiodatan välisistä yhteyksistä.
#### metka_actions_on_revisionable-tiedosto
Sisältää kuvauksen yleisistä toiminnoista mitä revisioitavalle objektille voi tehdä. Parempi kuvaus löytyy Metkan-käyttöohjeesta, joten siihen kannattaa tutustua tähän liittyen.
#### metka_server_operation-tiedosto
Sisältää kuvauksen palvelimen toimintaperiaatteesta ja yleisiä huomioita datan yleisestä liikkeestä palvelimen sisällä.
#### metka_parse_sequence-tiedosto
Kuvaa suurinpiirtein mitä tehdään por-tiedoston parsinnan yhteydessä.
#### metka_varatut_sanat-tiedosto
Lista termeistä jotka Metka määrittelee indeksoinnin yhteydessä. Näitä termejä ei pidä käyttää kenttäavaimina.
#### Reference specification -tiedosto
Sisältää spesifikaation Metkan referenssi-toiminnallisuudesta jolla kenttien tietoja voidaan määrittää riippumaan toisten kenttien tiedoista tai vaikkapa tallennetun json-tiedoston sisällöstä jos tiedosto noudattaa uml_json_misc_json-tiedostossa määriteltyä rakennetta.
#### restrictions_design_pseudo-tiedosto
Sisältää vapaamuotoista ja selittävää spesifikaatiota data-konfiguraation rajoitteet osiosta jolla voidaan määrittää erilaisia sääntöjä joita datan täytyy noudattaa tallentamista, hyväksymistä tai poistamista varten.
