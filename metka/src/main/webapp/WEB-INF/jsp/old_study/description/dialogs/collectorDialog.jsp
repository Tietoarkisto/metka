<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="popupContainer" id="collectorDialog">
	<h1 class="pageTitle"><spring:message code="study.view.description.collecting.addCollector"/></h1>
	<div class="popupRowHolder">
		<label class="inputRowLabel"><spring:message code="general.type"/></label>
		<select id="authorTypeSelect"><option id="personCollector"><spring:message code="study.view.description.collecting.addCollector.person"/>
		</option><option id="organizationCollector"><spring:message code="study.view.description.collecting.addCollector.organization"/></option></select>
	</div>
	<div class="popupRowHolder personCollector">
		<label class="inputRowLabel"><spring:message code="general.name"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder personCollector">
		<label class="inputRowLabel"><spring:message code="study.view.description.collecting.addCollector.personId"/>&nbsp;<img title="Lisää" src="../css/images/add.png"/></label>
		<input type="text" value="" disabled="disabled"/>
		<select disabled="disabled"><option>Tyyppi1</option><option>Tyyppi2</option><option>Tyyppi3</option></select><img style="visibility: hidden;" title="Poista" src="../css/images/cancel.png"/>
	</div>
	<div class="popupRowHolder organizationCollector">
		<label class="inputRowLabel"><spring:message code="study.view.description.organization"/></label>
		<select class="organizationSelect"><option>Organisaatio1</option><option>Organisaatio12</option><option>Organisaatio13</option><option class="otherOrganization">Muu...</option></select>
	</div>
	<div class="popupRowHolder personCollector">
		<label class="inputRowLabel"><spring:message code="study.view.description.affiliation"/></label>
		<select class="organizationSelect"><option>Organisaatio1</option><option>Organisaatio12</option><option>Organisaatio13</option><option class="otherOrganization">Muu...</option></select>
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
	<div class="popupRowHolder newOrganizationField">
		<label class="inputRowLabel"><spring:message code="general.abbreviation"/></label><input type="text" value=""/>
	</div>
	<div class="popupRowHolder personCollector">
		<label class="inputRowLabel"><spring:message code="study.view.description.affiliation.id"/>&nbsp;<img title="Lisää" src="../css/images/add.png"/></label>
		<input type="text" value="" disabled="disabled"/>
		<select disabled="disabled"><option>Tyyppi1</option><option>Tyyppi2</option><option>Tyyppi3</option></select><img style="visibility: hidden;" title="Poista" src="../css/images/cancel.png"/>
	</div>
	<div class="popupRowHolder organizationCollector">
		<label class="inputRowLabel"><spring:message code="study.view.description.organization.id"/>&nbsp;<img title="Lisää" src="../css/images/add.png"/></label>
		<input type="text" value="" disabled="disabled"/>
		<select disabled="disabled"><option>Tyyppi1</option><option>Tyyppi2</option><option>Tyyppi3</option></select><img style="visibility: hidden;" title="Poista" src="../css/images/cancel.png"/>
	</div>
	<div class="popupButtonsHolder">
		<input type="button" class="searchFormInput" value="Peruuta">
		<input type="button" class="searchFormInput" value="OK">
	</div>

</div>	