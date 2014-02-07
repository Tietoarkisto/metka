<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="timePeriodDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.coverage.addTimePeriod"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.date"/></label>
		<input type="text" class="datepicker"/><input type="text"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.coverage.timePeriodEvent"/></label>
		<select><option>Start</option><option>End</option></select>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>