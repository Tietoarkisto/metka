<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="compareVersionsDialog">
	<h1 class="pageTitle">Versioiden vertailu (Versio 1 -> Versio 3)</h1>
	<div class="popupRowHolder">
		<div class="versionHistoryCompareRow">
			<div class="versionProperty" style="font-weight: bold;">Ominaisuus</div>
			<div class="versionChange" style="font-weight: bold;">Muutos</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Perustiedot: Aineiston nimi</div>
			<div class="versionChange">"Eduskuntavaalitutkimukset 2003-2011: aineisto" -> "Eduskuntavaalitutkimukset 2003-2011: yhditetty aineisto"</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Perustiedot: Aineiston tyyppi</div>
			<div class="versionChange">Yhdistelm� -> Opetus</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Perustiedot: Aineiston sarja</div>
			<div class="versionChange">"V��r� sarjanimi" -> "Yksitt�iset materiaalit"</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Perustiedot: Liittyv�t julkaisut</div>
			<div class="versionChange">Lis�tty: "2345 (Julkaisu2)"</div>
		</div>				
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Koodikirja: liitteet</div>
			<div class="versionChange">Poistettu: data.zip</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Tiedostojen hallinta: foobar.docx: PAS</div>
			<div class="versionChange">Ei tietoa -> Kyll�</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Tiedostojen hallinta: foobar.docx: Virallinen selite</div>
			<div class="versionChange">Lis�tty: "T�ss� virallinen selite"</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Tiedostojen hallinta</div>
			<div class="versionChange">Poistettu: removed.zip</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Muuttujat: "[q4_1] Vastaajan ammattiryhm�": Haastattelijan ohje</div>
			<div class="versionChange">"Vanha ohje" -> "Uusi ohje"</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Muuttujat: "[q4_1] Vastaajan ammattiryhm�": Vaihtoehdot</div>
			<div class="versionChange">"Jonkin verran kiinnostunut" -> "V�h�n kiinnostunut"</div>
		</div>
		<div class="versionHistoryCompareRow">
			<div class="versionProperty">Muuttujat: Ryhm�t</div>
			<div class="versionChange">"Kuinka paljon seurasitte eduskuntavaaleja eri tiedotusv�lineist�?" -> "Kuinka paljon seurasitte eduskuntavaaleja eri mediasta?"</div>
		</div>

	</div>

	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="Sulje">
	</div>
</div>	