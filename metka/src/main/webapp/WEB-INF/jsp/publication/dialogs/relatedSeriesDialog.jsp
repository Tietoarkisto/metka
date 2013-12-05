<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="relatedSeriesDialog">
	<h1 class="pageTitle"><spring:message code="publication.view.addSeries.title"/></h1>
	<div class="popupRowHolder">
		<select><option>Valitse...</option><option>Yksittäiset aineistot</option><option>Sosiaalibarometrit</option><option></option></select>
	</div>
	
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	