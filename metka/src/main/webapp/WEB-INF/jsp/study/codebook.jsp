<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="materialCodebook" class="tabs2 codebook">
	<div class="rowContainer containsTranslations">
	</div>
	<div class="rowContainer containsTranslations">
		<%-- codeBookFileDialog --%>
		<div class="materialRowTitle"><spring:message code="study.view.codebook.tableTitle"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></div>
		<table class="metkaTable" id="materialCodebookFileTable">
			<thead>
				<tr><th><spring:message code="general.file"/></th>
					<th><spring:message code="study.view.codebook.title"/></th>
					<th><spring:message code="study.view.codebook.type"/></th>
					<th><spring:message code="study.view.codebook.description"/></th>
					<th><spring:message code="general.date.short"/></th>
					<th><spring:message code="study.view.codebook.saver"/></th><th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.codebook.files}" var="file">
					<tr class="materialCodebookFileRow">
						<td class="materialCodebookFileName">${file.path}</td>
						<td>${file.title}</td>
						<td>${file.type}</td>
						<td>${file.description}</td>
						<td>${file.date}</td>
						<td>${file.saver}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>		

		<div class="translationSv">
			<div class="materialRowTitle"><spring:message code="study.view.codebook.tableTitle"/></div>
			<table class="metkaTable" id="materialCodebookFileTable">
				<thead>					
					<tr><th><spring:message code="study.view.codebook.file"/></th>
					<th><spring:message code="study.view.codebook.title"/></th>
					<th><spring:message code="study.view.codebook.type"/></th>
					<th><spring:message code="study.view.codebook.description"/></th>
					<th><spring:message code="general.date.short"/></th>
					<th><spring:message code="study.view.codebook.saver"/></th><th></th></tr>
				</thead>
				<tbody>
					<c:forEach items="${study.data.codebook.files}" var="file">
						<tr class="materialCodebookFileRow">
							<td class="materialCodebookFileName">${file.path}</td>
							<td>${file.title}</td>
							<td>${file.type}</td>
							<td>${file.description}</td>
							<td>${file.date}</td>
							<td>${file.saver}</td>
							<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>		
		</div>

		<div class="translationEn">
			<div class="materialRowTitle"><spring:message code="study.view.codebook.tableTitle"/></div>
			<table class="metkaTable" id="materialCodebookFileTable">
				<thead>
					<tr><th><spring:message code="study.view.codebook.file"/></th>
					<th><spring:message code="study.view.codebook.title"/></th>
					<th><spring:message code="study.view.codebook.type"/></th>
					<th><spring:message code="study.view.codebook.description"/></th>
					<th><spring:message code="general.date.short"/></th>
					<th><spring:message code="study.view.codebook.saver"/></th><th></th></tr>
				</thead>
				<tbody>
					<c:forEach items="${study.data.codebook.files}" var="file">
						<tr class="materialCodebookFileRow">
							<td class="materialCodebookFileName">${file.path}</td>
							<td>${file.title}</td>
							<td>${file.type}</td>
							<td>${file.description}</td>
							<td>${file.date}</td>
							<td>${file.saver}</td>
							<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png" /></a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<div class="materialTableActionLinkHolder"><spring:message code="general.buttons.add"/></div>		
		</div>
	</div>
	
	<jsp:include page="buttons.jsp"/>
</div>	