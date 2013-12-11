<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="publicatinPersonDialog">
	<h1 class="pageTitle"><spring:message code="publication.view.addPerson.title"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.firstName"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.lastName"/></label><input type="text" value=""/>
	</div>
	
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>
</div>
		
