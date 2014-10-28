Metka
=====

Documentation-kansiosta löytyy suuri määrä spesifikaatiota Metkan eri osista. Kansiosta löytyy myös README.md tiedosto joka kuvaa lyhyesti eri osia kansion sisällöstä.
Tässä dokumentissa kuvataan tarkemmin muuta osaa Metkan sisällöstä ja toiminnasta.

# Pakettijako
## fi.uta.fsd
Tässä projektissa tuotettu Java-koodi löytyy kaikki fi.uta.fsd-paketin alta. Lisäksi projektista löytyy spssio-paketti joka sisältää por-tiedostojen käsittelyyn tarkoitetun javakirjaston. spssio-kirjasto on täysin erotettavissa omaan projektiinsa, mutta yksinkertaisuuden vuoksi kirjasto on mukana koodissa.
### metkaAmqp
Paketti sisältää AMQP viestien (erityisesti Rabbit-viestien) lähettämiseen tarkoitetut koodit.
### metkaAuthentication
Paketti sisältää käyttäjien autentikointiin ja oikeuksienhallintaan liittyvät koodit, kuten eri oikeuksien ja roolien määrittelyt.
### metkaExternal
Paketti sisältää ulkoisiin rajapintoihin (muuten kuin nettisivun kautta käytettäväksi tarkoitettujen toimintojen) määrittelyyn ja toteutukseen liittyvät koodit.
### metkaSearch
Paketti sisältää lucene-indeksointiin ja hakuihin, sekä Voikko-analysaattoriin liittyvät koodit
#### analyzer
Sisältää Metkan tarvitsemia kustomoituja tekstianalysaattoreita.
#### commands
Sisältää lucenen käyttöön liittyviä komentoja jolla lucenea ohjaillaan muun koodin puolesta.
##### indexer
Sisältää datan indeksointiin liittyviä komentoja
##### searcher
Sisältää datan hakemiseen liittyviä komentoja
#### directory
Sisältää indeksitiedostojen hallintaan tarkoitettuja apuluokkia
#### entity
Sisältää indeksointiin liittyviä JPA-entiteettejä sekä näihin liittyvän repositoryn
#### enums
Sisältää indeksointiin liittyviä enumeraattoreita
#### filters
Sisältää Metkan tarvitsemia kustomoituja tekstifilttereitä
#### handlers
Sisältää Metkan komentojen (yleensä indeksointikomentojen) käyttöön liittyviä datan käsittelijöitä
#### indexers
Sisältää erityyppisen datan indeksointiin liittyviä luokkia
#### iterator
Sisältää Metkan tarvitsemia kustomoituja iteraattoreita jotka liittyvät erityisesti indeksointiin ja hakuun.
#### results
Sisältää hakukomentojen paluuarvoja
#### searchers
Sisältää erityyppisen datan hakuun liittyviä luokkia.
#### voikko
Sisältää Voikko-kirjaston käyttöön liittyviä luokkia
### metka
Sisältää pääosan Metkan palvelinkoodista. Täältä löytyy mm. varsinaisen lomakedatan ja nettisivun pyörittämiseen liittyvät koodit.
#### adapters
Sisältää erilaisia adaptereita joita tarvitaan datan muunnoksissa formaatista toiseen.
#### automation
Sisältää automaattisesti käynnistyvää toiminnallisuutta, kuten konfiguraatioiden luku levyltä kun palvelin käynnistetään.
#### ddi
Sisältää DDI-tiedostojen käsittelyyn liittyvää koodia.
##### builder
Sisältää DDI-tiedoston export-toimintoon liittyvät koodit
##### reader
Sisältää DDI-tiedoston import-toimintoon liittyvät koodit
#### enums
Sisältää Metkan yleisesti käyttämiä enumeraattoreita
#### model
Sisältää json-tiedostojen käyttöön liittyvät koodit, kuten datan käsittely, luokkamallit json-tiedostoille, sarjoittamiseen ja lukemiseen liittyvät erikoistoiminnot jne.
##### access
Data-json tietojen käsittelyyn liittyvät apuluokat. Sisältää datan lukemisen, muokkauksen ja tarkistuksen ja näihin liittyvät komennot. Nämä komennot käsittelevät vain java-luokkia joten json-tieto on ensin luettava objekteiksi.
##### json-rakenteet
general-, configuration-, data-, guiconfiguration- ja transfer -paketit sisältävät java-luokka pohjaisen esitystavan kyseisistä json-tiedostoista. Näissä objekteissa määritellään mitä propertyjä json-objekteilla voi olla ja mitkä ovat näiden vakioarvot, jos vakioarvoja on. Objektit käyttävät tilannekohtaisesti polymorfismia, mutta kaikkia tilanteita joissa polymorfismia voisi käyttää ei ole toteutettu näin. Json sarjoitetaan ja luetaan käyttäen Jackson-json kirjaston toimintoja joten eri annotaatiot löytyvät kyseisen kirjaston dokumentaatiosta.
##### deserializers
Erilaisten json-objektien lukijoita.
##### factories
Json-objektien ja rakenteiden (pääasiassa datan) käsittelyyn liittyviä luokkia. Nämä luokat mm. muodostavat pohjadatan kun tehdään uusia revisioitavia objekteja ja varmistavat että kaikki tarvittava tieto löytyy datasta. Lisäksi täältä löytyy mm. aineiston biblcit ja packages tietojen muodostaminen.
##### interfaces
Erilaiset Json-tietoihin liittyvät interface-objektit joita käytetään java-luokissa.
##### serializers
Erilaisten json-objektien sarjoittajia.
#### mvc
Käyttöliittymärajapintaan liittyvät luokat löytyvät täältä.
##### controller
Eri html-pyyntöjen määrittelyt
##### services
Palvelut jotka käsittelevät html-pyyntöjä, keskustelevat tietovarastorajapintojen kanssa ja muokkaavat dataa käyttöliittymän ymmärtämään muotoon.
###### simple
Sisältää vanhentuneita luokkia ja luokkia jotka pitäisi siirtää mekta.transfer paketin alle.
##### validator
Erilaiset validaattorit joita käytetään käyttöliittymän tekemien pyyntöje kanssa. Ei tällä hetkellä käyttöä.
##### MetkaObjectMapper-luokka
Määrittelee miten java-objekteja tulkitaan json-tiedostoiksi. Tämä koskee kaikkea objecti-json-objekti muunnosta, ei pelkästään käyttöliittymälle menevää muunnosta. Luokka on kuitenkin määritelty täällä koska suurin osa objekti-json-objekti muunnoksista tapahtuu käyttöliittymän ja palvelimen välisessä liikenteessä.
##### ModelUtil-luokka
Määrittelee html-navigointipyyntöihin liittyviä vakioarvoja kuten käyttäjänimen ja sivuston osan johon käyttäjä ohjataan. Kaikkien html-pyyntöjen jotka ohjaavat käyttäjän jsp-sivulle tulisi käyttää tätä luokkaa näiden vakioarvojen asettamiseen.
#### names
Määrittelee erilaisia vakiotekstikokoelmia jotka sisältävät mm. Metkan käyttämiä kenttäavaimia. Eivät sinällään liity ohjelman toimintaan vaan yrittävät helpottaa kirjoitusvirheiden välttämistä.
#### search
Erilaisten hakutoimintojen toteutukset. Haut voivat sisältää hakupyyntöjä lucene-indeksiin, hakuja tietokannasta ym.
#### storage
Tiedon säilytykseen liittyvät luokat.
##### collecting
Sisältää toteutuksen erilaisten referenssien käsittelylle ja avaamiselle arvojoukoiksi.
##### entity
Metkan käyttämät JPA-entiteetit
##### repository
Metkan käyttämät Repository-objektit. Repository on käytännössä rajapinta tietovarastoon ja hoitaa haut ja tallennukset tähän varastoon. Tietovarasto voi olla tietokanna taulu, lucene-indeksi ym. Sisältää myös erityisesti repositoryjen käyttöön tarkoitettuja enumeraattoreita.
##### response
Sisältää repositoryjen käyttämiä paluuarvo-objekteja joihin koostetaan dataa yksinkertaisempaan muotoon.
##### restrictions
Sisältää rajoitekonfiguraation käsittelyyn ja validointiin liittyvät luokat.
##### util
Sisältää yleisiä apuluokkia tallennusdatan käsittelyyn.
##### variables
Sisältää toteutuksen por-tiedostojen parsinnasta aineistomuuttujiksi.
#### transfer
Sisältää erinäisiä käyttöliittymän ja palvelimen väliseen kommunikointiin liittyviä request- response- ynnä muita objekteja. Nämä vastaavat json-rakenteita joita käyttöliittymä lähettää tai ottaa vastaan.
# Reititys
Kaikki web-osoitteet joihin Metka vastaa löytyvät luokista joiden nimi päättyy termiin *Controller*. HTTP-metodien käyttö on jaettu kahteen osaan. Jos metodi ei ota vastaan parametreja tai ainoat parametrit löytyvät osana URIa niin käytetään GET-metodia, muutoin käytetään POST-metodia.
### api
Kaikki polut jotka alkavat `api/` saapuvat tähän osioon. Nämä polut eivät käytä Shibboleth-autentikaatiota vaan autentikaatio joko pakotetaan tiettyyn käyttäjään tai suoritetaan jollain mulla autentikaatiotavalla (yleensä käyttäen api-avain autentikaatiota).

