<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="producerDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.authorsAndProducers.addProducer"/></h1>
	<div class="popupRowHolder personAuthor">
		<label class="inputRowLabel"><spring:message code="study.view.description.authorsAndProducers.addProducer.producer"/></label>
		<select id="organizationSelect"><option id="chooseOrganization">Valitse...</option><option>Organisaatio1</option><option>Organisaatio12</option><option>Organisaatio13</option><option id="otherOrganization">Muu...</option></select>
	</div>
	<div class="popupRowHolder newOrganizationField">
		<label class="inputRowLabel"><spring:message code="study.view.description.organization"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder newOrganizationField translationEn">
		<label class="inputRowLabel"><spring:message code="study.view.description.organization"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder newOrganizationField translationSv">
		<label class="inputRowLabel"><spring:message code="study.view.description.organization"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder organizationField">
		<label class="inputRowLabel"><spring:message code="general.abbreviation"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder organizationField">
		<label class="inputRowLabel"><spring:message code="study.view.description.id"/>&nbsp;<img title="Lisää" src="../css/images/add.png"/></label>
		<input type="text" value="" disabled="disabled"/>
		<select disabled="disabled"><option>Tyyppi1</option><option>Tyyppi2</option><option>Tyyppi3</option></select><img style="visibility: hidden;" title="Poista" src="../css/images/cancel.png"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.authorsAndProducers.projectNumber"/></label>
		<input type="text" value=""/><img style="visibility: hidden;" title="Poista" src="../css/images/cancel.png"/>
	</div>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="study.view.description.authorsAndProducers.role"/></label>
		<select><option>Rahoittaja</option><option>Projekti</option><option>Tyyppi3</option></select>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.cancel'/>" />
		<input type="button" class="searchFormInput" value="<spring:message code='general.buttons.save'/>" />
	</div>

</div>	