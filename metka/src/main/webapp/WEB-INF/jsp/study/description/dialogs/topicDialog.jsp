<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="topicDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.keywords.addTopic"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.vocabulary"/></label>
		<select id="vocabularySelect"><option id="chooseVocabulary"><spring:message code="study.view.description.vocabulary.choose"/></option><option>FSD</option><option>CESSDA</option></select>
	</div>
	<div class="popupRowHolder vocabularyFields">
		<label class="inputRowLabel"><spring:message code="study.view.description.keywords.topic"/></label>
		<select id="topicSelect" multiple><option id="chooseTopic"><spring:message code="study.view.description.keywords.addTopic.choose"/></option><option>Tieteenala1</option><option>Tieteenala12</option><option>Tieteenala13</option></select>
	</div>
	<div class="popupRowHolder vocabularyFields">
		<label class="inputRowLabel"><spring:message code="study.view.description.uri"/></label><input style="width: 290px" type="text" value="http://www.foo.bar.fi" disabled="disabled" />
	</div>
	<div class="popupRowHolder topicFields">
		<label class="inputRowLabel"><spring:message code="study.view.description.idOrAddress"/></label><input style="width: 290px" type="text" value="FooBar" disabled="disabled" />
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	