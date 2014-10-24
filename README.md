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
