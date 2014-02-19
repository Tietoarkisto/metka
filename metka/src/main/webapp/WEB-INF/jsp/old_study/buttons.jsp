<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="buttonsHolder" style="margin-bottom:20px;">
	<a href="dialogs/versionHistoryDialog.html" class="versionHistoryButton fancyboxpopup fancybox.ajax button"><spring:message code="general.buttons.versionHistory"/></a>
	<input type="button" class="reserveMaterialButton previewButton searchFormInput" value="<spring:message code='general.buttons.reserve'/>" />

	<a href="dialogs/approveMaterialDialog.html" class="approveChangesButton editButton fancyboxpopup fancybox.ajax button"><spring:message code="general.buttons.approve"/></a>
	<input type="button" class="saveAsDraftButton editButton searchFormInput" value="<spring:message code='general.buttons.saveAsDraft'/>" />

	<input type="button" class="releaseMaterialButton reservedButton searchFormInput" value="<spring:message code='general.buttons.release'/>" />
	<input type="button" class="editMaterialButton reservedButton searchFormInput" value="<spring:message code='general.buttons.edit'/>" />
</div>	