**Sijainti: APIController**  
`[/createStudy],methods=[POST]`  
*Käyttö:* Luo uuden aineiston.

`[/getConfiguration],methods=[POST]`  
*Käyttö:* Palauttaa pyydetyn datakonfiguraation.

`[/getRevision],methods=[GET]`  
*Käyttö:* Palauttaa pyydetyn revisiodatan.

`[/index],methods=[POST]`  
*Käyttö:* Pyytää hakukonetta indeksoimaan annetun listan revisioita.

`[/save],methods=[POST]`  
*Käyttö:* Yrittää tallentaa annetun revisiodatan

`[/search],methods=[POST]`  
*Käyttö:* Suorittaa annetun hakulauseen.

### web
Kaikki polut jotka alkavat `web/` saapuvat tähän osioon. Nämä polut käyttävät Shibboleth-autentikaatiota joten käyttäjän täytyy olla kirjautuneena jotta pyynnön käsittely voidaan suorittaa onnistuneesti.

**Sijainti: BinderController**  
`[/binder],methods=[GET]`  
*Käyttö:* Ohjaa käyttäjän "Mapit" alasivulle

`[/binder/binderContent/{binderId}],methods=[GET]`  
*Käyttö:* Palauttaa yhden mapin sisällön

`[/binder/listBinderPages],methods=[GET]`  
*Käyttö:* Listaa kaikki mappisivut

