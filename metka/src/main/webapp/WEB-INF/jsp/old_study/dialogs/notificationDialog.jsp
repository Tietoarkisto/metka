<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="notificationDialog">
	<h1 class="pageTitle"><spring:message code="study.view.basic.addNotification"/></h1>
	<div class="popupRowHolder">
		<textarea rows="4" cols="100"></textarea>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	