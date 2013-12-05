<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="materialFiles" class="tabs2 files">
					
	<div class="materialRowTitle"><spring:message code="study.view.files.title"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" id="materialFileTable">
		<thead>
			<tr><th><spring:message code="general.file"/></th>
			<th><spring:message code="study.view.files.saver"/></th>
			<th><spring:message code="general.language"/></th>
			<th></th></tr>
		</thead>
		<tbody>
			<c:forEach items="${study.data.files}" var="file">
				<tr class="materialFileRow">
				<td class="materialFileName">${file.path}<a href="dialogs/attachFileEditDialog.html" class="fancyboxpopup fancybox.ajax"></a></td>
				<td>${file.saver}</td>
				<td>${file.language}</td>
				<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td></tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="materialTableActionLinkHolder"><a href="dialogs/attachFileDialog.html" class="addRow fancyboxpopup fancybox.ajax"><spring:message code="general.buttons.add"/></a></div>						
	
	<div class="materialRowTitle"><spring:message code="study.view.files.removedFiles.title"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" id="materialRemovedFileTable">
		<thead>
			<tr><th><spring:message code="general.file"/></th>
			<th><spring:message code="study.view.files.fileDescription"/></th>
			<th><spring:message code="study.view.files.descrpition"/></th></tr>
		</thead>
		<tbody>
			<c:forEach items="${study.data.removedFiles}" var="file">
				<tr class="materialRemovedFileRow">
				<td class="materialFileName">${file.path}</td>
				<td>${file.fileDescription}</td>
				<td>${file.description}</td></tr>
			</c:forEach>
		</tbody>
	</table>
	
	<div id="materialRemovedFileInfoRow" style="display: none;">
		<div id="materialRemovedFileInfoContent">
			<table>
				<thead><tr><th class="fileInfoLabel"></th><th class="fileInfoContent"></th></tr></thead>
				<tbody>
					<%-- valitun tiedoston tiedot --%>
					<tr><td class="fileInfoLabel"><spring:message code="general.file"/></td><td class="fileInfoContent"><a id="" class="fileInfoContentFileName" href="#">${selectedFile.path}</a></td>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.officialDescription"/></td><td class="fileInfoContent">${selectedFile.officialDescription}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.unofficialDescription"/></td><td class="fileInfoContent">${selectedFile.unofficialDescription}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.comment"/></td><td class="fileInfoContent">${selectedFile.comment}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.saver"/></td><td class="fileInfoContent">${selectedFile.saver}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.pas"/></td><td class="fileInfoContent">${selectedFile.pas}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="general.language"/></td><td class="fileInfoContent">${selectedFile.language}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.origin"/></td><td class="fileInfoContent">${selectedFile.origin}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.delivery"/></td><td class="fileInfoContent">${selectedFile.delivery}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.www"/></td><td class="fileInfoContent">${selectedFile.www}</td></tr>
					<tr><td class="fileInfoLabel"><spring:message code="study.view.files.modificationDate"/></td><td class="fileInfoContent">${selectedFile.modified}</td></tr>
				</tbody>
			</table>
		</div>
	
		<div class="materialRowTitle"><spring:message code="study.view.files.removedFiles.title"/></div>
		<table class="metkaTable sortableTable" id="materialFileInfoTable">
			<thead>
				<tr><th><spring:message code="study.view.files.editDate"/></th>
				<th><spring:message code="study.view.files.editor"/></th>
				<th><spring:message code="study.view.files.description"/></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${selectedFile.historyItems}" var="historyItem">
					<tr><td>${historyItem.editDate}</td><td>${historyItem.editor}</td><td>${historyItem.description}</td></tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	
	<jsp:include page="buttons.jsp"/>
</div>