`[/binder/listStudyBinderPages/{id}],methods=[GET]`  
*Käyttö:* Listaa kaikki mappisivut johon tietty aineisto kuuluu

`[/binder/removePage/{pageId}],methods=[GET]`  
*Käyttö:* Poistaa "mappisivun" tietokannasta

`[/binder/saveBinderPage],methods=[POST]`  
*Käyttö:* Tallettaa mappisivun käyttäjän syötteen perusteella. Jos sivu on uusi niin uusi rivi luodaan tietokantaan, muuten vanha rivi päivitetään.

**Sijainti: ExpertSearchController**  
`[/expert],methods=[GET]`  
*Käyttö:* Ohjaa käyttäjän "Eksperttihaku" alasivulle

`[/expert/list],methods=[GET]`  
*Käyttö:* Listaa kaikki tallennetut haut

`[/expert/query],methods=[POST]`  
*Käyttö:* Suorita eksperttihaku

`[/expert/remove/{id}],methods=[GET]`  
*Käyttö:* Poistaa tallennetun haun tietokannasta id:n perusteella

`[/expert/save],methods=[POST]`  
*Käyttö:* Tallentaa käyttäjän tekemän haun tietokantaan

**Sijainti: GeneralController**  
`[/],methods=[GET]`  
*Käyttö:* Ohjaa käyttäjän .../metka/web/ osoitteesta .../metka/web/expert osoitteeseen

