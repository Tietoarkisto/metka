<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="fileDialog">
	<h1 class="pageTitle"><spring:message code="study.view.files.addFile"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.path"/></label>
		<input type="text"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.description"/></label>
		<textarea></textarea>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.officialDescription"/></label>
		<textarea></textarea>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.unofficialDescription"/></label>
		<textarea></textarea>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.comment"/></label>
		<textarea></textarea>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.type"/></label>
		<select><option>Kyselylomake</option><option>Kirjoitusohjeet</option><option>Muu</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.pas"/></label>
		<select><option>Ei tietoa</option><option>Ei</option><option>Kyllä</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.language"/></label>
		<select><option>Suomi</option><option>Englanti</option><option>Ruotsi</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.origin"/></label>
		<select><option>Kyllä</option><option>Ei</option><option>Ei relevantti</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.www"/></label>
		<select><option>x</option><option>y</option><option>z</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.files.delivery"/></label>
		<select><option>Ei tietoa</option><option>Kyllä</option><option>Ei</option></select>
	</div>

	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>
</div>	