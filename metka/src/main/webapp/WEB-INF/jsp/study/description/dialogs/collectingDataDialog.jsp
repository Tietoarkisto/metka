<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer">
	<h1 class="pageTitle">Lis‰‰ havaintoyks./aikaulott./otantamen./keruumen./keruuv‰line (samantyyppinen kaikille)</h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.word"/></label>
		<select id="collSelect"><option id="chooseColl"><spring:message code="general.choose"/></option><option>Foo1</option><option>Foo2</option><option id="otherColl"><spring:message code="study.view.description.word.other"/></option></select>
	</div>
	<div class="popupRowHolder otherCollInput" style="display: none;">
		<label class="inputRowLabel"><spring:message code="study.view.description.word.otherWord"/></label><input style="width: 290px" type="text" value=""/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.uri"/></label><input style="width: 290px" type="text" value="http://www.foo.bar.fi" disabled="disabled" />
	</div>
	<div class="popupRowHolder wordFields" style="display: none;">
		<label class="inputRowLabel"><spring:message code="study.view.description.idOrAddress"/></label><input id="collectionId" style="width: 290px" type="text" value="FooBar" disabled="disabled" />
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	