<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div id="materialStudyLevel" class="tabs2 description">
	<div id="studyLevelTopRow">
		<input type="button" id="toggleAccordion" class="searchFormInput" value="<spring:message code='study.view.description.expandAll'/>" />
	</div>
	<div id="studyLevelData">
		<jsp:include page="otherNames.jsp"/>	
		<jsp:include page="authorsAndProducers.jsp"/>
		<jsp:include page="keywordsAndTopics.jsp"/>
		<jsp:include page="abstract.jsp"/>
		<jsp:include page="coverage.jsp"/>
		<jsp:include page="collecting.jsp"/>
		<jsp:include page="usage.jsp"/>
		<jsp:include page="otherStudies.jsp"/>
	</div>
	
	<jsp:include page="../buttons.jsp"/>
	
</div>