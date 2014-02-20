<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="countryDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.coverage.addCountry"/></h1>

	<div class="popupRowHolder">
		<label class="inputRowLabel"><a href="http://en.wikipedia.org/wiki/ISO_3166-1" target="_blank" title="ISO-3166-1"><spring:message code="study.view.description.coverage.country"/></a></label>
		<input type="text" value="" style="width: 290px;"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.abbreviation"/></label>
		<input type="text" value="" style="width: 290px;"/>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='study.view.description.coverage.addFinland'/>">
	</div>

</div>	