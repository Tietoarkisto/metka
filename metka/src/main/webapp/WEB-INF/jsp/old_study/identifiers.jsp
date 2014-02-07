<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="materialPackaging" class="tabs2 identifiers">
	<div class="materialRowTitle"><img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" >
		<thead>
			<tr><th><spring:message code="study.view.identifiers.output"/></th>
			<th><spring:message code="study.view.identifiers.version"/></th>
			<th><spring:message code="general.date"/></th>
			<th><spring:message code="study.view.identifiers.urn"/></th></tr>
		</thead>
		<tbody>
			<c:forEach items="${study.data.identifiers}" var="identifier">
				<tr class="packagingRow"><td>${identifier.name}</td><td>${identifier.version}</td><td>${identifier.modified}</td><td>${identifier.urn}</td></tr>
			</c:forEach>
		</tbody>
	</table>

	<div id="packagingHistoryContainer" style="display: none;">
		<div class="materialRowTitle"><spring:message code="study.view.identifiers.versionHistory" arguments="DDI-xml-fi"/></div>
		<table class="metkaTable sortableTable" id="materialPackagingInfoTable">
			<thead>
				<tr><th><spring:message code="study.view.identifiers.version"/></th>
				<th><spring:message code="general.handler"/></th><th><spring:message code="general.date"/></th></tr>
			</thead>
			<tbody>
				<%-- Tässä valitun historiatiedot --%>
				<tr><td>0.5</td><td>Mikko Tanskanen</td><td>2.10.2013</td></tr>
			</tbody>
		</table>
	</div>
	
	<jsp:include page="buttons.jsp"/>
	
</div>
			