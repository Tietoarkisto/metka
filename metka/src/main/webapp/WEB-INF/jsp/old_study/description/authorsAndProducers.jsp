<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.authorsAndProducers"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="dataRow">
	<%-- authorDialog --%>
		<table class="metkaTable studyLevelOrderedTable" id="studyLevelAuthors">
			<thead>
				<tr><th><spring:message code="study.view.description.authorsAndProducers.author"/></th>
				<th><spring:message code="study.view.description.id"/></th>
				<th><spring:message code="study.view.description.id.type"/></th>
				<th><spring:message code="study.view.description.affiliation.abbr"/></th>
				<th><spring:message code="study.view.description.affiliation.id.abbr"/></th>
				<th><spring:message code="study.view.description.affiliation.id.type.abbr"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.authors}" var="author">
					<tr><td>${author.name}</td>
					<td>
						<c:forEach items="${author.ids}" var="id">
							${id.id}<br/>
						</c:forEach>
					</td>
					<td>
						<c:forEach items="${author.ids}" var="id">
							${id.type}<br/>
						</c:forEach>
					</td>
					<td>${author.affiliation.name}</td>
					<td>
						<c:forEach items="${author.affiliation.ids}" var="id">
							${id.id}<br/>
						</c:forEach>
					</td>
					<td>
						<c:forEach items="${author.affiliation.ids}" var="id">
							${id.type}<br/>
						</c:forEach>
					</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>					
				</c:forEach>
			</tbody>									
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>
	<div class="dataRow">
		<%-- authorDialog --%>
		<table class="metkaTable studyLevelOrderedTable" id="studyLevelOtherAuthors">
			<thead>
				<tr><th><spring:message code="study.view.description.authorsAndProducers.otherAuthor"/></th>
				<th><spring:message code="study.view.description.id"/></th>
				<th><spring:message code="study.view.description.id.type"/></th>
				<th><spring:message code="study.view.description.affiliation.abbr"/></th>
				<th><spring:message code="study.view.description.affiliation.id.abbr"/></th>
				<th><spring:message code="study.view.description.affiliation.id.type.abbr"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.otherAuthors}" var="author">
					<tr><td>${author.name}</td>
					<td>
						<c:forEach items="${author.ids}" var="id">
							${id.id}<br/>
						</c:forEach>
					</td>
					<td>
						<c:forEach items="${author.ids}" var="id">
							${id.type}<br/>
						</c:forEach>
					</td>
					<td>${author.affiliation.name}</td>
					<td>
						<c:forEach items="${author.affiliation.ids}" var="id">
							${id.id}<br/>
						</c:forEach>
					</td>
					<td>
						<c:forEach items="${author.affiliation.ids}" var="id">
							${id.type}<br/>
						</c:forEach>
					</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>					
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>

	<div class="dataRow">
		<%-- producerDialog --%>
		<table class="metkaTable studyLevelOrderedTable" id="studyLevelProducers">
			<thead>
				<tr><th><spring:message code="study.view.description.authorsAndProducers.producer"/></th>
				<th><spring:message code="study.view.description.id"/></th>
				<th><spring:message code="study.view.description.id.type"/></th>
				<th><spring:message code="study.view.description.role"/></th>
				<th><spring:message code="study.view.description.projectNumber"/></th>
				<th><spring:message code="general.abbreviation"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.producers}" var="producer">
					<tr><td>${producer.name}</td>
					<td>
						<c:forEach items="${producer.ids}" var="id">
							${id.id}<br/>
						</c:forEach>
					</td>
					<td>
						<c:forEach items="${producer.ids}" var="id">
							${id.type}<br/>
						</c:forEach>
					</td>
					<td>${producer.role}</td>
					<td>
						<c:forEach items="${producer.projectNumbers}" var="number">
							${number}<br/>
						</c:forEach>
					</td>
					<td>${producer.abbreviation}</td>
					<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>					
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>
</div>