<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<label class="studyLevelTitle"><spring:message code="study.view.description.abstract"/>&nbsp;<img src="../images/info-sign.png" class="helpImage"/></label>						
<div class="accordionContent">
	<div class="rowContainer">
		<div class="studyLevelDataSetTextareaContainer">
			<label><spring:message code="study.view.description.abstract.summary"/></label>
			<textarea></textarea>
		</div>

		<div class="studyLevelDataSetTextareaContainer">
			<label><spring:message code="study.view.description.abstract.referenceInfo"/></label>
			<textarea></textarea>
		</div>
	</div>
</div>