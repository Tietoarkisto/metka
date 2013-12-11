<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="keywordDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.keywords.addKeyword"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.vocabulary"/></label>
		<select id="vocabularySelect"><option id="chooseVocabulary"><spring:message code="study.view.description.vocabulary.choose"/></option><option>YSA</option><option>ELSST</option><option id="noVocabulary"><spring:message code="study.view.description.noVocabulary"/></option></select>
	</div>
	<div id="keywordSelectContainer" class="popupRowHolder vocabularyFields">
		<label class="inputRowLabel"><spring:message code="study.view.description.keywords.keyword"/></label>
		<select id="keywordSelect" multiple><option id="chooseKeyword"><spring:message code="study.view.description.keywords.addKeyword.choose"/></option><option>Sana1</option><option>Sana2</option></select>
	</div>
	<div class="popupRowHolder otherKeywordInput" style="display: none;">
		<label class="inputRowLabel"><spring:message code="study.view.description.keywords.addKeyword.other"/></label>
		<input style="width: 290px" type="text" value=""/>
	</div>
	<div class="popupRowHolder vocabularyFields">
		<label class="inputRowLabel"><spring:message code="study.view.description.uri"/></label><input style="width: 290px" type="text" value="http://www.foo.bar.fi" disabled="disabled" />
	</div>
	<div class="popupRowHolder keywordFields" style="display: none;">
		<label class="inputRowLabel"><spring:message code="study.view.description.idOrAddress"/></label><input style="width: 290px" type="text" value="FooBar" disabled="disabled" />
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	