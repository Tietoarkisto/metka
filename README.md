Metka
=====

Documentation-kansiosta löytyy suuri määrä spesifikaatiota Metkan eri osista. Kansiosta löytyy myös README.md tiedosto joka kuvaa lyhyesti eri osia kansion sisällöstä.
Tässä dokumentissa kuvataan tarkemmin muuta osaa Metkan sisällöstä ja toiminnasta.

---

# Moduulit
Itse pääprojekti on nimeltää metka, lisäksi mukana on 2 kirjastoa omina Maven moduuleinaan.


----------


## [codebook](./../../tree/master/codebook)

codebook-paketti sisältää Java-luokat DDI 2.5.1-standardille (http://www.ddialliance.org/Specification/DDI-Codebook/2.5/).


----------


## [spssio](./../../tree/master/spssio)

spssio-paketti joka sisältää por-tiedostojen käsittelyyn tarkoitetun javakirjaston.


----------



## [metka](./../../tree/master/metka)

Itse pääsovellus.
 


----------


# Metka-moduulin pakettijako

Tässä projektissa tuotettu Java-koodi löytyy kaikki fi.uta.fsd-paketin alta.


----------


> # [metkaAmqp/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaAmqp)
> 
> Paketti sisältää AMQP viestien (erityisesti Rabbit-viestien)
> lähettämiseen tarkoitetut koodit.


----------


> # [metkaAuthentication/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaAuthentication)
> 
> Paketti sisältää käyttäjien autentikointiin ja oikeuksienhallintaan
> liittyvät koodit, kuten eri oikeuksien ja roolien määrittelyt.


----------

> # [metkaExternal/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaExternal)
> 
> Paketti sisältää ulkoisiin rajapintoihin (muuten kuin nettisivun
> kautta käytettäväksi tarkoitettujen toimintojen) määrittelyyn ja
> toteutukseen liittyvät koodit.


----------


> # [metkaSearch/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch)
> 
> Paketti sisältää lucene-indeksointiin ja hakuihin, sekä
> Voikko-analysaattoriin liittyvät koodit
> 
> ### [metkaSearch/analyzer/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/analyzer)
> 
> Sisältää Metkan tarvitsemia kustomoituja tekstianalysaattoreita.
> 
> ### [metkaSearch/commands/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/commands)
> 
> Sisältää lucenen käyttöön liittyviä komentoja jolla lucenea ohjaillaan
> muun koodin puolesta.
> 
> ### [metkaSearch/directory/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/directory)
> 
> Sisältää indeksitiedostojen hallintaan tarkoitettuja apuluokkia
> 
> ### [metkaSearch/entity/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/entity)
> 
> Sisältää indeksointiin liittyviä JPA-entiteettejä sekä näihin
> liittyvän repositoryn
> 
> ### [metkaSearch/enums/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/enums)
> 
> Sisältää indeksointiin liittyviä enumeraattoreita
> 
> ### [metkaSearch/filters/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/filters)
> 
> Sisältää Metkan tarvitsemia kustomoituja tekstifilttereitä
> 
> ### [metkaSearch/handlers/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/handlers)
> 
> Sisältää Metkan komentojen (yleensä indeksointikomentojen) käyttöön
> liittyviä datan käsittelijöitä
> 
> ### [metkaSearch/indexers/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/indexers)
> 
> Sisältää erityyppisen datan indeksointiin liittyviä luokkia
> 
> ### [metkaSearch/iterator/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/iterator)
> 
> Sisältää Metkan tarvitsemia kustomoituja iteraattoreita jotka
> liittyvät erityisesti indeksointiin ja hakuun.
> 
> ### [metkaSearch/results/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/results)
> 
> Sisältää hakukomentojen paluuarvoja
> 
> ### [metkaSearch/searchers/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/searchers)
> 
> Sisältää erityyppisen datan hakuun liittyviä luokkia.
> 
> #### [metkaSearch/searchers/Searcher.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/searchers/Searcher.java)
> 
> Sisältää datan hakemiseen liittyviä komentoja
> 
> ### [metkaSearch/voikko/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaSearch/voikko)
> 
> Sisältää Voikko-kirjaston käyttöön liittyviä luokkia


----------

> # [metka/](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka)
> Sisältää pääosan Metkan palvelinkoodista. Täältä löytyy mm.
> varsinaisen lomakedatan ja nettisivun pyörittämiseen liittyvät koodit.
> 
> 
> ----------
> 
> 
> > ## [metka/adapters](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/adapters)
> > 
> > Sisältää erilaisia adaptereita joita tarvitaan datan muunnoksissa
> > formaatista toiseen.
> 
> 
> ----------
> 
> > ## [metka/aop](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/aop)
> > 
> > Metkan käyttämät [AOP](https://en.wikipedia.org/wiki/Aspect-oriented_programming)
> luokat.
> 
> ----------
> 
> > ## [metka/automation](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/automation)
> > 
> > Sisältää automaattisesti käynnistyvää toiminnallisuutta, kuten konfiguraatioiden luku levyltä kun palvelin käynnistetään.
> 
> 
> ----------
> 
> 
> > ## [metka/ddi](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/ddi)
> > 
> > Sisältää DDI-tiedostojen käsittelyyn liittyvää koodia.
> > 
> > ### [metka/ddi/builder](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/ddi/builder)
> > 
> > Sisältää DDI-tiedoston export-toimintoon liittyvät koodit
> > 
> > ### [metka/ddi/reader](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/ddi/reader)
> > 
> > Sisältää DDI-tiedoston import-toimintoon liittyvät koodit
> 
> 
> ----------
> 
> 
> > ## [metka/enums](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/enums)
> > 
> > Sisältää Metkan yleisesti käyttämiä enumeraattoreita
> 
> 
> ----------
> 
> 
> > ## [metka/model](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model)
> > 
> > Sisältää json-tiedostojen käyttöön liittyvät koodit, kuten datan
> > käsittely, luokkamallit json-tiedostoille, sarjoittamiseen ja
> > lukemiseen liittyvät erikoistoiminnot jne.
> > 
> > ### [metka/model/access](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/access/)
> > 
> > Data-json tietojen käsittelyyn liittyvät apuluokat. Sisältää datan
> > lukemisen, muokkauksen ja tarkistuksen ja näihin liittyvät komennot.
> > Nämä komennot käsittelevät vain java-luokkia joten json-tieto on ensin
> > luettava objekteiksi.
> > 
> > ### [metka/model/general](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/general/)
> > ### [metka/model/configuration](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/configuration/)
> > ### [metka/model/data](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/data/)
> > ### [metka/model/guiconfiguration](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/guiconfiguration/)
> > ### [metka/model/transfer](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/transfer/)
> > 
> > general-, configuration-, data-, guiconfiguration- ja transfer
> > -paketit sisältävät Java-luokka pohjaisen esitystavan kyseisistä json-tiedostoista. Näissä objekteissa määritellään mitä propertyjä
> > json-objekteilla voi olla ja mitkä ovat näiden vakioarvot, jos
> > vakioarvoja on. Objektit käyttävät tilannekohtaisesti polymorfismia,
> > mutta kaikkia tilanteita joissa polymorfismia voisi käyttää ei ole
> > toteutettu näin. Json sarjoitetaan ja luetaan käyttäen Jackson-json
> > kirjaston toimintoja joten eri annotaatiot löytyvät kyseisen kirjaston
> > dokumentaatiosta.
> > 
> > ### [metka/model/deserializers](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/deserializers/)
> > 
> > Erilaisten json-objektien lukijoita.
> > 
> > ### [metka/model/factories](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/factories/)
> > 
> > Json-objektien ja rakenteiden (pääasiassa datan) käsittelyyn liittyviä
> > luokkia. Nämä luokat mm. muodostavat pohjadatan kun tehdään uusia
> > revisioitavia objekteja ja varmistavat että kaikki tarvittava tieto
> > löytyy datasta. Lisäksi täältä löytyy mm. aineiston biblcit ja
> > packages tietojen muodostaminen.
> > 
> > ### [metka/model/interfaces](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/interfaces/)
> > 
> > Erilaiset Json-tietoihin liittyvät interface-objektit joita käytetään
> > java-luokissa.
> > 
> > ### [metka/model/serializers](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/model/serializers/)
> > 
> > Erilaisten json-objektien sarjoittajia.
> 
> 
> ----------
> 
> 
> > ## [metka/mvc](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc)
> > 
> > Käyttöliittymärajapintaan liittyvät luokat löytyvät täältä.
> > 
> > ### [metka/mvc/controller](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller)
> > 
> > Eri html-pyyntöjen määrittelyt
> > 
> > ### [metka/mvc/services](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/services)
> > 
> > Palvelut jotka käsittelevät html-pyyntöjä, keskustelevat
> > tietovarastorajapintojen kanssa ja muokkaavat dataa käyttöliittymän
> > ymmärtämään muotoon.
> > 
> > #### [metka/mvc/MetkaObjectMapper.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/MetkaObjectMapper.java)
> > 
> > Määrittelee miten java-objekteja tulkitaan json-tiedostoiksi. Tämä
> > koskee kaikkea objecti-json-objekti muunnosta, ei pelkästään
> > käyttöliittymälle menevää muunnosta. Luokka on kuitenkin määritelty
> > täällä koska suurin osa objekti-json-objekti muunnoksista tapahtuu
> > käyttöliittymän ja palvelimen välisessä liikenteessä.
> > 
> > #### [metka/mvc/ModelUtil.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/ModelUtil.java)
> > 
> > Määrittelee html-navigointipyyntöihin liittyviä vakioarvoja kuten
> > käyttäjänimen ja sivuston osan johon käyttäjä ohjataan. Kaikkien
> > html-pyyntöjen jotka ohjaavat käyttäjän jsp-sivulle tulisi käyttää
> > tätä luokkaa näiden vakioarvojen asettamiseen.
> 
> 
> ----------
> 
> 
> > ## [metka/names](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/names)
> > 
> > Määrittelee erilaisia vakiotekstikokoelmia jotka sisältävät mm. Metkan
> > käyttämiä kenttäavaimia. Eivät sinällään liity ohjelman toimintaan
> > vaan yrittävät helpottaa kirjoitusvirheiden välttämistä.
> 
> 
> ----------
> 
> 
> > ## [metka/search](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/search)
> > 
> > Erilaisten hakutoimintojen toteutukset. Haut voivat sisältää
> > hakupyyntöjä lucene-indeksiin, hakuja tietokannasta ym.
> 
> 
> ----------
> 
> 
> > ## [metka/storage](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage)
> > 
> > Tiedon säilytykseen liittyvät luokat.
> > 
> > ### [metka/storage/cascade](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/cascade)
> > 
> > Sisältää toteutuksen toiminnallisuudesta joka automatisoi lomakkeelle
> > suoritettavan toiminnan suorittamisen määritellyille alilomakkeille.
> > 
> > ### [metka/storage/collecting](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/collecting)
> > 
> > Sisältää toteutuksen erilaisten referenssien käsittelylle ja
> > avaamiselle arvojoukoiksi.
> > 
> > ### [metka/storage/entity](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/entity)
> > 
> > Metkan käyttämät JPA-entiteetit
> > 
> > ### [metka/storage/repository](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/repository)
> > 
> > Metkan käyttämät Repository-objektit. Repository on käytännössä
> > rajapinta tietovarastoon ja hoitaa haut ja tallennukset tähän
> > varastoon. Tietovarasto voi olla tietokanna taulu, lucene-indeksi ym.
> > Sisältää myös erityisesti repositoryjen käyttöön tarkoitettuja
> > enumeraattoreita.
> > 
> > ### [metka/storage/response](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/response)
> > 
> > Sisältää repositoryjen käyttämiä paluuarvo-objekteja joihin koostetaan
> > dataa yksinkertaisempaan muotoon.
> > 
> > ### [metka/storage/restrictions](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/restrictions)
> > 
> > Sisältää rajoitekonfiguraation käsittelyyn ja validointiin liittyvät
> > luokat.
> > 
> > ### [metka/storage/util](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/util)
> > Sisältää yleisiä apuluokkia tallennusdatan käsittelyyn.
> > 
> > ### [metka/storage/variables](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/storage/variables)
> > Sisältää toteutuksen por-tiedostojen parsinnasta aineistomuuttujiksi.
> 
> 
> ----------
> 
> > ## [metka/transfer](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/transfer)
> > 
> > Sisältää erinäisiä käyttöliittymän ja palvelimen väliseen
> > kommunikointiin liittyviä request- response- ynnä muita objekteja.
> > Nämä vastaavat json-rakenteita joita käyttöliittymä lähettää tai ottaa
> > vastaan.


----------


# URL-osoitteiden reititys

Kaikki web-osoitteet joihin Metka vastaa löytyvät luokista joiden nimi päättyy termiin *Controller*. HTTP-metodien käyttö on jaettu kahteen osaan. Jos metodi ei ota vastaan parametreja tai ainoat parametrit löytyvät osana URIa niin käytetään GET-metodia, muutoin käytetään POST-metodia.


----------


## api
Kaikki polut jotka alkavat `api/` saapuvat tähän osioon. Nämä polut eivät käytä Shibboleth-autentikaatiota vaan autentikaatio joko pakotetaan tiettyyn käyttäjään tai suoritetaan jollain mulla autentikaatiotavalla (yleensä käyttäen api-avain autentikaatiota).


----------

> **Sijainti: [APIController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metkaExternal/APIController.java)**
> 
> `[/exportDDI],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn aineiston DDI 2.5 xml -formaatissa.
> 
> `[/importDDI],methods=[POST]`  
> *Käyttö:* Yrittää tuoda tietoja annetusta DDI 2.5 xml -tiedostosta määriteltyyn aineistoon.
> 
> `[/claimRevision],methods=[POST]`  
> *Käyttö:* Ottaa haltuun pyydetyn revision muokkausta varten.
> 
> `[/releaseRevision],methods=[POST]`  
> *Käyttö:* Vapauttaa pyydetyn revision muokkauksesta.
> 
> `[/exportRevision],methods=[POST]`  
> *Käyttö:* Lataa pyydetyn revision JSON-objectina.
> 
> `[/collectReferenceOptions],methods=[POST]`  
> *Käyttö:* Palauttaa avain-arvo parit annetulle viittaukselle.
> 
> `[/saveRevision],methods=[POST]`  
> *Käyttö:* Tallentaa pyydetyn revision.
> 
> `[/indexRevisions],methods=[POST]`  
> *Käyttö:* Indeksoi annetut revisiot.
> 
> `[/performQuery],methods=[POST]`  
> *Käyttö:* Suorittaa annetun haun.
> 
> `[/approveRevision],methods=[POST]`  
> *Käyttö:* Hyväksyy pyydetyn revision.
> 
> `[/removeRevision],methods=[POST]`  
> *Käyttö:* Poistaa pyydetyn revision.
> 
> `[/viewRevision],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn revision yksinkertaistetussa muodossa.
> 
> `[/editRevision],methods=[POST]`  
> *Käyttö:* Palauttaa luonnoksen tai jos ei ole niin tekee uuden pyydetylle revisionablelle.
> 
> `[/restoreRevision],methods=[POST]`  
> *Käyttö:* Palauttaa poistetun revision,
> 
> `[/createRevision],methods=[POST]`  
> *Käyttö:* Luo uuden revision pyydetystä revisionablesta.
> 
> `[/getConfiguration],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn datakonfiguraation.


----------


## web
Kaikki polut jotka alkavat `web/` saapuvat tähän osioon. Nämä polut käyttävät Shibboleth-autentikaatiota joten käyttäjän täytyy olla kirjautuneena jotta pyynnön käsittely voidaan suorittaa onnistuneesti.


----------


> **Sijainti: [StudyController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/StudyController.java)**
> 
> `[/study/attachmentHistory],methods=[POST]`  
> *Käyttö:* Palauttaa aineistoliitteen revisiohistorian. Sisältää ylimääräistä tietoa mitä normaali revisiohistoriapyyntö ei sisällä.
> 
> `[/study/getOrganizations],methods=[GET]`  
> *Käyttö:* Palauttaa organisaatiolistan tietokannasta. 
> 
> `[/study/uploadOrganizations],methods=[POST]`  
> *Käyttö:* Päivittää organisaatiolistan tietokantaan. 
> 
> `[/study/ddi/export],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn aineiston DDI 2.5 xml -formaatissa.
> 
> `[/study/ddi/import],methods=[POST]`  
> *Käyttö:* Yrittää tuoda tietoja annetusta DDI 2.5 xml -tiedostosta määriteltyyn aineistoon.


----------
> **Sijainti: [ReferenceController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/ReferenceController.java)**
> 
> `[/references/collectOptionsGroup],methods=[POST]`  
> *Käyttö:* Parsii joukon kenttäavain pohjaisia referenssipyyntöjä (annetaan "referenssipinon" kaikki kenttäavaimet ja näiden arvot) ja palauttaa joukon arvo-teksti pareja parsittujen referenssien mukaan.
> 
> `[/references/referencePathGroup],methods=[POST]`  
> *Käyttö:* Parsii referenssipolku pohjaisen referenssipyynnön (annetaan linkitetty lista referenssipolku objekteja jotka sisältävät kaikki tarvittavat referenssit ja mahdolliset kenttäarvot) ja palauttaa joukon arvo-teksti pareja parsittujen referenssien mukaan.
> 
> `[/references/referenceStatus/{id}],methods=[GET]`  
> *Käyttö:* Palauttaa revisioitavan objektin tilan id:n perusteella. Kertoo onko kyseinen objekti olemassa ja onko sille suoritettu poisto.
>
> HUOM! tämä pitäisi siirtää selkeyden vuoksi RevisionControlleriin ja
> nimetä uudelleen "revisionableStatus"-metodiksi.

----------

> **Sijainti: [SettingsController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/SettingsController.java)**
> 
> `[/settings/listAPIUsers],methods=[GET]`  
> *Käyttö:* Palauttaa listan ulkoisten rajapintojen käyttäjistä.
> 
> `[/settings/removeAPIUser/{secret}],methods=[GET]`  
> *Käyttö:* Poistaa ulkoisen rajapinnan käyttäjän.
> 
> `[/settings/getJsonList/{type}],methods=[GET]`  
> *Käyttö:* Palauttaa listan kannasta löytyvistä pyydetyntyyppisistä json-tiedostoista.
> 
> `[/settings/getJsonContent],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn json-tiedoston sisällön (konfiguraatio tai vapaamuotoinen data).
> 
> `[/settings/indexEverything],methods=[GET]`  
> *Käyttö:* Pyytää hakukonetta tyhjentämään indeksit ja indeksoimaan kaiken indeksoitavan sisällön uudelleen.
> 
> `[/settings/stopIndexers],methods=[GET]`  
> *Käyttö:* Pysäyttää hakukoneen indekserit.
> 
> `[/settings/uploadJson],methods=[POST]`    *Käyttö:* Tallentaa annetun
> JSON-tiedoston tietokantaan (mahdollisesti korvaa jo olemassa olevan
> tiedoston).
> 
> `[/settings/newAPIUser],methods=[POST]`  
> *Käyttö:* Luo uuden ulkoisen rajapinnan käyttäjän.
> 
> `[/settings/downloadReport],methods=[GET]`  
> *Käyttö:* Palauttaa yksinkertaisen tietokantaraportin. 
> 
> `[/settings/openIndexCommands],methods=[GET]`  
> *Käyttö:* Palauttaa tiedon kuinka monta käsittelemätöntä indeksointipyyntöä on listalla.
> 
> `[/settings],methods=[GET]`  
> *Käyttö:* Palauttaa raportin ladattavaksi. HUOM! esimerkkitoteutus, hyödyllinen raporttitoiminto vaatii lisää määrittelyä.

----------
> **Sijainti: [GeneralController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/GeneralController.java)**
> 
> `[/],methods=[GET]`  
> *Käyttö:* Ohjaa käyttäjän .../metka/web/ osoitteesta .../metka/web/expert osoitteeseen
> 
> `[/logout],methods=[GET]`  
> *Käyttö:* tyhjentää käyttäjän session ja kirjaa käyttäjän ulos järjestelmästä. (Täällä on jokin bugi toistaiseksi)

----------

> **Sijainti: [ExpertSearchController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/ExpertSearchController.java)**
> 
> `[/expert/save],methods=[POST]`  
> *Käyttö:* Tallentaa käyttäjän tekemän haun tietokantaan
> 
> `[/expert/remove/{id}],methods=[GET]`  
> *Käyttö:* Poistaa tallennetun haun tietokannasta id:n perusteella
> 
> `[/expert/query],methods=[POST]`  
> *Käyttö:* Suorita eksperttihaku
> 
> `[/expert/list],methods=[GET]`  
> *Käyttö:* Listaa kaikki tallennetut haut
> 
> `[/expert],methods=[GET]`  
> *Käyttö:* Ohjaa käyttäjän "Eksperttihaku" alasivulle

----------

> **Sijainti: [RevisionController.java](./../../tree/master/metka/src/main/java/fi/uta/fsd/metka/mvc/controller/RevisionController.java)**
> 
> `[/revision/ajax/edit],methods=[POST]`  
> *Käyttö:* Pyytää muokattavaa luonnosta tietystä revisiosta, jos luonnos on jo olemassa palautetaan luonnos. Tämä ei takaa että käyttäjä voi muokata kyseistä luonnosta, olemassaolevalla luonnoksella voi olla eri handleri jo asetettuna.
> 
> `[/revision/ajax/approve],methods=[POST]`  
> *Käyttö:* Käynnistää hyväksymisprosessin annetulle TransferData objektille. Objektille suoritetaan ensin tallennus ja jos tallennus on onnistunut niin sen jälkeen suoritetaan hyväksyminen.
> 
> `[/revision/revisionCompare],methods=[POST]`  
> *Käyttö:* Vertaa kahta annettua revisiota keskenään ja palauttaa kyseisten revisioiden väliset muutokset listana.
> 
> `[/revision/adjacent],methods=[POST]`  
> *Käyttö:* Palauttaa annetun revision "viereisen" revisioitavan objektin, eli lähimmän revisioitavan objektin (id:n perusteella) jolla on sama tyyppi kuin annetulla objektilla. Käytetään edellinen/seuraava" tyyppiseen navigointiin.
> 
> `[/revision/ajax/view/{type}/{id}],methods=[GET]`  
> *Käyttö:* Palauttaa pyydetyn tyyppi+id yhdistelmän viimeisimmän revision jos kyseisellä yhdistelmällä löytyy revisioitava objekti.
> 
> `[/revision/ajax/beginEdit],methods=[POST]`  
> *Käyttö:* Palauttaa luonnoksen tai jos ei ole niin tekee uuden pyydetylle revisionablelle. 
> 
> `[/revision/ajax/claim],methods=[POST]`  
> *Käyttö:* Suorittaa annetulle revisiolle claim toiminnon, eli asettaa pyytävän käyttäjän kyseisen revision handleriksi jos revisio on luonnos tilassa.
> 
> `[/revision/ajax/view/{type}/{id}/{no}],methods=[GET]`  
> *Käyttö:* Palauttaa pyydetyn tyyppi+id+no yhdistelmää vastaavan revision jos kyseinen revisio löytyy.
> 
> `[/revision/download],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn revision json-datan tekstimuodossa tallennusta varten.
> 
> `[/revision/revisionHistory],methods=[POST]`  
> *Käyttö:* Palauttaa pyydetyn revision revisiohistorian.
> 
> `[/revision/view/{type}/{id}],methods=[GET]`  
> *Käyttö:* Siirtää käyttäjän annetun tyyppi+id parin viimeisimpään revisioon jos kyseinen pari löytyy.
> 
> `[/revision/view/{type}/{id}/{no}],methods=[GET]`  
> *Käyttö:* Siirtää käyttäjän tyyppi+id+no yhdistelmää vastaavaan revisioon jos kyseinen revisio löytyy.
> 
> `[/revision/ajax/configuration/{type}],methods=[GET]`  
> *Käyttö:* Palauttaa uusimman pyydetyntyyppisen konfiguraation.
> 
> `[/revision/ajax/restore],methods=[POST]`  
> *Käyttö:* Yrittää palauttaa loogisesti poistetun revisioitavan objektin.
> 
> `[/revision/ajax/remove],methods=[POST]`  
> *Käyttö:* Yrittää poistaa annetun revision. Jos revisio on luonnos ja poiston saa tehdä niin koko revisio poistetaan. Jos kyseessä on hyväksytty revisio eikä objektilla ole luonnoksia niin suoritetaan looginen poisto.
> 
> `[/revision/ajax/create],methods=[POST]`  
> *Käyttö:* Luo uuden pyydetyntyyppisen revisioitavan objektin ja onnistuneen luonnin jälkeen suorittaa sille CLAIM operaation.
> 
> `[/revision/ajax/save],methods=[POST]`  
> *Käyttö:* Yrittää tallentaa annetun TransferData objektin.
> 
> `[/revision/search/{type}],methods=[GET]`  
> *Käyttö:* Siirtää käyttäjän annetun tyypin "perushaku" sivulle.
> 
> `[/revision/ajax/search],methods=[POST]`  
> *Käyttö:* Suorittaa annetun revisiohaun (ns. perushaku).
> 
> `[/revision/ajax/release],methods=[POST]`  
> *Käyttö:* Vapauttaa annetun luonnoksen muiden käyttäjien muokattavaksi.

----------

# Palvelimen toiminta
Yleisperiaatteet palvelimella tapahtuville prosesseille löytyvät dokumentaatio kansiosta `metka_server_operation.graphml` kaaviosta.  

Spring huolehtii urlien reitityksetä oikeille Java-luokille. HTTP-pyyntöjä käsittelevät luokat ovat yleensä metka.mvc.controller-pakkauksen alaisia luokkia, mutta mikä tahansa `@Controller`-annotaatiolla merkatut luokat. Paketit joihin `@Controller` luokkia saa laittaa on listattu `apiServletContext.xml` ja `webServletContext.xml` tiedostoissa. Suurin osa `@Controller`-luokista löytyy `metka.mvc.controller`-paketista.

Kaikki pyyntöihin liittyvät Java-objektit, sekä parametrit että paluuarvot, voidaan ymmärtää json-objekteina kun kyseisen pyynnön käsittely on palvelimen ulkopuolella. Json-Java-Json muunnokset tehdään automaattisesti Jackson-kirjaston avulla joten `@Controller`-luokat voivat vastaanottaa ja lähettää Java-objekteja. UML-spesifikaatioita noudattavat Json-objektit ja rakenteet löytyvät `metka.model`-paketista, muut http-pyyntöjen tarvitsemat Java-objektit löytyvät `metka.transfer`-paketista (jos näitä löytyy jostain muualta niin on suositeltavaa refaktoroida nämä jompaan kumpaan pakettiin).

Kukin `@Controller` toimii http-rajapintana ja suorittaa hyvin vähän varsinaista toiminnallisuutta. Joissakin monimutkaisemmissa pyynnöisää `@Controller`-metodi saattaa tehdä jonkin verran pyynnön tai palautteen validointia ja päätöksiä siitä mihin pyynnöt tulee ohjata. `@Controller`-metodien pitäisi ohjata kaikki pyynnöt jonkin `@Service`-luokan läpi, mutta harvoissa erikoistapauksissa voi olla käytännöllistä ohjata pyyntö suoraan `@Repository`-luokkaan. Nämä tapaukset ovat kuitenkin harvassa.

Suurin osa `@Controller`-metodeista kutsuu `metka.mvc.services.impl`-paketissa olevia `@Service`-annotaatiolla merkattuja luokkia käyttäen `metka.mvc.services.`-paketista löytyviä Interface-tyyppejä. Suurin osa `@Service`-luokista on toteutettu käyttäen Interface-Implementation rakennetta joka mahdollistaa oikeuksien hallinnan käyttäen SpringSecurity-kirjastoa. Kaikkien `@Service`-luokkien jotka johtavat lopulta tietokantaan tulisi sisältää myös `@Transactional`-annotaatiota jolloin tietokannan transaktiot aloitetaan samaan aikaan autentikaation kanssa sillä osa autentikaatioon liittyvistä oikeuksista vaatii tietokantakyselyitä oikeuksien määrittämiseen.

`@Service`-luokkien metodit ottavat yleensä vastaan http-pyyntöjen parametrit sellaisinaan ja palauttavat vastausobjekteja joita http-pyynnöt voivat palauttaa suoraan. Nämä metodit voivat koostaa yhteen useita pyyntöjä `@Repository`-luokkiin mutta yleisemmin on suotavaa että `@Service`-metodit tekevät vain yhden pyynnön `@Repositoryyn` ja `@Repositoryt` voivat kutsua toisiaan kasatakseen sopivan tulosjoukon. `@Service`-metodien vastuulla on yleensä tehdä sopiva pyyntö ja kasata http-pyyntöä vastaava paluuarvo löydetyistä tuloksista. `@Service`-metodit voivat usein tehä pyyntöjä myös erilaisiin hakupalveluihin joko suoraan hakukomennoilla tai kutsumalla esimerkiksi `metka.search`-paketista löytyviä `@Repository`-luokkia.

`@Repository`-annotaatiolla merkatut luokat toimivat yhteytenä erilaisiin tietovarastoihin (esimerkiksi tietokanta, hakuindeksi). Suurin osa näistä luokista löytyy `metka.storage.repository` tai `metka.search` -paketeista. Nämä käyttävät jälleen Interface-Implementation rakennetta jolloin varsinainen toteutus on helpompi vaihtaa jos esim. datavarasto vaihtuu tietokannasta levykansioon. `@Repository`-luokat koostavat halutun tiedon tekemällä pyyntöjä joko suoraan datavarastoon tai muihin `@Repository`-luokkiin. Paluuarvot näistä luokista sisältävät yleensä jonkinlaisen informaation onnistuneesta tai epäonnistuneesta operaatiosta (ja mahdollisesti jonkinlaisen selityksen mikä epäonnistui) sekä yhden tai useampia paluuarvo-objekteja jotka voivat olla esim. `metka.model`-paketin toteutuksia json-objekteista.

Kun `@Repository`-luokat kommunikoivat tietokannan kanssa siihen käytetään yleensä `@Entity`-annotaatiolla merkattuja luokkia joista suurin osa löytyy `metka.storage.entity`-paketista ja jotka toimivat tietokantataulujen kuvauksina Java-koodissa. Varsinaisiin tietokantaoperaatioihin käytetään JPA-spesifikaation tarjoamia luokkia ja operaatioita (Metkan tapauksessa JPA:n toteutta Hibernate-kirjasto).


----------


# Rajapintakäyttäjät
API-servletin (kaikki api/ alkavat rajapintaosoitteet) käyttäjien määrittely tapahtuu tietokannan `api_user`-taulussa. Käyttäjälle on määritelty seuraavat tiedot:  
**api_user_id**: Rajapintakäyttäjän primääriavain, haetaan `api_user_id_seq`-sekvenssistä.  
**created_by**: Rajapintakäyttäjän luoneen käyttäjän käyttäjätunnus.  
**last_access**: Koska rajapintakäyttäjä on viimeksi tehnyt operaation. Palvelin ylläpitää.  
**name**: Rajapintakäyttäjän nimi. Kaikki käyttäjän tekemät operaatiot joista seurataan muutoksia käyttävät tätä nimeä muutosten merkintään. Yleensä muodossa `api:{name}` jossa `{name}` korvataan rajapintakäyttäjän nimellä.  
**permissions**: Rajapintakäyttäjän oikeudet. Tämä on bitflag tyyppinen tunniste, eli kentän arvo on yhteenlaskettu summa seuraavista tiedoista:  
* `1`: Saa luoda uuden aineiston.  
* `2`: Saa suorittaa lucene-haun ja pyytää revision uudelleenindeksointia.  
* `4`: Saa lukea revisiodataa.  
* `8`: Saa tallentaa revisiodataa.  
Eli esimerkiksi arvo `6` tarkoittaa että käyttäjä saa sekä suorittaa hakuja että lukea revisiodataa.  

**public_key**: Julkinen avain. Tämä on taulussa uniikki kenttä. Autentikointiin käytetään perinteistä api-key mekanismia jossa käyttäjällä on sekä julkinen, että salainen avain. Julkiselle avaimelle ei käytännössä ole mitään muita vaatiumksia kuin uniikkius. Yksi tapa muodostaa julkinen avain on konkatenoida käyttäjän tietokanta id, luojan tunnus ja luontiaika, ajaa tämä vaikkapa `SHA256` hash-funktion läpi ja tehdä tuloksesta `Base64` enkoodattu teksti. Tämä lähetetään autentikoinnin yhteydessä sellaisenaan.  
**secret**: Salainen avain. Tämä on taulussa uniikki kenttä. Myöskään salaiselle avaimelle ei käytännössä ole muita vaatimuksia kuin uniikkius. Yksi tapa muodostaa salainen avain on konkatenoida käyttäjän luontiaika, pulkinen avain, nimi, tietokannan id ja käyttäjän oikeudet, ajaa tämä vaikkapa `SHA512` hash-funktion läpi ja tehdä tuloksesta `Base64` enkoodattu teksti. Api-key autentikaatio käyttää tätä kannassa olevaa arvoa kun se yrittää varmentaa rajapintaan tullutta pyyntöä.  

Uutta käyttäjää luodessa arvot `created_by`, `name`, `permissions`, `public_key` ja `secret` tulee täyttää. Tietokannan käsittelyyn käytetystä työkalusta riippuu pitääkö `api_user_id` arvo noutaa itse sekvenssistä vai tuleeko arvo automaattisesti.


----------


# Käyttöliittymä

Sovelluksen käyttöliittymä rakennetaan JavaScriptillä. Sovellus hyödyntää pääasiassa näitä kirjastoja ja ohjelmistokehyksiä:

* [jQuery]
* [Bootstrap]
* [RequireJS]


----------


## Kansiorakenne

Käyttöliittymän tiedostot sijaitsevat [resources]-kansiossa. Palvelin ohjaa polkuihin `/css`, `/html`, `/js` ja `/lib` tulevat pyynnöt vastaaviin kansioihin [resources]-kansiossa. Reitityksiä voi hallita tiedostossa [globalServletContext.xml].


### [css/](./../../tree/master/metka/src/main/webapp/resources/css)

Kansio sisältä sovelluksen tyylit [styles.css]-tiedostossa, sekä käytöstä poistuneita muita CSS-tiedostoja.

Sovelluksen tyylien perustana on Bootstrapin tyylit, joita ylikirjoitetaan hallitusti ja mahdollisimman vähän.


### [html/](../../tree/master/metka/src/main/webapp/resources/html)

Kansio sisältää [käyttöohjeen][guide]. Muita staattisia html-tiedostoja sovelluksessa ei ole.


### [js/](../../tree/master/metka/src/main/webapp/resources/js)

Kansio sisältää sovelluksen JavaScript-tiedostot.


### [lib/](../../tree/master/metka/src/main/webapp/resources/lib)

Kansio sisältää kirjastot, lisäosat ym. sovelluksen riippuvuudet. Sivulle ne otetaan käyttöön [head.jsp](../../blob/master/metka/src/main/webapp/WEB-INF/inc/head.jsp)-tiedostossa.


----------


## JavaScript

Ohjelmoijan tulisi tuntea Array-objektin ES5-metodit, kuten [Array.prototype.forEach](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/forEach) ja [Array.prototype.map](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/map), sillä niitä käytetään sovelluksessa runsaasti.


### Rakenne

Ohjelmakoodi on jäsennelty [RequireJS]-moduuleihin [modules]-kansioon. Poikkeuksena:

- Sovelluksen RequireJS entry point: `main.js`
- `metka.js`-moduuli (jossa muutama ominaisuus, jolle ei ole löytynyt parempaa paikkaa)
- Globaalin `MetkaJS`-objektin alustus `metkaJS/metkaJS.js`-tiedostossa. Tähän objektiin palvelin asettaa käyttöliittymän alustuksessa tarvittavat tiedot, kuten käyttäjätiedot ja näytettävän sivun nimen.
- Ohjelmakoodi joka ei ole RequireJS:n alaisuudessa. (vain alkuvaiheessa kehitetty `MetkaJS/l10n.js`).


#### [modules]

RequireJS:n mukaisesti jokainen tiedosto on oma moduuli. Moduulit tässä sovelluksessa ovat lähes poikkeuksetta itsenäisiä aliohjelmia (JavaScript-funktioita) ja ne sijaitsevat päätasolla [modules]-kansiossa. Alikansioihin on ryhmitelty vain:

- [custom]/ Konfiguraatiossa määriteltyjen erikoistoimintoja sisältävien kenttien toiminnot
- [map]/ Muunnostoimintoja {fromFrormat}/{toFormat}.js
- [pages]/ Sivupohjat
- [utils]/ Yleiskäyttöisiä toimintoja

Lukuun ottamatta [custom]-elementtien sovelluslogiikkaa, RequireJS:stä käytetään synkronista versiota. Tämä siksi, että yksittäisen moduulin kirjoittaminen ja käyttöönotto olisi mahdollisimman vaivatonta. Sovelluksessa käytetään siis runsaasti tyyliä:

```js
// myModule.js

define(function (require) {
    'use strict';

    return function (name) {
        return 'hello ' + name;
    };
});
```

```js
// otherModule.js

define(function (require) {
    'use strict';

    return function (name) {
        var text = require('./myModule')('world!');
        console.log(text); // hello world!
    };
});
```


### jQuery, HTML:n rakentaminen ja DOM:in muokkaus

Sovelluksen UI rakennetaan jQueryllä. Rakentaminen alkaa `page.js`-tiedostosta, josta konfiguraatiota seuraten mennään alikomponentteihin, kunnes UI on valmis.

Jotta vältytään spagettimaiselta jQuery-koodilta, elementeille on vain harvoin asetettu omia `#id`:itä tai `.class`:eja (Poikkeuksena inputien generoidut ID:t `label`-elementtien `for`-attribuuteille). Tarvittaessa funktiot pitävät muistissaan rakentamansa elementit ja/tai välittävät niitä toisille funktioille. Elementtejä pyritään löytämään suhteellisilla metodeilla, kuten `.parent()` ja `.children()`. `$`-alkuiset muuttujat tarkoittavat jQuery-objekteja. Esim:

```js
var $button = $('<button type="button" class="btn">');
$someContainer.append($button);

// toisaalla
$button.click(function () {...});
```


----------


## Sovelluskehitys

(Edellytys: palvelinsovellus on asennettu ja käynnistetty onnistuneesti)

Kehityssykli on melko lyhyt:

1. Muokkaa tiedostoa.
2. Suorita [reloadWeb.js](../../blob/master/reloadWeb.js) (Node.js-skripti) tai [reloadWeb.sh](../../blob/master/reloadWeb.sh) -tiedosto ([resources]-kansio kopioidaan palvelimen hakemistoon).
3. F5, lataa sivu uudelleen.


----------

## Metkan asennus pääpiirteittäin sovelluskehitykseen

 1. Luo PostgreSQL-tietokanta metkalle.
 2. Conffaa sen asetukset esim filter-dev1.properties tiedostoon.
 3. Avaa koko projekti moduuleineen(metka, spssio, codebook) esim IntelliJ IDEA:an
 4. Buildaa ja asenna Mavenillä spssio ja codebook kirjasto-moduulit paikallisesti
 5. Buildaa metka-moduuli mvn clean package:lla, jolloin bootstrapattu Tomcat käynistyy ja metka avautuu http://localhost:8080/metka/web osoitteeseen

----------


[Bootstrap]:http://getbootstrap.com/
[jQuery]:http://jquery.com
[RequireJS]:http://requirejs.org/


[resources]:                ../../tree/master/metka/src/main/webapp/resources
[modules]:                  ../../tree/master/metka/src/main/webapp/resources/js/modules

[globalServletContext.xml]: ../../blob/master/metka/src/main/webapp/WEB-INF/globalServletContext.xml
[styles.css]:               ../../blob/master/metka/src/main/webapp/resources/css/styles.css
[guide]:                    ../../blob/master/metka/src/main/webapp/resources/html/guide/guide.html
[custom]:                   ../../blob/master/metka/src/main/webapp/resources/js/modules/custom
[map]:                   ../../blob/master/metka/src/main/webapp/resources/js/modules/map
[pages]:                   ../../blob/master/metka/src/main/webapp/resources/js/modules/pages
[utils]:                   ../../blob/master/metka/src/main/webapp/resources/js/modules/utils