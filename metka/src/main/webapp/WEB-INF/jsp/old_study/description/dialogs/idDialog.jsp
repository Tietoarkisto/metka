<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="idDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.addId"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.id"/></label>
		<input type="text"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.idIssuer"/></label>
		<input type="text"/>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	