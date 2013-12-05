<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="materialErrors" class="tabs2 errors">
	<%-- errorDialog --%>
	<div class="materialRowTitle"><img src="../images/info-sign.png" class="helpImage"/></div>
	<table class="metkaTable sortableTable" id="materialErrorsTable">
		<thead>
			<tr><th><spring:message code="general.date.short"/></th>
			<th><spring:message code="study.view.error.marker"/></th>
			<th><spring:message code="study.view.error.studySection"/></th>
			<th><spring:message code="study.view.error.description"/></th>
			<th><spring:message code="study.view.error.errorPoints"/></th>
			<th><spring:message code="study.view.error.markAsDone"/></th></tr>
		</thead>
		<tbody>
			<c:forEach items="${study.errors}" var="error">
				<tr class="materialErrorRow">
					<td>${error.date}</td>
					<td>${error.marker}</td>
					<td>${error.studySection}</td>
					<td>${error.shortDescription}</td>
					<td>${error.points}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.remove'/>" src="../images/cancel.png"/></a></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div class="materialTableActionLikHolder"><spring:message code="general.add"/></div>
	
	<jsp:include page="buttons.jsp"/>
</div>	