`[/logout],methods=[GET]`  
*Käyttö:* tyhjentää käyttäjän session ja kirjaa käyttäjän ulos järjestelmästä. (Täällä on jokin bugi toistaiseksi)

**Sijainti: ReferenceController**  
`[/references/collectOptionsGroup],methods=[POST]`  
*Käyttö:* Parsii joukon kenttäavain pohjaisia referenssipyyntöjä (annetaan "referenssipinon" kaikki kenttäavaimet ja näiden arvot) ja palauttaa joukon arvo-teksti pareja parsittujen referenssien mukaan.

`[/references/referencePathGroup],methods=[POST]`  
*Käyttö:* Parsii referenssipolku pohjaisen referenssipyynnön (annetaan linkitetty lista referenssipolku objekteja jotka sisältävät kaikki tarvittavat referenssit ja mahdolliset kenttäarvot) ja palauttaa joukon arvo-teksti pareja parsittujen referenssien mukaan.

`[/references/referenceRowRequest],methods=[POST]`  
*Käyttö:* Palauttaa REFERENCECONTAINER tyyppisen kentän rivin annettujen parametrien perusteella (halutun revision id ja polku kyseiseen tauluun, sekä rivissä oleva arvo). Rivi palautetaan TransferRow objektina joten se on heti liitettävissä käyttöliittymään.

`[/references/referenceStatus/{id}],methods=[GET]`  
*Käyttö:* Palauttaa revisioitavan objektin tilan id:n perusteella. Kertoo onko kyseinen objekti olemassa ja onko sille suoritettu poisto. HUOM! tämä pitäisi siirtää selkeyden vuoksi RevisionControlleriin ja nimetä uudelleen "revisionableStatus"-metodiksi.

**Sijainti: RevisionController**  
`[/revision/adjacent],methods=[POST]`  
*Käyttö:* Palauttaa annetun revision "viereisen" revisioitavan objektin, eli lähimmän revisioitavan objektin (id:n perusteella) jolla on sama tyyppi kuin annetulla objektilla. Käytetään "edellinen/seuraava" tyyppiseen navigointiin.

`[/revision/ajax/approve],methods=[POST]`  
*Käyttö:* Käynnistää hyväksymisprosessin annetulle TransferData objektille. Objektille suoritetaan ensin tallennus ja jos tallennus on onnistunut niin sen jälkeen suoritetaan hyväksyminen.

`[/revision/ajax/claim],methods=[POST]`  
*Käyttö:* Suorittaa annetulle revisiolle claim toiminnon, eli asettaa pyytävän käyttäjän kyseisen revision handleriksi jos revisio on luonnos tilassa.

`[/revision/ajax/configuration/{type}],methods=[GET]`  
*Käyttö:* Palauttaa uusimman pyydetyntyyppisen konfiguraation.

`[/revision/ajax/create],methods=[POST]`  
*Käyttö:* Luo uuden pyydetyntyyppisen revisioitavan objektin ja onnistuneen luonnin jälkeen suorittaa sille CLAIM operaation.

`[/revision/ajax/edit],methods=[POST]`  
*Käyttö:* Pyytää muokattavaa luonnosta tietystä revisiosta, jos luonnos on jo olemassa palautetaan luonnos. Tämä ei takaa että käyttäjä voi muokata kyseistä luonnosta, olemassaolevalla luonnoksella voi olla eri handleri jo asetettuna.

