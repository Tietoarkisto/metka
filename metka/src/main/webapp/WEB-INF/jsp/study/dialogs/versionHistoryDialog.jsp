<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="versionHistoryDialog">
	<h1 class="pageTitle">Versiohistoria</h1>
	<div class="popupRowHolder">
		<div class="versionHistoryDialogRow">
			<div class="versionName" style="font-weight: bold;">Versionumero</div>
			<div class="versionPublishedDate" style="font-weight: bold;">Julkaisupvm</div>
			<div class="versionCompare" style="font-weight: bold;">Vertaa</div>
			<div class="versionReplace" style="font-weight: bold;">Korvaa</div>
		</div>
		<div class="versionHistoryDialogRow">
			<div class="versionName">Versio 1</div>
			<div class="versionPublishedDate">2.10.2013</div>
			<div class="versionCompare"><input type="checkbox" name="version"/></div>
			<div class="versionReplace"><input type="button" class="searchFormInput" value="Korvaa"/></div>
		</div>
		<div class="versionHistoryDialogRow">
			<div class="versionName">Versio 2</div>
			<div class="versionPublishedDate">12.10.2013</div>
			<div class="versionCompare"><input type="checkbox" name="version"/ name="version"></div>
			<div class="versionReplace"><input type="button" class="searchFormInput" value="Korvaa"/></div>
		</div>
		<div class="versionHistoryDialogRow">
			<div class="versionName">Versio 3</div>
			<div class="versionPublishedDate">22.10.2013</div>
			<div class="versionCompare"><input type="checkbox" name="version"/></div>
			<div class="versionReplace"><input type="button" class="searchFormInput" value="Korvaa"/></div>
		</div>
		<div class="versionHistoryDialogRow">
			<div class="versionName">LUONNOS</div>
			<div class="versionPublishedDate"></div>
			<div class="versionCompare"><input type="checkbox" name="version"/></div>
			<div class="versionReplace"><input type="button" class="searchFormInput" value="Korvaa"/></div>
		</div>
	</div>
	
	<br/>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="Sulje"/>
		<input type="button" id="compareVersionsButton" class="searchFormInput" value="Vertaa">
			<a href="dialogs/compareVersionsDialog.html" id="compareVersionsLink" class="fancyboxpopup fancybox.ajax"></a>
		</input>
	</div>
</div>	