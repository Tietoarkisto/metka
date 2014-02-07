<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="relatedStudyDialog">
	<h1 class="pageTitle"><spring:message code="publication.view.addStudy.title"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.basic.number"/></label><input type="text" value=""/>
	</div>
	
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	