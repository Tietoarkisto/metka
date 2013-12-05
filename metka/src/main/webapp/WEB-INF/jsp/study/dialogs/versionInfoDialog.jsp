<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer">
	<h1 class="pageTitle"></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.version"/></label>
		<input type="text" value="2.1" readonly="readonly" />
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.date"/></label>
		<input type="text" class="datepicker" value="14.10.2013" readonly="readonly" />
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.handler"/></label>
		<input type="text" value="Mikko Tanskanen" readonly="readonly" />			
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.versions.shortDescription"/></label>
		<select id="errorTypeSelect" disabled="disabled">
			<option>Selite1</option>
			<option>Selite2</option>
			<option>Selite3</option>
			<option>Selite4</option>
			<option>Selite5</option>
		</select>
	</div>
	<div class="popupRowHolder translationFi">
		<label class="inputRowLabel"><spring:message code="general.versions.publicDescription"/></label>
		<textarea cols="42" rows="5" readonly="readonly"></textarea>
	</div>
	<div class="popupRowHolder translationSv">
		<label class="inputRowLabel"><spring:message code="general.versions.publicDescription"/></label>
		<textarea cols="42" rows="5"></textarea>
	</div>
	<div class="popupRowHolder translationEn">
		<label class="inputRowLabel"><spring:message code="general.versions.publicDescription"/></label>
		<textarea cols="42" rows="5"></textarea>
	</div>
	<div class="popupRowHolder translationFi">
		<label class="inputRowLabel"><spring:message code="general.versions.nonPublicDescription"/></label>
		<textarea cols="42" rows="5" readonly="readonly"></textarea>
	</div>
	<div class="popupRowHolder translationSv">
		<label class="inputRowLabel"><spring:message code="general.versions.nonPublicDescription"/></label>
		<textarea cols="42" rows="5"></textarea>
	</div>
	<div class="popupRowHolder translationEn">
		<label class="inputRowLabel"><spring:message code="general.versions.nonPublicDescription"/></label>
		<textarea cols="42" rows="5"></textarea>
	</div>
	<div class="popupButtonsHolder">				
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	