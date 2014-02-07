<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="universeDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.coverage.addUniverse"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.coverage.addUniverse.name"/></label>
		<input type="text"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.coverage.universe.masking"/></label>
		<select><option>E</option><option>I</option></select>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	