`[/revision/ajax/release],methods=[POST]`  
*Käyttö:* Vapauttaa annetun luonnoksen muiden käyttäjien muokattavaksi.

`[/revision/ajax/remove],methods=[POST]`  
*Käyttö:* Yrittää poistaa annetun revision. Jos revisio on luonnos ja poiston saa tehdä niin koko revisio poistetaan. Jos kyseessä on hyväksytty revisio eikä objektilla ole luonnoksia niin suoritetaan looginen poisto.

`[/revision/ajax/restore],methods=[POST]`  
*Käyttö:* Yrittää palauttaa loogisesti poistetun revisioitavan objektin.

`[/revision/ajax/save],methods=[POST]`  
*Käyttö:* Yrittää tallentaa annetun TransferData objektin.

`[/revision/ajax/search],methods=[POST]`  
*Käyttö:* Suorittaa annetun revisiohaun (ns. perushaku).

`[/revision/ajax/view/{type}/{id}],methods=[GET]`  
*Käyttö:* Palauttaa pyydetyn tyyppi+id yhdistelmän viimeisimmän revision jos kyseisellä yhdistelmällä löytyy revisioitava objekti.

`[/revision/ajax/view/{type}/{id}/{no}],methods=[GET]`  
*Käyttö:* Palauttaa pyydetyn tyyppi+id+no yhdistelmää vastaavan revision jos kyseinen revisio löytyy.

`[/revision/download],methods=[POST]`  
*Käyttö:* Palauttaa pyydetyn revision json-datan tekstimuodossa tallennusta varten.

`[/revision/revisionCompare],methods=[POST]`  
*Käyttö:* Vertaa kahta annettua revisiota keskenään ja palauttaa kyseisten revisioiden väliset muutokset listana.

`[/revision/revisionHistory],methods=[POST]`  
*Käyttö:* Palauttaa pyydetyn revision revisiohistorian.

`[/revision/search/{type}],methods=[GET]`  
*Käyttö:* Siirtää käyttäjän annetun tyypin "perushaku" sivulle.

`[/revision/studyIdSearch/{studyId}],methods=[GET]`  
*Käyttö:* Yrittää löytää aineiston annetun aineistonumeron perusteella.

`[/revision/view/{type}/{id}],methods=[GET]`  
*Käyttö:* Siirtää käyttäjän annetun tyyppi+id parin viimeisimpään revisioon jos kyseinen pari löytyy.

`[/revision/view/{type}/{id}/{no}],methods=[GET]`  
*Käyttö:* Siirtää käyttäjän tyyppi+id+no yhdistelmää vastaavaan revisioon jos kyseinen revisio löytyy.

**Sijainti: SeriesController**  
`[/series/getAbbreviations],methods=[GET]`  
*Käyttö:* Palauttaa listan kaikista löytyvistä sarjalyhenteistä. HUOM! voidaan yleensä korvata referenssikentällä.

`[/series/getNames],methods=[GET]`  
*Käyttö:* Palauttaa listan kaikista sarjanimistä. HUOM! voidaan yleensä korvata referenssikentällä.

**Sijainti: SettingsController**  
`[/settings],methods=[GET]`  
*Käyttö:* Siirtää käyttäjän asetukset alasivulle.

`[/settings/downloadReport],methods=[GET]`  
*Käyttö:* Palauttaa raportin ladattavaksi. HUOM! esimerkkitoteutus, hyödyllinen raporttitoiminto vaatii lisää määrittelyä.

`[/settings/getJsonContent],methods=[POST]`  
*Käyttö:* Palauttaa pyydetyn json-tiedoston sisällön (konfiguraatio tai vapaamuotoinen data).

`[/settings/getJsonList/{type}],methods=[GET]`  
*Käyttö:* Palauttaa listan kannasta löytyvistä pyydetyntyyppisistä json-tiedostoista.

`[/settings/indexEverything],methods=[GET]`  
*Käyttö:* Pyytää hakukonetta tyhjentämään indeksit ja indeksoimaan kaiken indeksoitavan sisällön uudelleen.

