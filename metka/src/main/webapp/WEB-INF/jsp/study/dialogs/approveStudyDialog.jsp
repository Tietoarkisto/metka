<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="approveStudyDialog">
	<h1 class="pageTitle"><spring:message code="study.view.approve.title"/></h1>
	<div class="popupRowHolder">
		<div class="approveLanguage"><input type="checkbox"><spring:message code="topMenu.language.finnish"/></div>
		<div class="approveLanguage"><input type="checkbox"><spring:message code="topMenu.language.english"/></div>
		<div class="approveLanguage"><input type="checkbox"><spring:message code="topMenu.language.swedish"/></div>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.approve.versionNumber"/></label>
		<input type="text"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.approve.description"/></label>
		<select id="errorTypeSelect">
			<option>Selite1</option>
			<option>Selite2</option>
			<option>Selite3</option>
			<option>Selite4</option>
			<option>Selite5</option>
		</select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.approve.publicDescription"/></label>
		<textarea></textarea>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.approve.nonPublicDescription"/></label>
		<textarea></textarea>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>