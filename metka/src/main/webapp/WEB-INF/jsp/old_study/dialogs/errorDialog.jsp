<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="errorDialog">
	<h1 class="pageTitle"><spring:message code="study.view.errors.addError"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.points"/></label>
		<select><option>1</option><option>2</option><option>3</option><option>4</option><option>5</option></select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.part"/></label>
		<select id="errorTypeSelect">
			<option id="basicError"><spring:message code="study.view.navi.basicInformation"/></option>
			<option id="filingError"><spring:message code="study.view.navi.depositAgreement"/></option>
			<option id="studyLevelError"><spring:message code="study.view.navi.description"/></option>
			<option id="variableError"><spring:message code="study.view.navi.variables"/></option>
			<option id="fileError"><spring:message code="study.view.navi.files"/></option>
			<option id="codeBookError"><spring:message code="study.view.navi.codebook"/></option>
			<option id="identifierError"><spring:message code="study.view.navi.identifiers"/></option>
		</select>
	</div>
	<div class="popupRowHolder" id="fileNameErrorRow">
		<label class="inputRowLabel" class="inputRowLabel"><spring:message code="study.view.errors.addError.file"/></label>
		<select>
			<c:forEach items="${study.data.files}" var="file">
				<option>${file.path}</option>
			</c:forEach>
		</select>
	</div>
	<div class="popupRowHolder" id="studyLevelErrorRow">
		<label class="inputRowLabel" class="inputRowLabel"><spring:message code="study.view.errors.addError.section"/></label>
		<select>
			<option><spring:message code="study.view.description.otherName"/></option>
			<option><spring:message code="study.view.description.authorsAndProducers"/></option>
			<option><spring:message code="study.view.description.keywords"/></option>
			<option><spring:message code="study.view.description.abstract"/></option>
			<option><spring:message code="study.view.description.coverage"/></option>
			<option><spring:message code="study.view.description.collecting"/></option>
			<option><spring:message code="study.view.description.usage"/></option>
			<option><spring:message code="study.view.description.otherStudies"/></option>
		</select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel" class="inputRowLabel"><spring:message code="study.view.errors.addError.language"/></label>
		<select>
			<option><spring:message code="topMenu.language.finnish"/></option>
			<option><spring:message code="topMenu.language.english"/></option>
			<option><spring:message code="topMenu.language.swedish"/></option>
		</select>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.description"/></label>
		<input type="text" />			
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.extensiceDescription"/></label>
		<textarea cols="42" rows="5"></textarea>
	</div>
		<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.triggerDate"/></label>
		<input type="text" class="datepicker"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.errors.addError.triggerReceiver"/></label>
		<select>
			<option>Mikko Tanskanen</option>
		</select>
	</div>
	<div class="popupButtonsHolder">				
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	