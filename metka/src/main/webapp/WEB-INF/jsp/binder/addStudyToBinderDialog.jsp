<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="addStudyToBinderDialog">
	<h1 class="page-header"><spring:message code="binders.addStudy.title"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="binders.table.study.number"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="binders.table.binder.number"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="binders.table.study.bindedMaterial"/></label><textarea rows="5"></textarea>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	