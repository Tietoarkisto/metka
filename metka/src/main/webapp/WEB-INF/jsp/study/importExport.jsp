<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="importExport" class="tabs2 importExport">
	<div class="viewFormButtonsHolder" style="margin-bottom:20px;">
		<%-- Import study ei kuulu tänne, uusi välilehti "Aineistot"-välilehdelle? --%>
		<input type="button" class="searchFormInput import" value="<spring:message code='study.view.importExport.importStudy'/>" />
		<input type="button" class="searchFormInput export" value="<spring:message code='study.view.importExport.exportAll'/>" />
		<input type="button" class="searchFormInput ddi2ExportButton" value="<spring:message code='study.view.importExport.ddi2Export'/>" />
		<input type="button" class="searchFormInput htmlViewButton" value="<spring:message code='study.view.importExport.htmlView'/>" />
	</div>
</div>