`[/settings/listAPIUsers],methods=[GET]`  
*Käyttö:* Palauttaa listan ulkoisten rajapintojen käyttäjistä.

`[/settings/newAPIUsers],methods=[POST]`  
*Käyttö:* Luo uuden ulkoisen rajapinnan käyttäjän.

`[/settings/openIndexCommands],methods=[GET]`  
*Käyttö:* Palauttaa tiedon kuinka monta käsittelemätöntä indeksointipyyntöä on listalla.

`[/settings/removeAPIUser/{key}],methods=[GET]`  
*Käyttö:* Poistaa ulkoisen rajapinnan käyttäjän.

`[/settings/uploadJson],methods=[POST]`  
*Käyttö:* Tallentaa annetun json-tiedoston tietokantaan (mahdollisesti korvaa jo olemassa olevan tiedoston).

**Sijainti: StudyController**  
`[/study/attachmentHistory],methods=[POST]`  
*Käyttö:* Palauttaa aineistoliitteen revisiohistorian. Sisältää ylimääräistä tietoa mitä normaali revisiohistoriapyyntö ei sisällä.

`[/study/ddi/export],methods=[POST]`  
*Käyttö:* Palauttaa pyydetyn aineiston DDI 2.5 xml -formaatissa.

`[/study/ddi/import],methods=[POST]`  
*Käyttö:* Yrittää tuoda tietoja annetusta DDI 2.5 xml -tiedostosta määriteltyyn aineistoon.

`[/study/listErrors/{id}],methods=[GET]`  
*Käyttö:* Palauttaa listan annettuun aineistoon liittyvistä aineistovirheistä.

`[/study/removeError/{id}],methods=[GET]`  
*Käyttö:* Poistaa annetun aineistovirheen.

`[/study/studiesWithErrors],methods=[GET]`  
*Käyttö:* Palauttaa listan aineistoista joille on merkattu aineistovirheitä.

`[/study/studiesWithVariables],methods=[GET]`  
*Käyttö:* Palauttaa listan aineistoista joille on luotu muuttujia.

`[/study/updateError],methods=[POST]`  
*Käyttö:* Päivittää aineistovirheen, jos virhettä ei vielä ole olemassa niin virhe lisätään kantaan.
# Palvelimen toiminta
Yleisperiaatteet palvelimella tapahtuville prosesseille löytyvät dokumentaatio kansiosta `metka_server_operation.graphml` kaaviosta.  

Spring huolehtii urlien reitityksetä oikeille Java-luokille. HTTP-pyyntöjä käsittelevät luokat ovat yleensä metka.mvc.controller-pakkauksen alaisia luokkia, mutta mikä tahansa `@Controller`-annotaatiolla merkatut luokat. Paketit joihin `@Controller` luokkia saa laittaa on listattu `apiServletContext.xml` ja `webServletContext.xml` tiedostoissa. Suurin osa `@Controller`-luokista löytyy `metka.mvc.controller`-paketista.

Kaikki pyyntöihin liittyvät Java-objektit, sekä parametrit että paluuarvot, voidaan ymmärtää json-objekteina kun kyseisen pyynnön käsittely on palvelimen ulkopuolella. Json-Java-Json muunnokset tehdään automaattisesti Jackson-kirjaston avulla joten `@Controller`-luokat voivat vastaanottaa ja lähettää Java-objekteja. UML-spesifikaatioita noudattavat Json-objektit ja rakenteet löytyvät `metka.model`-paketista, muut http-pyyntöjen tarvitsemat Java-objektit löytyvät `metka.transfer`-paketista (jos näitä löytyy jostain muualta niin on suositeltavaa refaktoroida nämä jompaan kumpaan pakettiin).

