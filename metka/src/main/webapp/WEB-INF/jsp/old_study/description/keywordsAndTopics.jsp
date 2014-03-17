<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.keywords"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>
<div class="accordionContent">
	<div class="dataRow">
		<%-- keywordDialog --%>
		<div class="studyLevelTableTitle"><spring:message code="study.view.description.keywords.keywords"/></div>
		<table class="metkaTable studyLevelVocabularyTable">
			<thead>
				<tr><th><spring:message code="study.view.description.keywords.vocabulary"/></th>
				<th><spring:message code="study.view.description.keywords.keyword"/></th>
				<th><spring:message code="study.view.description.uri"/></th>
				<th><spring:message code="study.view.description.keywords.idOrAddress"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.keywords}" var="keyword">
					<tr>
						<td>${keyword.vocabulary}</td>
						<td>${keyword.keyword}</td>
						<td>${keyword.uri}</td>
						<td>${keyword.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>
	<div class="dataRow">
		<%-- topicDialog --%>
		<div class="studyLevelTableTitle"><spring:message code="study.view.description.keywords.topics"/></div>
		<table class="metkaTable studyLevelVocabularyTable">
			<thead>
				<tr><th><spring:message code="study.view.description.keywords.vocabulary"/></th>
				<th><spring:message code="study.view.description.keywords.topic"/></th>
				<th><spring:message code="study.view.description.uri"/></th>
				<th><spring:message code="study.view.description.keywords.idOrAddress"/></th>
				<th></th></tr>
			</thead>
			<tbody>
				<c:forEach items="${study.data.description.topics}" var="topic">
					<tr>
						<td>${topic.vocabulary}</td>
						<td>${topic.branch}</td>
						<td>${topic.uri}</td>
						<td>${topic.idOrAddress}</td>
						<td><a class="removeRow" href="#"><img title="<spring:message code='general.buttons.remove'/>" src="../images/cancel.png"/></a></td></tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="accordionTableActionLinkHolder"><spring:message code='general.buttons.add'/></div>
	</div>						
</div>