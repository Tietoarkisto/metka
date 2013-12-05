<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="pidDialog">
	<h1 class="pageTitle"><spring:message code="publication.view.addPid.title"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="publication.view.pid"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="publication.view.pidType"/></label>
		<select><option>DOI</option><option>URN</option></select>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	