Kukin `@Controller` toimii http-rajapintana ja suorittaa hyvin vähän varsinaista toiminnallisuutta. Joissakin monimutkaisemmissa pyynnöisää `@Controller`-metodi saattaa tehdä jonkin verran pyynnön tai palautteen validointia ja päätöksiä siitä mihin pyynnöt tulee ohjata. `@Controller`-metodien pitäisi ohjata kaikki pyynnöt jonkin `@Service`-luokan läpi, mutta harvoissa erikoistapauksissa voi olla käytännöllistä ohjata pyyntö suoraan `@Repository`-luokkaan. Nämä tapaukset ovat kuitenkin harvassa.

Suurin osa `@Controller`-metodeista kutsuu `metka.mvc.services.impl`-paketissa olevia `@Service`-annotaatiolla merkattuja luokkia käyttäen `metka.mvc.services.`-paketista löytyviä Interface-tyyppejä. Suurin osa `@Service`-luokista on toteutettu käyttäen Interface-Implementation rakennetta joka mahdollistaa oikeuksien hallinnan käyttäen SpringSecurity-kirjastoa. Kaikkien `@Service`-luokkien jotka johtavat lopulta tietokantaan tulisi sisältää myös `@Transactional`-annotaatiota jolloin tietokannan transaktiot aloitetaan samaan aikaan autentikaation kanssa sillä osa autentikaatioon liittyvistä oikeuksista vaatii tietokantakyselyitä oikeuksien määrittämiseen.

`@Service`-luokkien metodit ottavat yleensä vastaan http-pyyntöjen parametrit sellaisinaan ja palauttavat vastausobjekteja joita http-pyynnöt voivat palauttaa suoraan. Nämä metodit voivat koostaa yhteen useita pyyntöjä `@Repository`-luokkiin mutta yleisemmin on suotavaa että `@Service`-metodit tekevät vain yhden pyynnön `@Repositoryyn` ja `@Repositoryt` voivat kutsua toisiaan kasatakseen sopivan tulosjoukon. `@Service`-metodien vastuulla on yleensä tehdä sopiva pyyntö ja kasata http-pyyntöä vastaava paluuarvo löydetyistä tuloksista. `@Service`-metodit voivat usein tehä pyyntöjä myös erilaisiin hakupalveluihin joko suoraan hakukomennoilla tai kutsumalla esimerkiksi `metka.search`-paketista löytyviä `@Repository`-luokkia.

`@Repository`-annotaatiolla merkatut luokat toimivat yhteytenä erilaisiin tietovarastoihin (esimerkiksi tietokanta, hakuindeksi). Suurin osa näistä luokista löytyy `metka.storage.repository` tai `metka.search` -paketeista. Nämä käyttävät jälleen Interface-Implementation rakennetta jolloin varsinainen toteutus on helpompi vaihtaa jos esim. datavarasto vaihtuu tietokannasta levykansioon. `@Repository`-luokat koostavat halutun tiedon tekemällä pyyntöjä joko suoraan datavarastoon tai muihin `@Repository`-luokkiin. Paluuarvot näistä luokista sisältävät yleensä jonkinlaisen informaation onnistuneesta tai epäonnistuneesta operaatiosta (ja mahdollisesti jonkinlaisen selityksen mikä epäonnistui) sekä yhden tai useampia paluuarvo-objekteja jotka voivat olla esim. `metka.model`-paketin toteutuksia json-objekteista.

Kun `@Repository`-luokat kommunikoivat tietokannan kanssa siihen käytetään yleensä `@Entity`-annotaatiolla merkattuja luokkia joista suurin osa löytyy `metka.storage.entity`-paketista ja jotka toimivat tietokantataulujen kuvauksina Java-koodissa. Varsinaisiin tietokantaoperaatioihin käytetään JPA-spesifikaation tarjoamia luokkia ja operaatioita (Metkan tapauksessa JPA:n toteutta Hibernate-kirjasto).

# JavaScript

**TODO: Tähän JavaScript kuvaus**

# Konfiguraatiotutoriaali

**TODO: Tähän konfiguraatiotutoriaali**
